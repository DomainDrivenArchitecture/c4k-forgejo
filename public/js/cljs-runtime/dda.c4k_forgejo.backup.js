goog.provide('dda.c4k_forgejo.backup');
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-forgejo.backup","aws-access-key-id","dda.c4k-forgejo.backup/aws-access-key-id",713087954),new cljs.core.Symbol("dda.c4k-common.common","bash-env-string?","dda.c4k-common.common/bash-env-string?",-1366162540,null),dda.c4k_common.common.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-forgejo.backup","aws-secret-access-key","dda.c4k-forgejo.backup/aws-secret-access-key",1854042954),new cljs.core.Symbol("dda.c4k-common.common","bash-env-string?","dda.c4k-common.common/bash-env-string?",-1366162540,null),dda.c4k_common.common.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-forgejo.backup","restic-password","dda.c4k-forgejo.backup/restic-password",120845851),new cljs.core.Symbol("dda.c4k-common.common","bash-env-string?","dda.c4k-common.common/bash-env-string?",-1366162540,null),dda.c4k_common.common.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-forgejo.backup","restic-repository","dda.c4k-forgejo.backup/restic-repository",-932989849),new cljs.core.Symbol("dda.c4k-common.common","bash-env-string?","dda.c4k-common.common/bash-env-string?",-1366162540,null),dda.c4k_common.common.bash_env_string_QMARK_);
dda.c4k_common.yaml.load_resource.cljs$core$IMultiFn$_add_method$arity$3(null,new cljs.core.Keyword(null,"backup","backup",26347393),(function (resource_name){
var G__6172 = resource_name;
switch (G__6172) {
case "backup/config.yaml":
return "apiVersion: v1\nkind: ConfigMap\nmetadata:\n  name: backup-config\n  labels:\n    app.kubernetes.io/name: backup\n    app.kubernetes.io/part-of: forgejo\ndata:\n  restic-repository: restic-repository";

break;
case "backup/cron.yaml":
return "apiVersion: batch/v1beta1\nkind: CronJob\nmetadata:\n  name: forgejo-backup\n  labels:\n    app.kubernetes.part-of: forgejo\nspec:\n  schedule: \"10 23 * * *\"\n  successfulJobsHistoryLimit: 1\n  failedJobsHistoryLimit: 1\n  jobTemplate:\n    spec:\n      template:\n        spec:\n          containers:\n          - name: backup-app\n            image: domaindrivenarchitecture/c4k-forgejo-backup\n            imagePullPolicy: IfNotPresent\n            command: [\"/entrypoint.sh\"]\n            env:\n            - name: POSTGRES_USER\n              valueFrom:\n                secretKeyRef:\n                  name: postgres-secret\n                  key: postgres-user\n            - name: POSTGRES_PASSWORD\n              valueFrom:\n                secretKeyRef:\n                  name: postgres-secret\n                  key: postgres-password\n            - name: POSTGRES_DB\n              valueFrom:\n                configMapKeyRef:\n                  name: postgres-config\n                  key: postgres-db\n            - name: POSTGRES_HOST\n              value: \"postgresql-service:5432\"\n            - name: POSTGRES_SERVICE\n              value: \"postgresql-service\"\n            - name: POSTGRES_PORT\n              value: \"5432\"\n            - name: AWS_DEFAULT_REGION\n              value: eu-central-1\n            - name: AWS_ACCESS_KEY_ID_FILE\n              value: /var/run/secrets/backup-secrets/aws-access-key-id\n            - name: AWS_SECRET_ACCESS_KEY_FILE\n              value: /var/run/secrets/backup-secrets/aws-secret-access-key\n            - name: RESTIC_REPOSITORY\n              valueFrom:\n                configMapKeyRef:\n                  name: backup-config\n                  key: restic-repository\n            - name: RESTIC_PASSWORD_FILE\n              value: /var/run/secrets/backup-secrets/restic-password\n            - name: CERTIFICATE_FILE\n              value: \"\"\n            volumeMounts:\n            - name: forgejo-data-volume\n              mountPath: /var/backups\n            - name: backup-secret-volume\n              mountPath: /var/run/secrets/backup-secrets\n              readOnly: true\n          volumes:\n          - name: forgejo-data-volume\n            persistentVolumeClaim:\n              claimName: forgejo-data-pvc\n          - name: backup-secret-volume\n            secret:\n              secretName: backup-secret\n          restartPolicy: OnFailure";

break;
case "backup/secret.yaml":
return "apiVersion: v1\nkind: Secret\nmetadata:\n  name: backup-secret\ntype: Opaque\ndata:\n  aws-access-key-id: aws-access-key-id\n  aws-secret-access-key: aws-secret-access-key\n  restic-password: restic-password";

break;
case "backup/backup-restore-deployment.yaml":
return "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: backup-restore\nspec:\n  replicas: 0\n  selector:\n    matchLabels:\n      app: backup-restore\n  strategy:\n    type: Recreate\n  template:\n    metadata:\n      labels:\n        app: backup-restore\n        app.kubernetes.io/name: backup-restore\n        app.kubernetes.io/part-of: forgejo\n    spec:\n      containers:\n      - image: domaindrivenarchitecture/c4k-forgejo-backup\n        name: backup-app\n        imagePullPolicy: IfNotPresent\n        command: [\"/entrypoint-start-and-wait.sh\"]\n        env:\n        - name: POSTGRES_USER\n          valueFrom:\n            secretKeyRef:\n              name: postgres-secret\n              key: postgres-user\n        - name: POSTGRES_PASSWORD\n          valueFrom:\n            secretKeyRef:\n              name: postgres-secret\n              key: postgres-password\n        - name: POSTGRES_DB\n          valueFrom:\n            configMapKeyRef:\n              name: postgres-config\n              key: postgres-db\n        - name: POSTGRES_HOST\n          value: \"postgresql-service:5432\"\n        - name: POSTGRES_SERVICE\n          value: \"postgresql-service\"\n        - name: POSTGRES_PORT\n          value: \"5432\"\n        - name: AWS_DEFAULT_REGION\n          value: eu-central-1\n        - name: AWS_ACCESS_KEY_ID_FILE\n          value: /var/run/secrets/backup-secrets/aws-access-key-id\n        - name: AWS_SECRET_ACCESS_KEY_FILE\n          value: /var/run/secrets/backup-secrets/aws-secret-access-key\n        - name: RESTIC_REPOSITORY\n          valueFrom:\n            configMapKeyRef:\n              name: backup-config\n              key: restic-repository\n        - name: RESTIC_PASSWORD_FILE\n          value: /var/run/secrets/backup-secrets/restic-password\n        - name: CERTIFICATE_FILE\n          value: \"\"\n        volumeMounts:\n        - name: forgejo-data-volume\n          mountPath: /var/backups\n        - name: backup-secret-volume\n          mountPath: /var/run/secrets/backup-secrets\n          readOnly: true\n      volumes:\n      - name: forgejo-data-volume\n        persistentVolumeClaim:\n          claimName: forgejo-data-pvc\n      - name: backup-secret-volume\n        secret:\n          secretName: backup-secret";

break;
default:
throw (new Error("Undefined Resource!"));

}
}));
dda.c4k_forgejo.backup.generate_config = (function dda$c4k_forgejo$backup$generate_config(my_conf){
var map__6179 = my_conf;
var map__6179__$1 = cljs.core.__destructure_map(map__6179);
var restic_repository = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6179__$1,new cljs.core.Keyword(null,"restic-repository","restic-repository",539250251));
return dda.c4k_common.common.replace_key_value(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("backup/config.yaml")),new cljs.core.Keyword(null,"restic-repository","restic-repository",539250251),restic_repository);
});
dda.c4k_forgejo.backup.generate_cron = (function dda$c4k_forgejo$backup$generate_cron(){
return dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("backup/cron.yaml"));
});
dda.c4k_forgejo.backup.generate_backup_restore_deployment = (function dda$c4k_forgejo$backup$generate_backup_restore_deployment(my_conf){
var backup_restore_yaml = dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("backup/backup-restore-deployment.yaml"));
if(((cljs.core.contains_QMARK_(my_conf,new cljs.core.Keyword(null,"local-integration-test","local-integration-test",-748876833))) && (cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(true,new cljs.core.Keyword(null,"local-integration-test","local-integration-test",-748876833).cljs$core$IFn$_invoke$arity$1(my_conf))))){
return dda.c4k_common.common.replace_named_value(backup_restore_yaml,"CERTIFICATE_FILE","/var/run/secrets/localstack-secrets/ca.crt");
} else {
return backup_restore_yaml;
}
});
dda.c4k_forgejo.backup.generate_secret = (function dda$c4k_forgejo$backup$generate_secret(my_auth){
var map__6183 = my_auth;
var map__6183__$1 = cljs.core.__destructure_map(map__6183);
var aws_access_key_id = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6183__$1,new cljs.core.Keyword(null,"aws-access-key-id","aws-access-key-id",-1304390194));
var aws_secret_access_key = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6183__$1,new cljs.core.Keyword(null,"aws-secret-access-key","aws-secret-access-key",-700318378));
var restic_password = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6183__$1,new cljs.core.Keyword(null,"restic-password","restic-password",-1619763169));
return dda.c4k_common.common.replace_key_value(dda.c4k_common.common.replace_key_value(dda.c4k_common.common.replace_key_value(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("backup/secret.yaml")),new cljs.core.Keyword(null,"aws-access-key-id","aws-access-key-id",-1304390194),dda.c4k_common.base64.encode(aws_access_key_id)),new cljs.core.Keyword(null,"aws-secret-access-key","aws-secret-access-key",-700318378),dda.c4k_common.base64.encode(aws_secret_access_key)),new cljs.core.Keyword(null,"restic-password","restic-password",-1619763169),dda.c4k_common.base64.encode(restic_password));
});

//# sourceMappingURL=dda.c4k_forgejo.backup.js.map
