FROM java:8-jdk-alpine
RUN sh -c 'mkdir /usr/app'
COPY ./target/juke-box-0.0.1-SNAPSHOT.jar /usr/app/
WORKDIR /usr/app
RUN sh -c 'touch juke-box-0.0.1-SNAPSHOT.jar'
ENTRYPOINT ["java","-jar","juke-box-0.0.1-SNAPSHOT.jar"]
						  