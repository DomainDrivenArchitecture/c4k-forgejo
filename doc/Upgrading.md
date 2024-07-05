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
	- `FORGEJO__oauth2__ENABLED: "true"`  
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[log]` `ROUTER` present. Use `[log]` `logger.router.MODE` i
  nstead. This fallback will be/has been removed in 1.21  
	- `FORGEJO__log_0x2E_logger_0x2E_router__MODE: console, file`  
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[service]` `EMAIL_DOMAIN_WHITELIST` present. Use `[service]
  ` `EMAIL_DOMAIN_ALLOWLIST` instead. This fallback will be/has been removed in 1.21  
	- `FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST:`  
- 2024/07/02 13:16:17 ...g/config_provider.go:329:deprecatedSetting() [E] Deprecated config option `[mailer]` `MAILER_TYPE` present. Use `[mailer]` `PROTOCOL` 
  instead. This fallback will be/has been removed in v1.19.0  
	- ...es/setting/mailer.go:133:loadMailerFrom() [E] Deprecated fallback `[mailer]` `PROTOCOL = smtp+startls` present. Use `[mailer]` `PROTOCOL = smtp+starttls`` instead. This fallback will be removed in v1.19.0  
		- `FORGEJO__mailer__PROTOCOL: smtp+starttls`  
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
	- [/repos/{owner}/{repo}/push_mirrors](https://code.forgejo.org/api/swagger/#/repository/repoListPushMirrors)
	- Application profiling

##### repos

- do we have repo descriptions?
	- https://codeberg.org/forgejo/forgejo/commit/1075ff74b5050f671c5f9824ae39390230b3c85d

##### app.ini

- check [ui] - n/e

### Vor dem Upgrade

- host cert used for auth? - no
- do we use webhooks? - no
- do we use:
	- [/repos/{owner}/{repo}/releases - repoListReleases](https://code.forgejo.org/api/swagger/#/repository/repoListReleases) - no
		- In the ListReleases, the `per_page` parameter has been decoupled from the `limit` parameter, we do not use the repoListReleases endpoint
		- In the `ArtifactDeploymentApi` in dda-devops-build we only use the `POST` method
			- The respective endpoint is [repoCreateRelease](https://code.forgejo.org/api/swagger/#/repository/repoCreateRelease)
	- [`/repos/{owner}/{repo}/push_mirrors`](https://code.forgejo.org/api/swagger/#/repository/repoListPushMirrors) - no
	- Application profiling - no
- do we have repo descriptions? - yes
	- There is now a sanitizer that only allows links, emphasis, code and emojis
		- See: https://codeberg.org/forgejo/forgejo/commit/1075ff74b5050f671c5f9824ae39390230b3c85d
	- Our repository descriptions are mostly plaintext and links

### Upgrade plan

TEST indicates actions that only apply to the test server and are ignored in PROD.
PROD indicates actions that only apply to the test server and are ignored in TEST.
See also the overview for upgrading: https://forgejo.org/docs/latest/admin/upgrade/

- Set up Forgejo server with c4k-forgejo v3.2.2
	- Has Forgejo v1.19
- TEST
	- Delete old remote ids
		- `ssh-keygen -f "/home/${USER}/.ssh/known_hosts" -R "repo.test.meissa.de"`
- Ssh to server
- Forgejo pod downscale
	- `k scale deployment forgejo --replicas=0`
- Install lock off
	- `k edit cm forgejo-env`
	- Set to `FORGEJO__security__INSTALL_LOCK: "false"`
- Forgejo pod upscale
	- `k scale deployment forgejo --replicas=1`
- Create admin test or prod admin and install forgejo
	- `gopass show server/meissa/forgejo-test` bzw `-prod`
- Forgejo pod downscale
- Install lock on
	- Set to `FORGEJO__security__INSTALL_LOCK: "true"`
- TEST
	- Forgejo pod upscale
	- Log in
	- Make Ssh keys
		- ed_xyz
		- rsa mit 2048
		- rsa mit 4096
	- Create repos
	- Forgejo pod downscale
- PROD
	- Backup pod upscale
		- `k scale deployment backup-restore --replicas=1`
	- Restore backups
	- Delete or rename app.ini's in the pod
	- Backup pod downscale
		- `k scale deployment backup-restore --replicas=0`
- Set image version to 7.0.4 in forgejo deployment
	- `k edit deployment.apps forgejo`
- Update configmap:
	- Double check install lock enabled
	- `FORGEJO__oauth2__ENABLED: "true"`  
	- `FORGEJO__log_0x2E_logger_0x2E_router__MODE: console, file`  
	- `FORGEJO__service__EMAIL_DOMAIN_ALLOWLIST:`  
	- `FORGEJO__mailer__PROTOCOL: smtp+starttls`  
	- `FORGEJO__federation__ENABLED: true`  
- TEST
	- Backup pod upscale
	- Delete or rename app.ini's in the pod
	- Backup pod downscale
- Forgejo pod upscale
- Migrations happen automatically
- `/admin` page and click run Sync missed branches from git data to databases
	- and **Sync missed tags ...*
- Rsa keys with size 2048 can not be added anymore. However, it seems they still can be used if they are on the server
- Team members having app tokens need to recreate them with proper scopes
- Add analytics: https://forgejo.org/docs/latest/admin/customization/