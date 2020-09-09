
FROM openjdk:8
EXPOSE 5222
RUN mkdir /cph
WORKDIR /cph
COPY ./target/vysper-wrapper-1.0-SNAPSHOT.jar /cph/vysper-wrapper.jar


ENTRYPOINT ["java","-jar","vysper-wrapper.jar"]