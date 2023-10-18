#Build stage
FROM docker.io/library/golang:1.21-alpine3.18 AS build-env

ARG GOPROXY
ENV GOPROXY ${GOPROXY:-direct}

#ARG GITEA_VERSION
ARG TAGS="sqlite sqlite_unlock_notify"
ENV TAGS "bindata timetzdata $TAGS"
ARG CGO_EXTRA_CFLAGS

ENV FORGEJO_GIT_URL "https://codeberg.org/forgejo/forgejo.git"
#ENV FORGEJO_GIT_URL "https://git.exozy.me/a/gitea.git"
ENV FORGEJO_BRANCH "forgejo-development"
#ENV FORGEJO_BRANCH "libreplanet-federation-demo"

#Build deps
RUN apk --no-cache add build-base git nodejs npm

#Setup repo
RUN git clone --single-branch --branch ${FORGEJO_BRANCH} ${FORGEJO_GIT_URL} ${GOPATH}/src/code.gitea.io/gitea 

WORKDIR ${GOPATH}/src/code.gitea.io/gitea

#Checkout version if set
RUN make clean-all build

# Begin env-to-ini build
RUN go build contrib/environment-to-ini/environment-to-ini.go

# Run stage
FROM docker.io/library/alpine:3.18
LABEL maintainer="contact@forgejo.org"

EXPOSE 22 3000

RUN apk -U upgrade
RUN apk cache clean
RUN apk --no-cache add \
    bash \
    ca-certificates \
    curl \
    gettext \
    git \
    linux-pam \
    openssh \
    s6 \
    sqlite \
    su-exec \
    gnupg

RUN addgroup \
    -S -g 1000 \
    git && \
  adduser \
    -S -H -D \
    -h /data/git \
    -s /bin/bash \
    -u 1000 \
    -G git \
    git && \
  echo "git:*" | chpasswd -e

ENV USER git
ENV GITEA_CUSTOM /data/gitea

VOLUME ["/data"]

ENTRYPOINT ["/usr/bin/entrypoint"]
CMD ["/bin/s6-svscan", "/etc/s6"]

COPY --from=build-env /go/src/code.gitea.io/gitea/docker/root /
COPY --from=build-env /go/src/code.gitea.io/gitea/gitea /app/gitea/gitea
COPY --from=build-env /go/src/code.gitea.io/gitea/environment-to-ini /usr/local/bin/environment-to-ini
COPY --from=build-env /go/src/code.gitea.io/gitea/contrib/autocompletion/bash_autocomplete /etc/profile.d/gitea_bash_autocomplete.sh
RUN chmod 755 /usr/bin/entrypoint /app/gitea/gitea /usr/local/bin/gitea /usr/local/bin/environment-to-ini
RUN chmod 755 /etc/s6/gitea/* /etc/s6/openssh/* /etc/s6/.s6-svscan/*
RUN chmod 644 /etc/profile.d/gitea_bash_autocomplete.sh
