# Project Setup

## clj setup

### install leiningen
```
sudo apt install leiningen
```
or manually using Instructions on https://leiningen.org/#install

### install vscode + extensions
```
sudo snap install code
```
or with packages from https://code.visualstudio.com/Download

install extension "Calva: Clojure & ClojureScript Interactive Programming"

## graalvm-setup

```
curl -LO  https://github.com/graalvm/graalvm-ce-builds/releases/download/jdk-21.0.2/graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz

# unpack
tar -xzf graalvm-community-jdk-21.0.2_linux-x64_bin.tar.gz

sudo mv graalvm-community-openjdk-21.0.2+13.1 /usr/lib/jvm/
sudo ln -s /usr/lib/jvm/graalvm-community-openjdk-21.0.2+13.1 /usr/lib/jvm/graalvm-21
sudo ln -s /usr/lib/jvm/graalvm-21/bin/gu /usr/local/bin
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/graalvm-21/bin/java 2
sudo update-alternatives --config java
sudo ln -s /usr/lib/jvm/graalvm-21/bin/native-image /usr/local/bin

# deps
sudo apt-get install build-essential zlib1g-dev

# build
cd ~/repo/c4k/c4k-forgejo
lein uberjar
mkdir -p target/graalvm
lein native

# execute
./target/graalvm/c4k-cloud -h
./target/graalvm/c4k-cloud src/test/resources/valid-config.edn src/test/resources/valid-auth.edn 
./target/graalvm/c4k-cloud src/test/resources/invalid-config.edn src/test/resources/invalid-auth.edn
```

## c4k-setup
### install kubectl

```
sudo -i
curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | apt-key add -
echo "deb http://apt.kubernetes.io/ kubernetes-xenial main" \
  | tee -a /etc/apt/sources.list.d/kubernetes.list
apt update && apt install kubectl
kubectl completion bash >> /etc/bash_completion.d/kubernetes
```

### install kubeconform

```
curl -Lo /tmp/kubeconform.tar.gz https://github.com/yannh/kubeconform/releases/download/v0.4.7/kubeconform-linux-amd64.tar.gz
tar -xf /tmp/kubeconform.tar.gz
sudo cp kubeconform /usr/local/bin
```

### remote access to c4k

```
scp -r root@devops.test.meissa-gmbh.de:/home/c4k/.kube ~/
ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@devops.test.meissa-gmbh.de -L 8002:localhost:8002 -L 6443:192.168.5.1:6443

# add in /etc/hosts "127.0.0.1 kubernetes"

# change in ~/.kube/config 192.168.5.1 -> kubernetes

kubectl get pods
```

### deploy cloud

```
java -jar target/uberjar/c4k-cloud-standalone.jar valid-config.edn valid-auth.edn | kubeconform --kubernetes-version 1.19.0 --strict --skip Certificate -
java -jar target/uberjar/c4k-cloud-standalone.jar valid-config.edn my-auth.edn | kubectl apply -f -
```
