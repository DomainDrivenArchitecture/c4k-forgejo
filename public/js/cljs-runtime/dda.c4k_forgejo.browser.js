goog.provide('dda.c4k_forgejo.browser');
dda.c4k_forgejo.browser.generate_group = (function dda$c4k_forgejo$browser$generate_group(name,content){
return new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"element","element",1974019749),new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.Keyword(null,"attrs","attrs",-2090668713),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"class","class",-2030961996),"rounded border border-3  m-3 p-2"], null),new cljs.core.Keyword(null,"content","content",15833224),new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [new cljs.core.PersistentArrayMap(null, 4, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"element","element",1974019749),new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Keyword(null,"b","b",1482224470),new cljs.core.Keyword(null,"attrs","attrs",-2090668713),new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"style","style",-496642736),"z-index: 1; position: relative; top: -1.3rem;"], null),new cljs.core.Keyword(null,"content","content",15833224),name], null),new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"element","element",1974019749),new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Keyword(null,"fieldset","fieldset",-1949770816),new cljs.core.Keyword(null,"content","content",15833224),content], null)], null)], null)], null);
});
dda.c4k_forgejo.browser.generate_content = (function dda$c4k_forgejo$browser$generate_content(){
return dda.c4k_common.common.concat_vec.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.assoc.cljs$core$IFn$_invoke$arity$3(dda.c4k_common.browser.generate_needs_validation(),new cljs.core.Keyword(null,"content","content",15833224),dda.c4k_common.common.concat_vec.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([dda.c4k_forgejo.browser.generate_group("domain",dda.c4k_common.common.concat_vec.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([dda.c4k_common.browser.generate_input_field("fqdn","Your fqdn:","repo.test.de"),dda.c4k_common.browser.generate_input_field("deploy-federated","Deploy a federated version of forgejo:","false"),dda.c4k_common.browser.generate_input_field("mailer-from","Your mailer email address:","test@test.de"),dda.c4k_common.browser.generate_input_field("mailer-host","Your mailer host:","test.de"),dda.c4k_common.browser.generate_input_field("mailer-port","Your mailer port:","123"),dda.c4k_common.browser.generate_input_field("service-noreply-address","Your noreply domain:","test.de"),dda.c4k_common.browser.generate_input_field("issuer","(Optional) Your issuer prod/staging:",""),dda.c4k_common.browser.generate_input_field("app-name","(Optional) Your app name:",""),dda.c4k_common.browser.generate_input_field("domain-whitelist","(Optional) Domain whitelist for registration email-addresses:","")], 0))),dda.c4k_forgejo.browser.generate_group("provider",dda.c4k_common.common.concat_vec.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([dda.c4k_common.browser.generate_input_field("volume-total-storage-size","Your forgejo volume-total-storage-size:","20")], 0))),dda.c4k_forgejo.browser.generate_group("credentials",dda.c4k_common.browser.generate_text_area("auth","Your auth.edn:","{:postgres-db-user \"forgejo\"\n         :postgres-db-password \"forgejo-db-password\"\n         :mailer-user \"test@test.de\"\n         :mailer-pw \"mail-test-password\"}","5")),new cljs.core.PersistentVector(null, 1, 5, cljs.core.PersistentVector.EMPTY_NODE, [dda.c4k_common.browser.generate_br()], null),dda.c4k_common.browser.generate_button("generate-button","Generate c4k yaml")], 0)))], null),dda.c4k_common.browser.generate_output("c4k-forgejo-output","Your c4k deployment.yaml:","25")], 0));
});
dda.c4k_forgejo.browser.generate_content_div = (function dda$c4k_forgejo$browser$generate_content_div(){
return new cljs.core.PersistentArrayMap(null, 3, [new cljs.core.Keyword(null,"type","type",1174270348),new cljs.core.Keyword(null,"element","element",1974019749),new cljs.core.Keyword(null,"tag","tag",-1290361223),new cljs.core.Keyword(null,"div","div",1057191632),new cljs.core.Keyword(null,"content","content",15833224),dda.c4k_forgejo.browser.generate_content()], null);
});
dda.c4k_forgejo.browser.config_from_document = (function dda$c4k_forgejo$browser$config_from_document(){
var issuer = dda.c4k_common.browser.get_content_from_element.cljs$core$IFn$_invoke$arity$variadic("issuer",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));
var app_name = dda.c4k_common.browser.get_content_from_element.cljs$core$IFn$_invoke$arity$variadic("app-name",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));
var domain_whitelist = dda.c4k_common.browser.get_content_from_element.cljs$core$IFn$_invoke$arity$variadic("domain-whitelist",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));
return cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.PersistentArrayMap(null, 7, [new cljs.core.Keyword(null,"fqdn","fqdn",-494775377),dda.c4k_common.browser.get_content_from_element("fqdn"),new cljs.core.Keyword(null,"deploy-federated","deploy-federated",-1957879598),dda.c4k_common.browser.get_content_from_element("deploy-federated"),new cljs.core.Keyword(null,"mailer-from","mailer-from",46746467),dda.c4k_common.browser.get_content_from_element("mailer-from"),new cljs.core.Keyword(null,"mailer-host","mailer-host",-1829907585),dda.c4k_common.browser.get_content_from_element("mailer-host"),new cljs.core.Keyword(null,"mailer-port","mailer-port",690436546),dda.c4k_common.browser.get_content_from_element("mailer-port"),new cljs.core.Keyword(null,"service-noreply-address","service-noreply-address",-205702396),dda.c4k_common.browser.get_content_from_element("service-noreply-address"),new cljs.core.Keyword(null,"volume-total-storage-size","volume-total-storage-size",1173468179),dda.c4k_common.browser.get_content_from_element.cljs$core$IFn$_invoke$arity$variadic("volume-total-storage-size",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"deserializer","deserializer",858642506),parseInt], 0))], null),(((!(clojure.string.blank_QMARK_(issuer))))?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"issuer","issuer",-1199257898),issuer], null):null),(((!(clojure.string.blank_QMARK_(app_name))))?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"default-app-name","default-app-name",460840317),app_name], null):null),(((!(clojure.string.blank_QMARK_(domain_whitelist))))?new cljs.core.PersistentArrayMap(null, 1, [new cljs.core.Keyword(null,"service-domain-whitelist","service-domain-whitelist",-547788367),domain_whitelist], null):null)], 0));
});
dda.c4k_forgejo.browser.validate_all_BANG_ = (function dda$c4k_forgejo$browser$validate_all_BANG_(){
dda.c4k_common.browser.validate_BANG_("fqdn",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","fqdn","dda.c4k-forgejo.forgejo/fqdn",-686210095));

dda.c4k_common.browser.validate_BANG_("deploy-federated",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","deploy-federated","dda.c4k-forgejo.forgejo/deploy-federated",-14143504));

dda.c4k_common.browser.validate_BANG_("mailer-from",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","mailer-from","dda.c4k-forgejo.forgejo/mailer-from",-1720436227));

dda.c4k_common.browser.validate_BANG_("mailer-host",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","mailer-host","dda.c4k-forgejo.forgejo/mailer-host",239649441));

dda.c4k_common.browser.validate_BANG_("mailer-port",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","mailer-port","dda.c4k-forgejo.forgejo/mailer-port",-1400754980));

dda.c4k_common.browser.validate_BANG_("service-noreply-address",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","service-noreply-address","dda.c4k-forgejo.forgejo/service-noreply-address",1964518762));

dda.c4k_common.browser.validate_BANG_.cljs$core$IFn$_invoke$arity$variadic("issuer",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","issuer","dda.c4k-forgejo.forgejo/issuer",941217312),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));

dda.c4k_common.browser.validate_BANG_.cljs$core$IFn$_invoke$arity$variadic("app-name",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","default-app-name","dda.c4k-forgejo.forgejo/default-app-name",-1742542185),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));

dda.c4k_common.browser.validate_BANG_.cljs$core$IFn$_invoke$arity$variadic("domain-whitelist",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","service-domain-whitelist","dda.c4k-forgejo.forgejo/service-domain-whitelist",1677619847),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"optional","optional",2053951509),true], 0));

dda.c4k_common.browser.validate_BANG_.cljs$core$IFn$_invoke$arity$variadic("volume-total-storage-size",new cljs.core.Keyword("dda.c4k-forgejo.forgejo","volume-total-storage-size","dda.c4k-forgejo.forgejo/volume-total-storage-size",-1034386183),cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"deserializer","deserializer",858642506),parseInt], 0));

dda.c4k_common.browser.validate_BANG_.cljs$core$IFn$_invoke$arity$variadic("auth",dda.c4k_forgejo.forgejo.auth_QMARK_,cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"deserializer","deserializer",858642506),cljs.tools.reader.edn.read_string], 0));

return dda.c4k_common.browser.set_form_validated_BANG_();
});
dda.c4k_forgejo.browser.add_validate_listener = (function dda$c4k_forgejo$browser$add_validate_listener(name){
return dda.c4k_common.browser.get_element_by_id(name).addEventListener("blur",(function (){
return dda.c4k_forgejo.browser.validate_all_BANG_();
}));
});
dda.c4k_forgejo.browser.init = (function dda$c4k_forgejo$browser$init(){
dda.c4k_common.browser.append_hickory(dda.c4k_forgejo.browser.generate_content_div());

document.getElementById("generate-button").addEventListener("click",(function (){
dda.c4k_forgejo.browser.validate_all_BANG_();

return dda.c4k_common.browser.set_output_BANG_(dda.c4k_common.common.generate_common(dda.c4k_forgejo.browser.config_from_document(),dda.c4k_common.browser.get_content_from_element.cljs$core$IFn$_invoke$arity$variadic("auth",cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([new cljs.core.Keyword(null,"deserializer","deserializer",858642506),cljs.tools.reader.edn.read_string], 0)),dda.c4k_forgejo.forgejo.config_defaults,dda.c4k_forgejo.core.k8s_objects));
}));

dda.c4k_forgejo.browser.add_validate_listener("fqdn");

dda.c4k_forgejo.browser.add_validate_listener("deploy-federated");

dda.c4k_forgejo.browser.add_validate_listener("mailer-from");

dda.c4k_forgejo.browser.add_validate_listener("mailer-host");

dda.c4k_forgejo.browser.add_validate_listener("mailer-port");

dda.c4k_forgejo.browser.add_validate_listener("service-noreply-address");

dda.c4k_forgejo.browser.add_validate_listener("app-name");

dda.c4k_forgejo.browser.add_validate_listener("domain-whitelist");

dda.c4k_forgejo.browser.add_validate_listener("volume-total-storage-size");

dda.c4k_forgejo.browser.add_validate_listener("issuer");

return dda.c4k_forgejo.browser.add_validate_listener("auth");
});

//# sourceMappingURL=dda.c4k_forgejo.browser.js.map
