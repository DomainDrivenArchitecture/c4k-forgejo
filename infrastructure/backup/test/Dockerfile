FROM c4k-forgejo-backup:latest

# install it
RUN apt update && apt install -qqy openjdk-17-jre-headless
ADD resources /tmp/
RUN rm -rf /root/.m2
RUN /tmp/install-test.bb
