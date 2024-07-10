# Playbook Upgrade from 1.19 to 7.0.5

## Info: Relevant Breaking Changes:

* 1.19.3:Current version
* 1.20.1-0: Breaking https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-20-1-0
* 1.21.1-0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-21-1-0
* 7.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#7-0-0

## Preparations

1. Stop Forgejo Prod: `k scale deployment forgejo --replicas=0`
1. Disable Backup Cron: `k patch cronjobs forgejo-backup -p '{"spec" : {"suspend" : true }}'`
1. Scale up Backup-Restore Deployment: `kubectl scale deployment backup-restore --replicas=1`
1. Execute Manual Backup: `kubectl exec -it backup-restore-... -- /usr/local/bin/backup.sh`

### Create 2nd Repo Prod Server

1. Terraform Preparations for 2nd Server: TODO
1. Install c4k-forgejo Version TODO   
   with config `"forgejo-image-version-overwrite": "1.19.3-0"`
1. Stop Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Disable Backup Cron: `k patch cronjobs forgejo-backup -p '{"spec" : {"suspend" : true }}'`
1. Scale up Backup-Restore Deployment: `kubectl scale deployment backup-restore --replicas=1`
1. Restore Forgejo Backup: See [BackupAndRestore.md](BackupAndRestore.md)
1. Check for `..._INSTALL_LOCK: true` in ConfigMap `forgejo-env`
1. Scale up Forgejo Deployment and check for (startup) problems: `k scale deployment forgejo --replicas=1`

## Upgrade to 1.20.1-0

1. Scale down Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Adjust configmap: `k edit cm forgejo-env`
    1. Remove `FORGEJO__database__CHARSET: utf8` (This was a misconfiguration, since this option only had effect for mysql dbs)
    1. Change `FORGEJO__mailer__MAILER_TYPE: smtp+startls` TO `FORGEJO__mailer__PROTOCOL: smtp+starttls` (Missed deprecation from 1.19)
    1. Change `FORGEJO__service__EMAIL_DOMAIN_WHITELIST: repo.test.meissa.de` TO `FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST: repo.test.meissa.de` (Fallback deprecation in 1.21)
1. Delete app.ini: `k exec -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `1.20.1-0` with `k edit deployment forgejo`
1. Scale up Forgejo Deployment: `k scale deployment forgejo --replicas=1`
1. Check for errors

## Upgrade to 1.21.1-0

1. Scale down Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Delete app.ini: `k exec -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `1.21.1-0` with `k edit deployment forgejo`
1. Scale up Forgejo Deployment: `k scale deployment forgejo --replicas=1`
1. Check for errors
1. After upgrading, login as an admin, go to the `/admin` page and click run `Sync missed branches from git data to databases` (`Fehlende Branches aus den Git-Daten in die Datenbank synchronisieren`). If this is not done there will be messages such as `LoadBranches: branch does not exist in the logs`.

## Upgrade to 7.0.0

1. Scale down Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Adjust configmap: `k edit cm forgejo-env`
    1. Change `FORGEJO__oauth2__ENABLE: "true"` TO `FORGEJO__oauth2__ENABLED: "true"`
1. Delete app.ini: `k exec -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `7.0.0` with `k edit deployment forgejo`
1. Scale up Forgejo Deployment: `k scale deployment forgejo --replicas=1`
1. Check for errors

## Upgrade to 7.0.5 (no breaking changes)

TODO: Upgrade to 8.0.0 instead after Release!

1. Scale down Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Delete app.ini: `k exec -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `7.0.5` with `k edit deployment forgejo`
1. Scale up Forgejo Deployment: `k scale deployment forgejo --replicas=1`
1. Check for errors

## Post Work

1. Switch DNS to new server
1. Reenable Backup Cron on new server: `k patch cronjobs forgejo-backup -p '{"spec" : {"suspend" : false }}'`
1. Execute manual Backup on new server: `kubectl exec -it backup-restore-... -- /usr/local/bin/backup.sh`
1. Scale down Backup-Restore Deployment: `kubectl scale deployment backup-restore --replicas=1`
1. The scope of all access tokens might (invisibly) have changed (in v1.20). Thus, rotate all tokens!
1. Users should check their ssh keys: if they use rsa keys the minimum length should be 3072 bits! However, shorter keys should still work.

## Known Errors

### Error in v1.20.1-0

In the logs the following error can be found. This will be resolved automatically with the next upgrade (v1.21).

```
2024/07/08 08:31:30 ...g/config_provider.go:321:deprecatedSetting() [E] Deprecated fallback `[log]` `ROUTER` present. Use `[log]` `logger.router.MODE` instead. This fallback will be/has been removed in 1.21
```

# Add Shynet Analytics

1. Log into shynet & create new Service
    1. Copy the generated html snippet and save it somewhere you remember
1. SSH into prod server
1. Make the necessary folders and files in forgejo data dir:
    1. `kubectl exec -n forgejo -it forgejo-... -- bash`
    1. `mkdir -p /data/gitea/templates/custom`
    1. `touch /data/gitea/templates/custom/footer.tmpl`
1. Open the `footer.tmpl` and paste the saved snippet
1. Restart the pod
    1. `k scale -n forgejo deployment forgejo --replicas=0`
    1. `k scale -n forgejo deployment forgejo --replicas=1`
1. Add Information about analytics: Clone Datenschutz Repo
    1. `git clone ssh://git@repo.prod.meissa.de:2222/meissa/Datenschutz.git`
1. Merge forgejo-upgrade into main
    1. `git merge forgejo-upgrade`
1. Push to origin
    1. `git push`
