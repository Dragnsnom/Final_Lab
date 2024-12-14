### Runtime  stage
FROM registry.astondevs.ru/eclipse-temurin:17-jdk AS runtime
LABEL "user-service"="user-service"
LABEL description="com.example.user-service"
USER root
# Set timezone
ARG DEBIAN_FRONTEND=noninteractive
ENV TZ=Europe/Moscow
RUN apt-get update \
    && apt-get install -y tzdata \
    && apt-get clean
# Create the home directory for the new app user.
RUN mkdir -p /home/app
# Create an app user so our program doesn't run as root.
RUN groupadd -r app && useradd -r -g app -d /home/app -s /sbin/nologin -c "Docker image user" app
# Set the home directory to our app user's home.
ENV HOME=/home/app
ENV APP=user-service-0.0.1-SNAPSHOT.jar
ENV APP_HOME=/home/app/user-service
## SETTING UP THE APP ##
RUN mkdir -p $APP_HOME
WORKDIR $APP_HOME
# Chown all the files to the app user.
RUN chown -R app:app $APP_HOME
# Change to the app user.
USER app:app
# Copy in the application code.
COPY ./target/$APP $APP_HOME/$APP

ENTRYPOINT exec java -Djava.awt.headless=true -jar $APP_HOME/$APP
EXPOSE 8080
