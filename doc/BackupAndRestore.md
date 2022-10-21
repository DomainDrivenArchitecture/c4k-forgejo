# Backup Architecture details

![](backup.svg)

* we use restic to produce small & encrypted backups
* backup is scheduled at `schedule: "10 23 * * *"`
* Gitea stores files in `/data/gitea` and `/data/git/repositories`, these files are backed up. 
* The postgres db is also backed up

## Manual init the restic repository for the first time

1. apply backup-and-restore pod:   
   `kubectl scale deployment backup-restore --replicas=1`
2. exec into pod and execute restore pod (press tab to get your exact pod name)   
   `kubectl exec -it backup-restore-... -- /usr/local/bin/init.sh`
3. remove backup-and-restore pod:   
   `kubectl scale deployment backup-restore --replicas=0`


## Manual backup the restic repository for the first time

1. apply backup-and-restore pod:   
  `kubectl scale deployment backup-restore --replicas=1`
2. exec into pod and execute backup pod (press tab to get your exact pod name)   
   `kubectl exec -it backup-restore-... -- /usr/local/bin/backup.sh`
3. remove backup-and-restore pod:   
   `kubectl scale deployment backup-restore --replicas=0`


## Manual restore

1. apply backup-and-restore pod:   
  `kubectl scale deployment backup-restore --replicas=1`
2. Scale down gitea deployment:
   `kubectl scale deployment gitea --replicas=0`
3. exec into pod and execute restore pod (press tab to get your exact pod name)   
   `kubectl exec -it backup-restore-... -- /usr/local/bin/restore.sh`
4. Start gitea again:
   `kubectl scale deployment gitea --replicas=1`
5. remove backup-and-restore pod:   
   `kubectl scale deployment backup-restore --replicas=0`
