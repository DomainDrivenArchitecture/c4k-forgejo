# Upgrading process

## adhoc (on kubernetes cluster)

Ssh into your kubernetes cluster running the forgejo instance.  

``` bash
kubectl edit configmap forgejo-env
# make sure INSTALL_LOCK under security is set to true to disable the installation screen
# save and exit
kubectl edit deployments forgejo
# search for your current forgejo version, e.g. 1.17.0
# replace with new version
# save and exit
kubectl scale deployment forgejo --replicas=0
kubectl scale deployment forgejo --replicas=1
```

Logging into the admin account should now show the new version.

You may want to update your c4k-forgejo resources to reflect the changes made on the cluster.