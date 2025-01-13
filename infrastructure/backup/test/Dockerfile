FROM c4k-forgejo-backup:latest

ADD resources /tmp/
RUN RESTIC_PASSWORD_FILE=/tmp/file_password RESTIC_NEW_PASSWORD_FILE=/tmp/new_file_password RESTIC_REPOSITORY=/tmp/restic-repo POSTGRES_SERVICE=dummy POSTGRES_PORT=dummy POSTGRES_DB=dummy POSTGRES_USER=dummy POSTGRES_PASSWORD=dummy AWS_ACCESS_KEY_ID=dummy AWS_SECRET_ACCESS_KEY=dummy /tmp/test.bb
