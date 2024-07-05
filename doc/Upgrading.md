# Upgrading process

## adhoc (on kubernetes cluster)

Ssh into your kubernetes cluster running the forgejo instance.  

``` bash
kubectl edit configmap forgejo-env
# make sure INSTALL_LOCK under security is set to true to disable the installation screen
# save and exit
kubectl edit deployments forgejo
# search for your current forgejo version, e.g. 1.19
# replace with new version
# save and exit
kubectl scale deployment forgejo --replicas=0
kubectl scale deployment forgejo --replicas=1
```

Logging into the admin account should now show the new version.

You may want to update your c4k-forgejo resources to reflect the changes made on the cluster.

## Upgrading from 1.19

### Config related issues with c4k-forgejo v3.2.2

- oauth2: ENABLED instead of ENABLE
	- `FORGEJOoauth2ENABLED: "true"`
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[log]` `ROUTER` present. Use `[log]` `logger.router.MODE` i
  nstead. This fallback will be/has been removed in 1.21  
	- `FORGEJOlog_0x2E_logger_0x2E_routerMODE: console, file`
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[service]` `EMAIL_DOMAIN_WHITELIST` present. Use `[service]
  ` `EMAIL_DOMAIN_ALLOWLIST` instead. This fallback will be/has been removed in 1.21  
	- `FORGEJOserviceEMAIL_DOMAIN_ALLOWLIST:`
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[mailer]` `MAILER_TYPE` present. Use `[mailer]` `PROTOCOL` 
  instead. This fallback will be/has been removed in v1.19.0  
	- ...es/setting/mailer.go:133:loadMailerFrom() [E] Deprecated fallback `[mailer]` `PROTOCOL = smtp+startls` present. Use `[mailer]` `PROTOC
	  OL = smtp+starttls`` instead. This fallback will be removed in v1.19.0  
		- `FORGEJOmailerPROTOCOL: smtp+starttls`
		- starttls instead of startls
				
### Breaking Changes

#### 1.19.3:Aktueller Stand

#### 1.20.1-0: Breaking https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-20-1-0

##### app.ini

- check [queue] section - n/e
- check [repository.editor] - n/e
- check [storage] - n/e
- check ssh_keygen_path in app.ini - n/e
- is WORK_PATH set? Or app.ini writeable by forgejo server user?
    - 1. no
    - 2. probably
		- If not, it shows in the logs starting with: `Unable to update WORK_PATH`
		- Also ssh pushing will likely fail - *test ssh*
		- no errors on test instance
- set logger.router.mode as described in environment-to-ini
	- see: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/contrib/environment-to-ini
- check [git.reflog] and maybe move to [git.config] - n/e
- check [indexer], [mailer], [repository] - n/e

##### tokens

- scoped and personal access tokens were refactored
	- scope may change, if we have tokens they should be rotated

#### 1.21.1-0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#1-21-1-0

##### custom themes

- move to `custom/public/assets/`

##### git branches

- `/admin` page and click run Sync missed branches from git data to databases.

##### db - mysql

- we use postgres

##### ssh server

- host cert used for auth?

##### ssh keys

- all team members need to check their key length, now 3072

##### tokens

- finer restrictions might now return 404 errors on users in certain teams with certain restrictions

#### 7.0.0: https://codeberg.org/forgejo/forgejo/src/branch/forgejo/RELEASE-NOTES.md#7-0-0

##### webhooks

- do we use webhooks?

##### db

- psql min ver is 12 - should be fine right?

##### api

- benutzen wir:
	- [/repos/{owner}/{repo}/releases](https://code.forgejo.org/api/swagger/#/repository/repoListReleases)
	- [`/repos/{owner}/{repo}/push_mirrors`](https://code.forgejo.org/api/swagger/#/repository/repoListPushMirrors)
	- Application profiling

##### repos

- do we have repo descriptions?
	- https://codeberg.org/forgejo/forgejo/commit/1075ff74b5050f671c5f9824ae39390230b3c85d

##### app.ini

- check [ui] - n/e

### Vor dem Upgrade

- host cert used for auth?
- benutzen wir webhooks?
- benutzen wir:
	- [/repos/{owner}/{repo}/releases](https://code.forgejo.org/api/swagger/#/repository/repoListReleases)
	- [`/repos/{owner}/{repo}/push_mirrors`](https://code.forgejo.org/api/swagger/#/repository/repoListPushMirrors)
	- Application profiling
- do we have repo descriptions?
	- https://codeberg.org/forgejo/forgejo/commit/1075ff74b5050f671c5f9824ae39390230b3c85d

### Upgrade plan

TEST kennzeichnet Aktionen die nur für den Testserver gelten und in PROD ignoriert werden.
PROD kennzeichnet Aktionen die nur für den Testserver gelten und in TEST ignoriert werden.
Generelle Übersicht zu Upgrades: https://forgejo.org/docs/latest/admin/upgrade/

- Forgejo server aufsetzen mit alter version
- TEST
	- Alte remote id löschen
		- ssh-keygen -f "/home/erik/.ssh/known_hosts" -R "repo.test.meissa.de"
- auf server ssh'en
- forgejo pod runterfahren
	- `k scale deployment forgejo --replicas=0`
- install lock aus
	- `k edit cm forgejo-env`
	- set to `FORGEJOsecurityINSTALL_LOCK: "false"`
- forgejo pod hochfahren
	- `k scale deployment forgejo --replicas=1`
- admin test oder prod admin anlegen und forgejo installieren
	- `gopass show server/meissa/forgejo-test` bzw `-prod`
- forgejo pod runterfahren
- install lock an
	- set to `FORGEJOsecurityINSTALL_LOCK: "true"`
- TEST
	- forgejo pod hochfahren
	- einloggen
	- ssh keys anlegen
		- ed_xyz
		- rsa mit 2048
		- rsa mit 4096
	- Repos anlegen
	- forgejo pod runterfahren
- PROD
	- backup pod hochfahren
		- `k scale deployment backup-restore --replicas=1`
	- backups zurückspielen
	- im backup pod vorhandene app.ini's löschen bzw umbenennen
	- backup pod runterfahren
		- `k scale deployment backup-restore --replicas=0`
- im deployment image ver auf 7.0.4 setzen
	- `k edit deployment.apps forgejo`
- configmap updaten
	- double check ob install lock an
	- siehe Config related errors oben
- TEST
	- backup pod hochfahren
	- im backup pod vorhandene app.ini's löschen bzw umbenennen
	- backup pod runterfahren
		- `k scale deployment backup-restore --replicas=0`
- forgejo pod hochscalen
- Migrations happen automatically
- `/admin` page and click run Sync missed branches from git data to databases
	- and **Sync missed tags ...*
- rsa keys with size 2048 can not be added anymore. However, it seems they still can be used if they are on the server
- team members having app tokens need to recreate them with proper scopes
- add analytics: https://forgejo.org/docs/latest/admin/customization/