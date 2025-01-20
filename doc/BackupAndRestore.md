# Backup Architecture details

* we use restic to produce small & encrypted backups
* backup is scheduled at `schedule: "10 23 * * *"`
* Forgejo stores files in `/data/gitea` and `/data/git/repositories`, these files are backed up. 
* The postgres db is also backed up

## Manual backup

1. Scale down forgejo deployment:   
   `kubectl -n forgejo scale deployment forgejo --replicas=0`
2. apply backup-and-restore pod:   
  `kubectl -n forgejo scale deployment backup-restore --replicas=1`
3. exec into pod and execute backup pod (press tab to get your exact pod name)   
   `kubectl -n forgejo exec -it backup-restore-... -- /usr/local/bin/backup.bb`
4. remove backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=0`
5. Scale up forgejo deployment:   
   `kubectl -n forgejo scale deployment forgejo --replicas=1`

## Manual restore

1. Scale down forgejo deployment:  
   `kubectl -n forgejo scale deployment forgejo --replicas=0`
2. apply backup-and-restore pod:   
  `kubectl -n forgejo scale deployment backup-restore --replicas=1`
3. exec into pod and execute restore pod (press tab to get your exact pod name):   
   `kubectl -n forgejo exec -it backup-restore-... -- /usr/local/bin/restore.bb`
4. remove backup-and-restore pod:   
   `kubectl -n forgejo scale deployment backup-restore --replicas=0`
5. start forgejo again:  
   `kubectl -n forgejo scale deployment forgejo --replicas=1`

## Change Password

1. Check restic-new-password env is set in backup deployment   
   ```
   kind: Deployment
   metadata:
     name: backup-restore
   spec:
     ...
       spec:
         containers:
         - name: backup-app
           env:
           - name: RESTIC_NEW_PASSWORD_FILE
             value: /var/run/secrets/backup-secrets/restic-new-password
   ```
2. Add restic-new-password to secret   
   ```
   kind: Secret
   metadata:
     name: backup-secret
   data:
     restic-password: old
     restic-new-password: new
   ```
3. Scale backup-restore deployment up:   
   `kubectl -n nextcloud scale deployment backup-restore --replicas=1`
4. exec into pod and execute restore pod   
   `kubectl -n nextcloud exec -it backup-restore -- change-password.bb`
5. Scale backup-restore deployment down:   
  `kubectl -n nextcloud scale deployment backup-restore --replicas=0`
6. Replace restic-password with restic-new-password in secret   
   ```
   kind: Secret
   metadata:
     name: backup-secret
   data:
     restic-password: new
   ```
