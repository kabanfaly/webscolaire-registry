package com.webscolaire.registry.mapper;

import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WsRegisterMapper extends EntityMapper<WsRegisterDto, WsRegister> {}
