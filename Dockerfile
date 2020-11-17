FROM alpine:edge
MAINTAINER CSC
RUN apk add --no-cache openjdk11
COPY ./target/ncp-0.0.1-SNAPSHOT.war /opt/fi_ncp/lib/
COPY ./fi_ncp_entrypoint.sh /opt/fi_ncp/bin/
ENV SPRING_APPLICATION_JSON='{"spring": "test_json"}'
ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "/opt/fi_ncp/lib/ncp-0.0.1-SNAPSHOT.war"]
VOLUME /var/lib/fi_ncp_server
EXPOSE 9001
