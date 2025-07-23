goog.provide('dda.c4k_common.postgres');
dda.c4k_common.postgres.postgres_size_QMARK_ = (function dda$c4k_common$postgres$postgres_size_QMARK_(input){
return cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"4gb","4gb",-236951575),null,new cljs.core.Keyword(null,"8gb","8gb",1820517612),null,new cljs.core.Keyword(null,"2gb","2gb",175964494),null,new cljs.core.Keyword(null,"16gb","16gb",654916511),null], null), null),input);
});
dda.c4k_common.postgres.postgres_image_QMARK_ = (function dda$c4k_common$postgres$postgres_image_QMARK_(input){
return cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["postgres:13",null,"postgres:14",null], null), null),input);
});
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-user","dda.c4k-common.postgres/postgres-db-user",294546573),new cljs.core.Symbol("dda.c4k-common.predicate","bash-env-string?","dda.c4k-common.predicate/bash-env-string?",-1836972098,null),dda.c4k_common.predicate.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-password","dda.c4k-common.postgres/postgres-db-password",262988616),new cljs.core.Symbol("dda.c4k-common.predicate","bash-env-string?","dda.c4k-common.predicate/bash-env-string?",-1836972098,null),dda.c4k_common.predicate.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","postgres-data-volume-path","dda.c4k-common.postgres/postgres-data-volume-path",-559762079),new cljs.core.Symbol("cljs.core","string?","cljs.core/string?",-2072921719,null),cljs.core.string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","postgres-size","dda.c4k-common.postgres/postgres-size",685676416),new cljs.core.Symbol("dda.c4k-common.postgres","postgres-size?","dda.c4k-common.postgres/postgres-size?",2022573628,null),dda.c4k_common.postgres.postgres_size_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","db-name","dda.c4k-common.postgres/db-name",-659148795),new cljs.core.Symbol("dda.c4k-common.predicate","bash-env-string?","dda.c4k-common.predicate/bash-env-string?",-1836972098,null),dda.c4k_common.predicate.bash_env_string_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","pvc-storage-class-name","dda.c4k-common.postgres/pvc-storage-class-name",-407663769),new cljs.core.Symbol("dda.c4k-common.predicate","pvc-storage-class-name?","dda.c4k-common.predicate/pvc-storage-class-name?",1622691716,null),dda.c4k_common.predicate.pvc_storage_class_name_QMARK_);
cljs.spec.alpha.def_impl(new cljs.core.Keyword("dda.c4k-common.postgres","pv-storage-size-gb","dda.c4k-common.postgres/pv-storage-size-gb",-572635235),new cljs.core.Symbol("cljs.core","pos?","cljs.core/pos?",-652182749,null),cljs.core.pos_QMARK_);
dda.c4k_common.postgres.pg_config_QMARK_ = cljs.spec.alpha.map_spec_impl(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[null,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","postgres-size","dda.c4k-common.postgres/postgres-size",685676416),new cljs.core.Keyword("dda.c4k-common.postgres","db-name","dda.c4k-common.postgres/db-name",-659148795),new cljs.core.Keyword("dda.c4k-common.postgres","postgres-data-volume-path","dda.c4k-common.postgres/postgres-data-volume-path",-559762079),new cljs.core.Keyword("dda.c4k-common.postgres","pvc-storage-class-name","dda.c4k-common.postgres/pvc-storage-class-name",-407663769),new cljs.core.Keyword("dda.c4k-common.postgres","pv-storage-size-gb","dda.c4k-common.postgres/pv-storage-size-gb",-572635235)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__6167){
return cljs.core.map_QMARK_(G__6167);
})], null),(function (G__6167){
return cljs.core.map_QMARK_(G__6167);
}),new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"postgres-size","postgres-size",-1270014308),new cljs.core.Keyword(null,"db-name","db-name",1157928745),new cljs.core.Keyword(null,"postgres-data-volume-path","postgres-data-volume-path",-2109923699),new cljs.core.Keyword(null,"pvc-storage-class-name","pvc-storage-class-name",1271488579),new cljs.core.Keyword(null,"pv-storage-size-gb","pv-storage-size-gb",2039291521)], null),cljs.core.PersistentVector.EMPTY,null,cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 5, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","postgres-size","dda.c4k-common.postgres/postgres-size",685676416),new cljs.core.Keyword("dda.c4k-common.postgres","db-name","dda.c4k-common.postgres/db-name",-659148795),new cljs.core.Keyword("dda.c4k-common.postgres","postgres-data-volume-path","dda.c4k-common.postgres/postgres-data-volume-path",-559762079),new cljs.core.Keyword("dda.c4k-common.postgres","pvc-storage-class-name","dda.c4k-common.postgres/pvc-storage-class-name",-407663769),new cljs.core.Keyword("dda.c4k-common.postgres","pv-storage-size-gb","dda.c4k-common.postgres/pv-storage-size-gb",-572635235)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null)))], null),null]));
dda.c4k_common.postgres.pg_auth_QMARK_ = cljs.spec.alpha.map_spec_impl(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-user","dda.c4k-common.postgres/postgres-db-user",294546573),new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-password","dda.c4k-common.postgres/postgres-db-password",262988616)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__6169){
return cljs.core.map_QMARK_(G__6169);
})], null),(function (G__6169){
return cljs.core.map_QMARK_(G__6169);
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"postgres-db-user","postgres-db-user",1981611945),new cljs.core.Keyword(null,"postgres-db-password","postgres-db-password",-1958852060)], null),cljs.core.PersistentVector.EMPTY,null,cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-user","dda.c4k-common.postgres/postgres-db-user",294546573),new cljs.core.Keyword("dda.c4k-common.postgres","postgres-db-password","dda.c4k-common.postgres/postgres-db-password",262988616)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null)))], null),null]));
dda.c4k_common.postgres.postgres_function = cljs.spec.alpha.map_spec_impl(cljs.core.PersistentHashMap.fromArrays([new cljs.core.Keyword(null,"req-un","req-un",1074571008),new cljs.core.Keyword(null,"opt-un","opt-un",883442496),new cljs.core.Keyword(null,"gfn","gfn",791517474),new cljs.core.Keyword(null,"pred-exprs","pred-exprs",1792271395),new cljs.core.Keyword(null,"keys-pred","keys-pred",858984739),new cljs.core.Keyword(null,"opt-keys","opt-keys",1262688261),new cljs.core.Keyword(null,"req-specs","req-specs",553962313),new cljs.core.Keyword(null,"req","req",-326448303),new cljs.core.Keyword(null,"req-keys","req-keys",514319221),new cljs.core.Keyword(null,"opt-specs","opt-specs",-384905450),new cljs.core.Keyword(null,"pred-forms","pred-forms",172611832),new cljs.core.Keyword(null,"opt","opt",-794706369)],[null,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","deserializer","dda.c4k-common.postgres/deserializer",-2036850258),new cljs.core.Keyword("dda.c4k-common.postgres","optional","dda.c4k-common.postgres/optional",433072377)], null),null,new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [(function (G__6170){
return cljs.core.map_QMARK_(G__6170);
})], null),(function (G__6170){
return cljs.core.map_QMARK_(G__6170);
}),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"deserializer","deserializer",858642506),new cljs.core.Keyword(null,"optional","optional",2053951509)], null),cljs.core.PersistentVector.EMPTY,null,cljs.core.PersistentVector.EMPTY,new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword("dda.c4k-common.postgres","deserializer","dda.c4k-common.postgres/deserializer",-2036850258),new cljs.core.Keyword("dda.c4k-common.postgres","optional","dda.c4k-common.postgres/optional",433072377)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.core","fn","cljs.core/fn",-1065745098,null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Symbol(null,"%","%",-950237169,null)], null),cljs.core.list(new cljs.core.Symbol("cljs.core","map?","cljs.core/map?",-1390345523,null),new cljs.core.Symbol(null,"%","%",-950237169,null)))], null),null]));
dda.c4k_common.yaml.load_resource.cljs$core$IMultiFn$_add_method$arity$3(null,new cljs.core.Keyword(null,"postgres","postgres",-439520670),(function (resource_name){
var G__6171 = resource_name;
switch (G__6171) {
case "postgres/config-2gb.yaml":
return "apiVersion: v1\nkind: ConfigMap\nmetadata:\n  name: postgres-config\n  labels:\n    app: postgres\ndata:\n  postgres-db: postgres\n  postgresql.conf: |\n    max_connections = 100\n    work_mem = 4MB\n    shared_buffers = 512MB\n";

break;
case "postgres/config-4gb.yaml":
return "apiVersion: v1\nkind: ConfigMap\nmetadata:\n  name: postgres-config\n  labels:\n    app: postgres\ndata:\n  postgres-db: postgres\n  postgresql.conf: |\n    max_connections = 500\n    work_mem = 2MB\n    shared_buffers = 1024MB\n";

break;
case "postgres/config-8gb.yaml":
return "apiVersion: v1\nkind: ConfigMap\nmetadata:\n  name: postgres-config\n  labels:\n    app: postgres\ndata:\n  postgres-db: postgres\n  postgresql.conf: |\n    max_connections = 700\n    work_mem = 3MB\n    shared_buffers = 2048MB\n";

break;
case "postgres/config-16gb.yaml":
return "apiVersion: v1\nkind: ConfigMap\nmetadata:\n  name: postgres-config\n  labels:\n    app: postgres\ndata:\n  postgres-db: postgres\n  postgresql.conf: |\n    max_connections = 1000\n    work_mem = 4MB\n    shared_buffers = 2048MB\n";

break;
case "postgres/deployment.yaml":
return "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: postgresql\nspec:\n  selector:\n    matchLabels:\n      app: postgresql\n  strategy:\n    type: Recreate\n  template:\n    metadata:\n      labels:\n        app: postgresql\n    spec:\n      containers:\n        - image: postgres\n          name: postgresql\n          env:\n            - name: POSTGRES_USER\n              valueFrom:\n                secretKeyRef:\n                  name: postgres-secret\n                  key: postgres-user\n            - name: POSTGRES_PASSWORD\n              valueFrom:\n                secretKeyRef:\n                  name: postgres-secret\n                  key: postgres-password\n            - name: POSTGRES_DB\n              valueFrom:\n                configMapKeyRef:\n                  name: postgres-config\n                  key: postgres-db\n          ports:\n            - containerPort: 5432\n              name: postgresql\n          volumeMounts:\n            - name: postgres-config-volume\n              mountPath: /etc/postgresql/postgresql.conf\n              subPath: postgresql.conf\n              readOnly: true\n            - name: postgre-data-volume\n              mountPath: /var/lib/postgresql/data\n      volumes:\n        - name: postgres-config-volume\n          configMap:\n            name: postgres-config\n        - name: postgre-data-volume\n          persistentVolumeClaim:\n            claimName: postgres-claim\n";

break;
case "postgres/persistent-volume.yaml":
return "kind: PersistentVolume\napiVersion: v1\nmetadata:\n  name: postgres-pv-volume\n  labels:\n    type: local\nspec:\n  storageClassName: manual\n  accessModes:\n    - ReadWriteOnce\n  capacity:\n    storage: 10Gi\n  hostPath:\n    path: \"/var/postgres\"";

break;
case "postgres/pvc.yaml":
return "apiVersion: v1\nkind: PersistentVolumeClaim\nmetadata:\n  name: postgres-claim\n  labels:\n    app: postgres\nspec:\n  storageClassName: REPLACEME\n  accessModes:\n    - ReadWriteOnce\n  resources:\n    requests:\n      storage: REPLACEME";

break;
case "postgres/secret.yaml":
return "apiVersion: v1\nkind: Secret\nmetadata:\n  name: postgres-secret\ntype: Opaque\ndata:\n  postgres-user: \"psql-user\"\n  postgres-password: \"psql-pw\"\n";

break;
case "postgres/service.yaml":
return "apiVersion: v1\nkind: Service\nmetadata:\n  name: postgresql-service\nspec:\n  selector:\n    app: postgresql\n  ports:\n    - port: 5432\n";

break;
default:
throw (new Error("Undefined Resource!"));

}
}));
/**
 * 
 */
dda.c4k_common.postgres.generate_config = (function dda$c4k_common$postgres$generate_config(var_args){
var args__5775__auto__ = [];
var len__5769__auto___6200 = arguments.length;
var i__5770__auto___6201 = (0);
while(true){
if((i__5770__auto___6201 < len__5769__auto___6200)){
args__5775__auto__.push((arguments[i__5770__auto___6201]));

var G__6202 = (i__5770__auto___6201 + (1));
i__5770__auto___6201 = G__6202;
continue;
} else {
}
break;
}

var argseq__5776__auto__ = ((((0) < args__5775__auto__.length))?(new cljs.core.IndexedSeq(args__5775__auto__.slice((0)),(0),null)):null);
return dda.c4k_common.postgres.generate_config.cljs$core$IFn$_invoke$arity$variadic(argseq__5776__auto__);
});

(dda.c4k_common.postgres.generate_config.cljs$core$IFn$_invoke$arity$variadic = (function (config){
var map__6180 = cljs.core.first(config);
var map__6180__$1 = cljs.core.__destructure_map(map__6180);
var postgres_size = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6180__$1,new cljs.core.Keyword(null,"postgres-size","postgres-size",-1270014308),new cljs.core.Keyword(null,"2gb","2gb",175964494));
var db_name = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6180__$1,new cljs.core.Keyword(null,"db-name","db-name",1157928745),"postgres");
return cljs.core.assoc_in(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1(["postgres/config-",cljs.core.name(postgres_size),".yaml"].join(''))),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"data","data",-232669377),new cljs.core.Keyword(null,"postgres-db","postgres-db",-764163406)], null),db_name);
}));

(dda.c4k_common.postgres.generate_config.cljs$lang$maxFixedArity = (0));

/** @this {Function} */
(dda.c4k_common.postgres.generate_config.cljs$lang$applyTo = (function (seq6176){
var self__5755__auto__ = this;
return self__5755__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq6176));
}));


cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-config","dda.c4k-common.postgres/generate-config",406802790,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.cat_impl(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"&","&",509580121)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.maybe_impl(dda.c4k_common.postgres.pg_config_QMARK_,new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));
/**
 * 
 */
dda.c4k_common.postgres.generate_deployment = (function dda$c4k_common$postgres$generate_deployment(var_args){
var args__5775__auto__ = [];
var len__5769__auto___6204 = arguments.length;
var i__5770__auto___6205 = (0);
while(true){
if((i__5770__auto___6205 < len__5769__auto___6204)){
args__5775__auto__.push((arguments[i__5770__auto___6205]));

var G__6206 = (i__5770__auto___6205 + (1));
i__5770__auto___6205 = G__6206;
continue;
} else {
}
break;
}

var argseq__5776__auto__ = ((((0) < args__5775__auto__.length))?(new cljs.core.IndexedSeq(args__5775__auto__.slice((0)),(0),null)):null);
return dda.c4k_common.postgres.generate_deployment.cljs$core$IFn$_invoke$arity$variadic(argseq__5776__auto__);
});

(dda.c4k_common.postgres.generate_deployment.cljs$core$IFn$_invoke$arity$variadic = (function (config){
var map__6185 = cljs.core.first(config);
var map__6185__$1 = cljs.core.__destructure_map(map__6185);
var postgres_image = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6185__$1,new cljs.core.Keyword(null,"postgres-image","postgres-image",-308049100),"postgres:13");
return cljs.core.assoc_in(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("postgres/deployment.yaml")),new cljs.core.PersistentVector(null, 6, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"template","template",-702405684),new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"containers","containers",-2127048083),(0),new cljs.core.Keyword(null,"image","image",-58725096)], null),postgres_image);
}));

(dda.c4k_common.postgres.generate_deployment.cljs$lang$maxFixedArity = (0));

/** @this {Function} */
(dda.c4k_common.postgres.generate_deployment.cljs$lang$applyTo = (function (seq6184){
var self__5755__auto__ = this;
return self__5755__auto__.cljs$core$IFn$_invoke$arity$variadic(cljs.core.seq(seq6184));
}));


cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-deployment","dda.c4k-common.postgres/generate-deployment",-1775037542,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.cat_impl(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"&","&",509580121)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.maybe_impl(dda.c4k_common.postgres.pg_config_QMARK_,new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"&","&",509580121),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","?","cljs.spec.alpha/?",1605136319,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));
/**
 * 
 */
dda.c4k_common.postgres.generate_persistent_volume = (function dda$c4k_common$postgres$generate_persistent_volume(config){
var map__6188 = config;
var map__6188__$1 = cljs.core.__destructure_map(map__6188);
var postgres_data_volume_path = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6188__$1,new cljs.core.Keyword(null,"postgres-data-volume-path","postgres-data-volume-path",-2109923699),"/var/postgres");
var pv_storage_size_gb = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6188__$1,new cljs.core.Keyword(null,"pv-storage-size-gb","pv-storage-size-gb",2039291521),(10));
return cljs.core.assoc_in(cljs.core.assoc_in(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("postgres/persistent-volume.yaml")),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"hostPath","hostPath",-1578264728),new cljs.core.Keyword(null,"path","path",-188191168)], null),postgres_data_volume_path),new cljs.core.PersistentVector(null, 3, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"capacity","capacity",72689734),new cljs.core.Keyword(null,"storage","storage",1867247511)], null),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(pv_storage_size_gb),"Gi"].join(''));
});

cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-persistent-volume","dda.c4k-common.postgres/generate-persistent-volume",1342276597,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.cat_impl(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"config","config",994861415)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null),dda.c4k_common.postgres.pg_config_QMARK_,null,null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));
/**
 * 
 */
dda.c4k_common.postgres.generate_pvc = (function dda$c4k_common$postgres$generate_pvc(config){
var map__6191 = config;
var map__6191__$1 = cljs.core.__destructure_map(map__6191);
var pv_storage_size_gb = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6191__$1,new cljs.core.Keyword(null,"pv-storage-size-gb","pv-storage-size-gb",2039291521),(10));
var pvc_storage_class_name = cljs.core.get.cljs$core$IFn$_invoke$arity$3(map__6191__$1,new cljs.core.Keyword(null,"pvc-storage-class-name","pvc-storage-class-name",1271488579),"manual");
return cljs.core.assoc_in(cljs.core.assoc_in(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("postgres/pvc.yaml")),new cljs.core.PersistentVector(null, 4, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"resources","resources",1632806811),new cljs.core.Keyword(null,"requests","requests",-733055638),new cljs.core.Keyword(null,"storage","storage",1867247511)], null),[cljs.core.str.cljs$core$IFn$_invoke$arity$1(pv_storage_size_gb),"Gi"].join('')),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"spec","spec",347520401),new cljs.core.Keyword(null,"storageClassName","storageClassName",2060039872)], null),cljs.core.name(pvc_storage_class_name));
});

cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-pvc","dda.c4k-common.postgres/generate-pvc",-3730384,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.cat_impl(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"config","config",994861415)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null),dda.c4k_common.postgres.pg_config_QMARK_,null,null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"config","config",994861415),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("dda.c4k-common.postgres","pg-config?","dda.c4k-common.postgres/pg-config?",-1195574387,null))),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));
/**
 * 
 */
dda.c4k_common.postgres.generate_secret = (function dda$c4k_common$postgres$generate_secret(my_auth){
var map__6195 = my_auth;
var map__6195__$1 = cljs.core.__destructure_map(map__6195);
var postgres_db_user = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6195__$1,new cljs.core.Keyword(null,"postgres-db-user","postgres-db-user",1981611945));
var postgres_db_password = cljs.core.get.cljs$core$IFn$_invoke$arity$2(map__6195__$1,new cljs.core.Keyword(null,"postgres-db-password","postgres-db-password",-1958852060));
return dda.c4k_common.common.replace_key_value(dda.c4k_common.common.replace_key_value(dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("postgres/secret.yaml")),new cljs.core.Keyword(null,"postgres-user","postgres-user",-718051424),dda.c4k_common.base64.encode(postgres_db_user)),new cljs.core.Keyword(null,"postgres-password","postgres-password",-847744824),dda.c4k_common.base64.encode(postgres_db_password));
});

cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-secret","dda.c4k-common.postgres/generate-secret",156955246,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"my-auth","my-auth",1156125747),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null))),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"my-auth","my-auth",1156125747),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null))),cljs.spec.alpha.cat_impl(new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.Keyword(null,"my-auth","my-auth",1156125747)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null),cljs.core.any_QMARK_,null,null)], null),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null))], null)),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null),new cljs.core.Keyword(null,"my-auth","my-auth",1156125747),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","spec","cljs.spec.alpha/spec",-707298191,null),new cljs.core.Symbol("cljs.core","any?","cljs.core/any?",-2068111842,null))),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));
/**
 * 
 */
dda.c4k_common.postgres.generate_service = (function dda$c4k_common$postgres$generate_service(){
return dda.c4k_common.yaml.from_string(dda.c4k_common.yaml.load_resource.cljs$core$IFn$_invoke$arity$1("postgres/service.yaml"));
});

cljs.spec.alpha.def_impl(new cljs.core.Symbol("dda.c4k-common.postgres","generate-service","dda.c4k-common.postgres/generate-service",-429869818,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","fspec","cljs.spec.alpha/fspec",-1289128341,null),new cljs.core.Keyword(null,"args","args",1315556576),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null)),new cljs.core.Keyword(null,"fn","fn",-1175266204),null,new cljs.core.Keyword(null,"ret","ret",-468222814),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null)),cljs.spec.alpha.fspec_impl(cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null)),cljs.spec.alpha.cat_impl(cljs.core.PersistentVector.EMPTY,cljs.core.PersistentVector.EMPTY,cljs.core.PersistentVector.EMPTY),null,null),cljs.core.list(new cljs.core.Symbol("cljs.spec.alpha","cat","cljs.spec.alpha/cat",-1471398329,null)),cljs.spec.alpha.spec_impl.cljs$core$IFn$_invoke$arity$4(new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),dda.c4k_common.predicate.map_or_seq_QMARK_,null,null),new cljs.core.Symbol("dda.c4k-common.predicate","map-or-seq?","dda.c4k-common.predicate/map-or-seq?",-1443028642,null),null,null,null));

//# sourceMappingURL=dda.c4k_common.postgres.js.map
