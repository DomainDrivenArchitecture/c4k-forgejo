# Release process

## adhoc (on kubernetes cluster)

Make sure you've got your gitea admin credentials.

``` bash
kubectl edit deployments [website-pod-deployment-name]
# search for your current gitea version, e.g. 1.17.0
# replace with new version
# save and exit
kubectl scale deployment meissa-io-deployment --replicas=0
kubectl scale deployment meissa-io-deployment --replicas=1
```

Visit your gitea url.  
The inital installation screen should be visible.  
Enter your admin credentials.

You now should be logged into the admin account with all repos visible.

You may want to update your c4k-gitea resources to reflect the changes made on the cluster.
