#!/bin/bash

if which tput >/dev/null 2>&1; then
   ncolors=$(tput colors)
fi
if [ -t 1 ] && [ -n "$ncolors" ] && [ "$ncolors" -ge 8 ]; then
  RED="$(tput setaf 1)"
  GREEN="$(tput setaf 2)"
  YELLOW="$(tput setaf 3)"
  BLUE="$(tput setaf 4)"
  BOLD="$(tput bold)"
  NORMAL="$(tput sgr0)"
else
  RED=""
  GREEN=""
  YELLOW=""
  BLUE=""
  BOLD=""
  NORMAL=""
fi


function usage {
  printf "Usage:\n"
  printf "${BLUE}\t ./deploy.sh ${YELLOW}NOM_DOSSIER_ZIP$ ${GREEN}NOM_FICHIER_WAR {NORMAL}\n"
  printf "${YELLOW}\t\t - NOM_DOSSIER_ZIP : ${NORMAL} Nom de l'archive zip contenant le service war a déployer.\n"
  printf "${GREEN}\t\t - NOM_FICHIER_WAR : ${NORMAL} Nom du fichier war à déployer.\n"
  printf "${BOLD}\t Exemple : ./deploy.sh Delivery-2017-09-25.zip jhipster-0.0.1-SNAPSHOT.war ${NORMAL}\n"
}

if [ $# -eq 0 ]; then
  usage
  exit
fi

# Date du jour
DATE=`date +%Y-%m-%d_%H%M%S`

# Vérification des parametres
if [ $# -ne 2 ]; then
 printf "${RED}Nombre d'arguments est incorrect. Le nom du fichier zip est manquant.${NORMAL}\n"
 usage
 exit
fi

if "$1" == "help"; then
  usage
fi

function getFileExtension {
  filename=$(basename -- "$1")
  echo "${filename##*.}"
}

function getFileName {
  filename=$(basename -- "$1")
  echo ${filename%.*}
}

# Arrêt du service ws-registry
printf "${NORMAL}Arrêt du service ws-registry ...\n"
sudo /etc/init.d/ws-registry stop 2>/dev/null
printf "${GREEN}Arrêt du service ws-registry OK\n"

PROJECT_ZIP_FILE=$1
WAR_FILE_NAME=$2
INSTALL_DIR=/workspace/webscolaire-registry

PROJECT_ZIP_FILE_EXT=`getFileExtension $PROJECT_ZIP_FILE`
WAR_FILE_NAME_EXT=`getFileExtension $WAR_FILE_NAME`

# Vérification de l'extension du fichier
if [ "$PROJECT_ZIP_FILE_EXT" != "zip" ]; then
  printf "${RED}Le premier argument doit être un fichier zip contant le '.war' à déployer!${NORMAL}\n"
  usage
  exit
fi

if [ "$WAR_FILE_NAME_EXT" != "war" ]; then
  printf "${RED}Le deuxième argument doit être le nom du fichier '.war' à déployer!${NORMAL}\n"
  usage
  exit
fi


# -- Nom répertoire du projet après décompression
PROJECT_DIR=`getFileName $PROJECT_ZIP_FILE`

# Backup du service existant s'il s'agit du même service
TMP=/$INSTALL_DIR/tmp

sudo mkdir $TMP 2>/dev/null
if [ -d "/$INSTALL_DIR/$PROJECT_DIR" ]; then
  BACKUP=$TMP/${PROJECT_DIR}__BK_${DATE}
  printf "${GREEN}Renommage du Dossier existant '/$INSTALL_DIR/$PROJECT_DIR' en '$BACKUP'.${NORMAL}\n"
  sudo mv /$INSTALL_DIR/$PROJECT_DIR $BACKUP
fi

# Décompression du fichier
unzip $PROJECT_ZIP_FILE -d /$INSTALL_DIR/ >/dev/null 2>&1
if [ -d "/$INSTALL_DIR/$PROJECT_DIR" ]; then
  printf "${GREEN}Décompression du fichier OK.${NORMAL}\n"
else
  printf "${RED}Erreur lors de la décompression du fichier $PROJECT_ZIP_FILE ${NORMAL}.\n"
  exit
fi


# Création des lien symboliques
sudo unlink /$INSTALL_DIR/current_delivery >/dev/null 2>&1
sudo ln -s /$INSTALL_DIR/$PROJECT_DIR /$INSTALL_DIR/current_delivery
sudo chmod +x /$INSTALL_DIR/current_delivery/$WAR_FILE_NAME
sudo ln -sf /$INSTALL_DIR/current_delivery/$WAR_FILE_NAME /$INSTALL_DIR/current_ws_registry.war
sudo ln -sf /$INSTALL_DIR/config /$INSTALL_DIR/current_delivery

# Création du repertoire logs
LOG_DIR=/$INSTALL_DIR/current_delivery/logs
sudo mkdir $LOG_DIR
sudo chmod a+w $LOG_DIR
sudo ln -sf $LOG_DIR /$INSTALL_DIR

# Création de la configuration
APP_CONFIG_NAME="/$INSTALL_DIR/current_delivery/${PROJECT_DIR}.conf"
cp /$INSTALL_DIR/etc/service_conf_tpl.conf $APP_CONFIG_NAME

# Démarrage du service ws-registry
printf "${GREEN}Démarrage du service ws-registry\n"
sudo ln -sf /$INSTALL_DIR/current_ws_registry.war /etc/init.d/ws-registry
sudo /etc/init.d/ws-registry start 2>/dev/null

printf "\n${NORMAL}"
