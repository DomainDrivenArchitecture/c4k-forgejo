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

## cljs / js-dev setup

```
sudo apt install npm
sudo npm install -g npx

# maybe
sudo npm install -g shadow-cljs

# in project root to retrieve all dependencies
npm install --ignore-scripts
npx shadow-cljs compile test
```

### create frontend script

```
npx shadow-cljs release frontend
```

## graalvm-setup

```
curl -LO  https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-21.0.0.2/graalvm-ce-java11-linux-amd64-21.0.0.2.tar.gz 

# unpack
tar -xzf graalvm-ce-java11-linux-amd64-21.0.0.2.tar.gz 

sudo mv graalvm-ce-java11-21.0.0.2 /usr/lib/jvm/
sudo ln -s /usr/lib/jvm/graalvm-ce-java11-21.0.0.2 /usr/lib/jvm/graalvm
sudo ln -s /usr/lib/jvm/graalvm/bin/gu /usr/local/bin
sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/graalvm/bin/java 2
sudo update-alternatives --config java

# install native-image in graalvm-ce-java11-linux-amd64-21.0.0.2/bin
sudo gu install native-image
sudo ln -s /usr/lib/jvm/graalvm/bin/native-image /usr/local/bin

# deps
sudo apt-get install build-essential libz-dev zlib1g-dev

# build
cd ~/repo/dda/c4k-cloud
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
