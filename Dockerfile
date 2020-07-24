FROM openjdk:8
COPY build/libs/sds-*.jar /usr/src/sds/sds.jar
WORKDIR /usr/src/sds

EXPOSE 16211/tcp
EXPOSE 16200/udp
EXPOSE 16221-16285/udp

CMD ["java", "-jar", "sds.jar"]