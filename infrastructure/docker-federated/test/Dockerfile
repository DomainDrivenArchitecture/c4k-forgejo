FROM c4k-forgejo-fed

RUN apk --no-cache add openjdk11-jre-headless

RUN curl -L -o /tmp/serverspec.jar \
    https://github.com/DomainDrivenArchitecture/dda-serverspec-crate/releases/download/2.0.0/dda-serverspec-standalone.jar

COPY serverspec.edn /tmp/serverspec.edn

RUN java -jar /tmp/serverspec.jar /tmp/serverspec.edn -v
