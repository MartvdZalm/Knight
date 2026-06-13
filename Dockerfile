# Build stage
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

COPY pom.xml .
COPY eclipse-java-style.xml .
COPY src ./src

RUN apt-get update \
	&& apt-get install -y --no-install-recommends maven \
	&& mvn -B package -DskipTests \
	&& apt-get purge -y maven \
	&& apt-get autoremove -y \
	&& rm -rf /var/lib/apt/lists/*

# Runtime stage
FROM eclipse-temurin:21-jre

RUN apt-get update \
	&& apt-get install -y --no-install-recommends g++ \
	&& rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /build/target/knight-1.0-SNAPSHOT.jar /app/knight.jar
COPY share ./share

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/knight.jar"]
