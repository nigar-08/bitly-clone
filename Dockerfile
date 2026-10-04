FROM node:22-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend ./
ENV VITE_BACKEND_URL=
ENV VITE_REACT_FRONT_END_URL=
RUN npm run build

FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app
COPY backend/mvnw ./
COPY backend/.mvn/ .mvn/
COPY backend/pom.xml ./
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
