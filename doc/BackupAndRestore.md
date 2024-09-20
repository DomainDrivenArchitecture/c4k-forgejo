# Backup Architecture details

![](backup.svg)

* we use restic to produce small & encrypted backups
* backup is scheduled at `schedule: "10 23 * * *"`
* Forgejo stores files in `/data/gitea` and `/data/git/repositories`, these files are backed up. 
* The postgres db is also backed up

## Manual init the restic repository for the first time

1. apply backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=1`
2. exec into pod and execute restore pod (press tab to get your exact pod name)   
   `kubectl -n forgejo exec -it backup-restore-... -- /usr/local/bin/init.bb`
3. remove backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=0`


## Manual backup the restic repository for the first time

1. apply backup-and-restore pod:   
  `kubectl -n forgejo scale deployment backup-restore --replicas=1`
2. exec into pod and execute backup pod (press tab to get your exact pod name)   
   `kubectl -n forgejo exec -it backup-restore-... -- /usr/local/bin/backup.bb`
3. remove backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=0`


## Manual restore

1. apply backup-and-restore pod:   
  `kubectl -n forgejo scale deployment backup-restore --replicas=1`
2. Scale down forgejo deployment:
   `kubectl -n forgejo scale deployment forgejo --replicas=0`
3. exec into pod and execute restore pod (press tab to get your exact pod name)   
   `kubectl -n forgejo exec -it backup-restore-... -- /usr/local/bin/restore.bb`
4. Start forgejo again:
   `kubectl -n forgejo scale deployment forgejo --replicas=1`
5. remove backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=0`
