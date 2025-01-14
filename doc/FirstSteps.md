# First Steps

## Create admin user

1. exec into pod and execute restore pod (press tab to get your exact pod name)   
   `kubectl -n forgejo exec -it backup-restore-... -- bash`
2. create admin user   
   `su git -c "gitea admin user create --username [login] --password [password] -email "email"--admin"`
