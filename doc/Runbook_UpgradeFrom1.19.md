# Playbook Upgrade from 1.19 to 7.0.5

## Info: Relevant Breaking Changes:

* 1.19.3: First version under consideration
* 1.20.1-0: Breaking https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-20-1-0
* 1.21.1-0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-21-1-0
* 7.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#7-0-0
* 8.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#8-0-0
* 9.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/release-notes-published/9.0.0.md

## Preparations

1. Stop Forgejo Prod: `k scale deployment forgejo --replicas=0`
1. Disable Backup Cron: `k patch cronjobs forgejo-backup -p '{"spec" : {"suspend" : true }}'`
1. Scale up Backup-Restore Deployment: `kubectl scale deployment backup-restore --replicas=1`
1. Execute Manual Backup: `kubectl exec -n forgejo -it backup-restore-... -- /usr/local/bin/backup.sh`

### Create 2nd Repo Prod Server

1. Terraform Preparations for 2nd Server: TODO
1. Install c4k-forgejo Version `3.5.0`!
   with config `"forgejo-image-version-overwrite": "1.19.3-0"` (in server-setup)
1. Stop Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Disable Backup Cron: `k patch -n forgejo cronjobs forgejo-backup -p '{"spec" : {"suspend" : true }}'`
1. Scale up Backup-Restore Deployment: `kubectl scale -n forgejo deployment backup-restore --replicas=1`
1. Restore Forgejo Backup: See [BackupAndRestore.md](BackupAndRestore.md)
1. Check for `..._INSTALL_LOCK: true` in ConfigMap `forgejo-env`
1. Scale up Forgejo Deployment and check for (startup) problems: `k scale -n forgejo deployment forgejo --replicas=1`

## Upgrade to 1.20.1-0

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Adjust configmap: `k edit -n forgejo cm forgejo-env`
    1. Remove `FORGEJO__database__CHARSET: utf8` (This was a misconfiguration, since this option only had effect for mysql dbs)
    1. Change `FORGEJO__mailer__MAILER_TYPE: smtp+startls` TO `FORGEJO__mailer__PROTOCOL: smtp+starttls` (Missed deprecation from 1.19)
    1. Change `FORGEJO__service__EMAIL_DOMAIN_WHITELIST: repo.test.meissa.de` TO `FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST: repo.test.meissa.de` (Fallback deprecation in 1.21)
1. Delete app.ini: `k exec -n forgejo -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `1.20.1-0` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Upgrade to 1.21.1-0

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Delete app.ini: `k exec -n forgejo -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `1.21.1-0` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`
1. After upgrading, login as an admin, go to the `/admin` page and click run `Sync missed branches from git data to databases` (`Fehlende Branches aus den Git-Daten in die Datenbank synchronisieren`). If this is not done there will be messages such as `LoadBranches: branch does not exist in the logs`.

## Upgrade to 7.0.0

1. Check DB Version.
    1. MariaDB or MySQL needs to be 8.0 or higher.
    2. Postgres needs to be 12 or higher
1. API Endpoints
    1. Check if the [/repos/{owner}/{repo}/releases](https://code.forgejo.org/api/swagger/#/repository/repoListReleases) API endpoint is used
        1. as the per_page param is not used for [limit](https://codeberg.org/forgejo/forgejo/commit/0aab2d38a7d91bc8caff332e452364468ce52d9a) anymore
    2. Check if [/repos/{owner}/{repo}/push_mirrors](https://code.forgejo.org/api/swagger/#/repository/repoListPushMirrors) and [/repos/{owner}/{repo}/push_mirrors](https://code.forgejo.org/api/swagger/#/repository/repoAddPushMirror) API endpoints are used
        1. The date format of created and last_update fields are now [timestamps](https://codeberg.org/forgejo/forgejo/commit/0ee7cbf725f45650136be45f8e0f74d395f73b5c)
    3. [pprof](https://forgejo.org/docs/v7.0/admin/config-cheat-sheet/#server-server) endpoint changed labels
        1. graceful-lifecycle to gracefulLifecycle
        2. process-type to processType
        3. process-description to processDescription This allows for those endpoints to be scraped by services requiring prometheus style labels such as grafana-agent.
1. The Gitea themes were renamed and the \[ui\].THEMES setting must be changed as follows:
    1. gitea is replaced by gitea-light
    2. arc-green is replaced by gitea-dark
    3. auto is replaced by gitea-auto
1. Migration warning
    2. If the logs show a line like the following, run `doctor convert` to fix it.
        3. Current database is using a case-insensitive collation "utf8mb4_general_ci"
    4. Large instances may experience slow migrations when the database is upgraded to support SHA-256 git repositories.
1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Adjust configmap: `k edit -n forgejo cm forgejo-env`
    1. Change `FORGEJO__oauth2__ENABLE: "true"` TO `FORGEJO__oauth2__ENABLED: "true"`
1. Delete app.ini: `k exec -n forgejo -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `7.0.0` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Upgrade to 8.0.3 (no relevant breaking changes)

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Delete app.ini: `k exec -n forgejo -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `8.0.3` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Upgrade to 9.0.3 (no relevant breaking changes)

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Set version to `9.0.3` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Upgrade to 10.0.3 (no relevant breaking changes)

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Set version to `10.0.3` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Upgrade to 11.0.1 (no relevant breaking changes)

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Set version to `11.0.1` with `k edit -n forgejo deployment forgejo`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Enable Federation

1. Scale down Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=0`
1. Adjust configmap: `k edit -n forgejo cm forgejo-env`
    1. Change `FORGEJO__federation__ENABLED: "false"` TO `FORGEJO__federation__ENABLED: "true"`
1. Delete app.ini: `k exec -n forgejo -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Scale up Forgejo Deployment: `k scale -n forgejo deployment forgejo --replicas=1`
1. Check for errors: `k logs -n forgejo forgejo-...`

## Post Work

1. Switch DNS to new server
1. Reenable Backup Cron on new server: `k patch -n forgejo cronjobs forgejo-backup -p '{"spec" : {"suspend" : false }}'`
1. Execute manual Backup on new server: `kubectl exec -n forgejo -it backup-restore-... -- /usr/local/bin/backup.sh`
1. Scale down Backup-Restore Deployment: `kubectl scale -n forgejo deployment backup-restore --replicas=1`
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
