# Playbook Upgrade from 1.19 to 7.0.5

## Info: Relevant Breaking Changes:

* 1.19.3:Current version
* 1.20.1-0: Breaking https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-20-1-0
* 1.21.1-0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-21-1-0
* 7.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#7-0-0

## Preparations

1. Stop Forgejo Prod: TODO
1. Disable Backup Cron: TODO
1. Scale up Backup-Restore Deployment: TODO
1. Execute Manual Backup: TODO

### Create 2nd Repo Prod Server

1. Terraform Preparations for 2nd Server: TODO
1. Install c4k-forgejo Version TODO   
   with config `"forgejo-image-version-overwrite": "1.19.3-0"`
1. Stop Forgejo Deployment: TODO
1. Scale up Backup-Restore Deployment: TODO
1. Restore Forgejo Backup: See [BackupAndRestore.md](BackupAndRestore.md)
1. Check for `..._INSTALL_LOCK: true` in ConfigMap `forgejo-env`
1. Scale up Forgejo Deployment and check for (startup) problems: TODO

## Upgrade to 1.20.1-0

1. Scale down Forgejo Deployment: `k scale deployment forgejo --replicas=0`
1. Adjust configmap: `k edit cm forgejo-env`
    1. Change `FORGEJO__mailer__MAILER_TYPE: smtp+startls` TO `FORGEJO__mailer__PROTOCOL: smtp+starttls` (Missed deprecation from 1.19)
1. Delete app.ini: `k exec -it backup-restore-... -- rm /var/backups/gitea/conf/app.ini`
1. Set version to `1.20.1-0` with `k edit deployment forgejo`
1. Scale up Forgejo Deployment: `k scale deployment forgejo --replicas=1`
1. Check for errors

## Upgrade to 1.21...

TODO:
2024/07/08 08:31:30 ...g/config_provider.go:321:deprecatedSetting() [E] Deprecated fallback `[log]` `ROUTER` present. Use `[log]` `logger.router.MODE` instead. This fallback will be/has been removed in 1.21
2024/07/08 08:31:30 ...g/config_provider.go:321:deprecatedSetting() [E] Deprecated fallback `[service]` `EMAIL_DOMAIN_WHITELIST` present. Use `[service]` `EMAIL_DOMAIN_ALLOWLIST` instead. This fallback will be/has been removed in 1.21

## Post Work

1. The scope of all access tokens might (invisibly) have changed (in v1.20). Thus, rotate all tokens!