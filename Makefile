image:
	./mvnw spring-boot:build-image
run:
	docker-compose -f src/main/docker/app.yml up -d
stop:
	docker-compose -f src/main/docker/app.yml down
logs:
	docker-compose -f src/main/docker/app.yml logs -f
