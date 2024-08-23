FROM c4k-forgejo-backup:latest

ADD resources /tmp/
RUN ENV_PASSWORD=env-password FILE_PASSWORD_FILE=/tmp/file_password /tmp/test.bb
