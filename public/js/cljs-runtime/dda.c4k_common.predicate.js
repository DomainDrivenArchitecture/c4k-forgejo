goog.provide('dda.c4k_common.predicate');
dda.c4k_common.predicate.bash_env_string_QMARK_ = (function dda$c4k_common$predicate$bash_env_string_QMARK_(input){
return ((typeof input === 'string') && (cljs.core.not(cljs.core.re_matches(/.*['\"\$]+.*/,input))));
});
dda.c4k_common.predicate.fqdn_string_QMARK_ = (function dda$c4k_common$predicate$fqdn_string_QMARK_(input){
return ((typeof input === 'string') && ((!((cljs.core.re_matches(/(?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{0,62}[a-zA-Z0-9]\.)+[a-zA-Z]{2,63}$)/,input) == null)))));
});
dda.c4k_common.predicate.string_of_separated_by_QMARK_ = (function dda$c4k_common$predicate$string_of_separated_by_QMARK_(spec_function,separator,input){
return cljs.core.every_QMARK_(cljs.core.true_QMARK_,cljs.core.map.cljs$core$IFn$_invoke$arity$2(spec_function,clojure.string.split.cljs$core$IFn$_invoke$arity$2(input,separator)));
});
dda.c4k_common.predicate.letsencrypt_issuer_QMARK_ = (function dda$c4k_common$predicate$letsencrypt_issuer_QMARK_(input){
return cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["staging",null,"prod",null], null), null),input);
});
dda.c4k_common.predicate.stage_QMARK_ = (function dda$c4k_common$predicate$stage_QMARK_(input){
return cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 5, ["int",null,"prod",null,"dev",null,"acc",null,"test",null], null), null),input);
});
dda.c4k_common.predicate.map_or_seq_QMARK_ = (function dda$c4k_common$predicate$map_or_seq_QMARK_(input){
return ((cljs.core.map_QMARK_(input)) || (cljs.core.seq_QMARK_(input)));
});
dda.c4k_common.predicate.pvc_storage_class_name_QMARK_ = (function dda$c4k_common$predicate$pvc_storage_class_name_QMARK_(input){
return cljs.core.contains_QMARK_(new cljs.core.PersistentHashSet(null, new cljs.core.PersistentArrayMap(null, 2, ["local-path",null,"manual",null], null), null),input);
});
dda.c4k_common.predicate.port_number_QMARK_ = (function dda$c4k_common$predicate$port_number_QMARK_(input){
return ((cljs.core.integer_QMARK_(input)) && ((((input > (0))) && ((input <= (65535))))));
});
dda.c4k_common.predicate.host_and_port_string_QMARK_ = (function dda$c4k_common$predicate$host_and_port_string_QMARK_(input){
var and__5043__auto__ = typeof input === 'string';
if(and__5043__auto__){
var split_string = clojure.string.split.cljs$core$IFn$_invoke$arity$2(input,/:/);
return ((cljs.core._EQ_.cljs$core$IFn$_invoke$arity$2(cljs.core.count(split_string),(2))) && (((dda.c4k_common.predicate.fqdn_string_QMARK_(cljs.core.first(split_string))) && (dda.c4k_common.predicate.port_number_QMARK_(cljs.reader.read_string.cljs$core$IFn$_invoke$arity$1(cljs.core.second(split_string)))))));
} else {
return and__5043__auto__;
}
});
dda.c4k_common.predicate.integer_string_QMARK_ = (function dda$c4k_common$predicate$integer_string_QMARK_(input){
return ((typeof input === 'string') && ((((!((cljs.core.re_matches(/^\d+$/,input) == null)))) && (cljs.core.integer_QMARK_(cljs.reader.read_string.cljs$core$IFn$_invoke$arity$1(input))))));
});
dda.c4k_common.predicate.string_sequence_QMARK_ = (function dda$c4k_common$predicate$string_sequence_QMARK_(input){
return ((cljs.core.sequential_QMARK_(input)) && (cljs.core.every_QMARK_(cljs.core.true_QMARK_,cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__6159_SHARP_){
return typeof p1__6159_SHARP_ === 'string';
}),input))));
});
dda.c4k_common.predicate.int_gt_n_QMARK_ = (function dda$c4k_common$predicate$int_gt_n_QMARK_(n,input){
return ((cljs.core.int_QMARK_(input)) && ((input > n)));
});
dda.c4k_common.predicate.str_or_number_QMARK_ = (function dda$c4k_common$predicate$str_or_number_QMARK_(input){
return ((typeof input === 'string') || (typeof input === 'number'));
});

//# sourceMappingURL=dda.c4k_common.predicate.js.map
