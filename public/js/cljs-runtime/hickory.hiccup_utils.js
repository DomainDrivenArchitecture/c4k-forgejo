goog.provide('hickory.hiccup_utils');
/**
 * Given two possible indexes, returns the lesser that is not -1. If both
 * are -1, then -1 is returned. Useful for searching strings for multiple
 * markers, as many routines will return -1 for not found.
 * 
 * Examples: (first-idx -1 -1) => -1
 *           (first-idx -1 2) => 2
 *           (first-idx 5 -1) => 5
 *           (first-idx 5 3) => 3
 */
hickory.hiccup_utils.first_idx = (function hickory$hiccup_utils$first_idx(a,b){
if((a === (-1))){
return b;
} else {
if((b === (-1))){
return a;
} else {
var x__5133__auto__ = a;
var y__5134__auto__ = b;
return ((x__5133__auto__ < y__5134__auto__) ? x__5133__auto__ : y__5134__auto__);
}
}
});
hickory.hiccup_utils.index_of = (function hickory$hiccup_utils$index_of(var_args){
var G__10870 = arguments.length;
switch (G__10870) {
case 2:
return hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2((arguments[(0)]),(arguments[(1)]));

break;
case 3:
return hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3((arguments[(0)]),(arguments[(1)]),(arguments[(2)]));

break;
default:
throw (new Error(["Invalid arity: ",cljs.core.str.cljs$core$IFn$_invoke$arity$1(arguments.length)].join('')));

}
});

(hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2 = (function (s,c){
return s.indexOf(c);
}));

(hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3 = (function (s,c,idx){
return s.indexOf(c,idx);
}));

(hickory.hiccup_utils.index_of.cljs$lang$maxFixedArity = 3);

/**
 * clojure.string/split is a wrapper on java.lang.String/split with the limit
 * parameter equal to 0, which keeps leading empty strings, but discards
 * trailing empty strings. This makes no sense, so we have to write our own
 * to keep the trailing empty strings.
 */
hickory.hiccup_utils.split_keep_trailing_empty = (function hickory$hiccup_utils$split_keep_trailing_empty(s,re){
return clojure.string.split.cljs$core$IFn$_invoke$arity$3(s,re,(-1));
});
/**
 * Given a hiccup tag element, returns true iff the tag is in 'valid' hiccup
 * format. Which in this function means:
 *    1. Tag name is non-empty.
 *    2. If there is an id, there is only one.
 *    3. If there is an id, it is nonempty.
 *    4. If there is an id, it comes before any classes.
 *    5. Any class name is nonempty.
 */
hickory.hiccup_utils.tag_well_formed_QMARK_ = (function hickory$hiccup_utils$tag_well_formed_QMARK_(tag_elem){
var tag_elem__$1 = cljs.core.name(tag_elem);
var hash_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,"#") | (0));
var dot_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,".") | (0));
var tag_cutoff = hickory.hiccup_utils.first_idx(hash_idx,dot_idx);
var and__5043__auto__ = ((0) < ((tag_elem__$1).length));
if(and__5043__auto__){
var and__5043__auto____$1 = (((tag_cutoff === (-1)))?true:(tag_cutoff > (0)));
if(and__5043__auto____$1){
var and__5043__auto____$2 = (((hash_idx === (-1)))?true:((((-1) === hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3(tag_elem__$1,"#",(hash_idx + (1))))) && (((hash_idx + (1)) < hickory.hiccup_utils.first_idx(hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3(tag_elem__$1,".",(hash_idx + (1))),((tag_elem__$1).length))))));
if(and__5043__auto____$2){
var and__5043__auto____$3 = ((((cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(hash_idx,(-1))) && (cljs.core.not_EQ_.cljs$core$IFn$_invoke$arity$2(dot_idx,(-1)))))?(hash_idx < dot_idx):true);
if(and__5043__auto____$3){
if((dot_idx === (-1))){
return true;
} else {
var classes = tag_elem__$1.substring((dot_idx + (1)));
return cljs.core.every_QMARK_((function (p1__10872_SHARP_){
return ((0) < cljs.core.count(p1__10872_SHARP_));
}),hickory.hiccup_utils.split_keep_trailing_empty(classes,/\./));
}
} else {
return and__5043__auto____$3;
}
} else {
return and__5043__auto____$2;
}
} else {
return and__5043__auto____$1;
}
} else {
return and__5043__auto__;
}
});
/**
 * Given a well-formed hiccup tag element, return just the tag name as
 *   a string.
 */
hickory.hiccup_utils.tag_name = (function hickory$hiccup_utils$tag_name(tag_elem){
var tag_elem__$1 = cljs.core.name(tag_elem);
var hash_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,"#") | (0));
var dot_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,".") | (0));
var cutoff = hickory.hiccup_utils.first_idx(hash_idx,dot_idx);
if((cutoff === (-1))){
return tag_elem__$1;
} else {
return tag_elem__$1.substring((0),cutoff);
}
});
/**
 * Given a well-formed hiccup tag element, return a vector containing
 * any class names included in the tag, as strings. Ignores the hiccup
 * requirement that any id on the tag must come
 * first. Example: :div.foo.bar => ["foo" "bar"].
 */
hickory.hiccup_utils.class_names = (function hickory$hiccup_utils$class_names(tag_elem){
var tag_elem__$1 = cljs.core.name(tag_elem);
var curr_dot = hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,".");
var classes = cljs.core.transient$(cljs.core.PersistentVector.EMPTY);
while(true){
if((curr_dot === (-1))){
return cljs.core.persistent_BANG_(classes);
} else {
var next_dot = hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3(tag_elem__$1,".",(curr_dot + (1)));
var next_hash = hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3(tag_elem__$1,"#",(curr_dot + (1)));
var cutoff = hickory.hiccup_utils.first_idx(next_dot,next_hash);
if((cutoff === (-1))){
var G__10899 = next_dot;
var G__10900 = cljs.core.conj_BANG_.cljs$core$IFn$_invoke$arity$2(classes,tag_elem__$1.substring((curr_dot + (1))));
curr_dot = G__10899;
classes = G__10900;
continue;
} else {
var G__10901 = next_dot;
var G__10902 = cljs.core.conj_BANG_.cljs$core$IFn$_invoke$arity$2(classes,tag_elem__$1.substring((curr_dot + (1)),cutoff));
curr_dot = G__10901;
classes = G__10902;
continue;
}
}
break;
}
});
/**
 * Given a well-formed hiccup tag element, return a string containing
 * the id, or nil if there isn't one.
 */
hickory.hiccup_utils.id = (function hickory$hiccup_utils$id(tag_elem){
var tag_elem__$1 = cljs.core.name(tag_elem);
var hash_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$2(tag_elem__$1,"#") | (0));
var next_dot_idx = (hickory.hiccup_utils.index_of.cljs$core$IFn$_invoke$arity$3(tag_elem__$1,".",hash_idx) | (0));
if((hash_idx === (-1))){
return null;
} else {
if((next_dot_idx === (-1))){
return tag_elem__$1.substring((hash_idx + (1)));
} else {
return tag_elem__$1.substring((hash_idx + (1)),next_dot_idx);
}
}
});
/**
 * Given a sequence of hiccup forms, presumably the content forms of another
 * hiccup element, return a new sequence with any sequence elements expanded
 * into the main sequence. This logic does not apply recursively, so sequences
 * inside sequences won't be expanded out. Also note that this really only
 * applies to sequences; things that seq? returns true on. So this excludes
 * vectors.
 *   (expand-content-seqs [1 '(2 3) (for [x [1 2 3]] (* x 2)) [5]])
 *   ==> (1 2 3 2 4 6 [5])
 */
hickory.hiccup_utils.expand_content_seqs = (function hickory$hiccup_utils$expand_content_seqs(content){
var remaining_content = content;
var result = cljs.core.transient$(cljs.core.PersistentVector.EMPTY);
while(true){
if((remaining_content == null)){
return cljs.core.persistent_BANG_(result);
} else {
if(cljs.core.seq_QMARK_(cljs.core.first(remaining_content))){
var G__10905 = cljs.core.next(remaining_content);
var G__10906 = (function (){var remaining_seq = cljs.core.first(remaining_content);
var result__$1 = result;
while(true){
if((remaining_seq == null)){
return result__$1;
} else {
var G__10907 = cljs.core.next(remaining_seq);
var G__10908 = cljs.core.conj_BANG_.cljs$core$IFn$_invoke$arity$2(result__$1,cljs.core.first(remaining_seq));
remaining_seq = G__10907;
result__$1 = G__10908;
continue;
}
break;
}
})();
remaining_content = G__10905;
result = G__10906;
continue;
} else {
var G__10909 = cljs.core.next(remaining_content);
var G__10910 = cljs.core.conj_BANG_.cljs$core$IFn$_invoke$arity$2(result,cljs.core.first(remaining_content));
remaining_content = G__10909;
result = G__10910;
continue;
}
}
break;
}
});
/**
 * Given a well-formed hiccup form, ensure that it is in the form
 *   [tag attributes content1 ... contentN].
 * That is, an unadorned tag name (keyword, lowercase), all attributes in the
 * attribute map in the second element, and then any children. Note that this
 * does not happen recursively; content is not modified.
 */
hickory.hiccup_utils.normalize_element = (function hickory$hiccup_utils$normalize_element(hiccup_form){
var vec__10880 = hiccup_form;
var seq__10881 = cljs.core.seq(vec__10880);
var first__10882 = cljs.core.first(seq__10881);
var seq__10881__$1 = cljs.core.next(seq__10881);
var tag_elem = first__10882;
var content = seq__10881__$1;
if((!(hickory.hiccup_utils.tag_well_formed_QMARK_(tag_elem)))){
throw cljs.core.ex_info.cljs$core$IFn$_invoke$arity$2(["Invalid input: Tag element",cljs.core.str.cljs$core$IFn$_invoke$arity$1(tag_elem),"is not well-formed."].join(''),cljs.core.PersistentArrayMap.EMPTY);
} else {
}

var tag_name = cljs.core.keyword.cljs$core$IFn$_invoke$arity$1(clojure.string.lower_case(hickory.hiccup_utils.tag_name(tag_elem)));
var tag_classes = hickory.hiccup_utils.class_names(tag_elem);
var tag_id = hickory.hiccup_utils.id(tag_elem);
var tag_attrs = new cljs.core.PersistentArrayMap(null, 2, [new cljs.core.Keyword(null,"id","id",-1388402092),tag_id,new cljs.core.Keyword(null,"class","class",-2030961996),(((!(cljs.core.empty_QMARK_(tag_classes))))?clojure.string.join.cljs$core$IFn$_invoke$arity$2(" ",tag_classes):null)], null);
var vec__10883 = ((cljs.core.map_QMARK_(cljs.core.first(content)))?new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [cljs.core.first(content),cljs.core.rest(content)], null):new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [null,content], null));
var map_attrs = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__10883,(0),null);
var content__$1 = cljs.core.nth.cljs$core$IFn$_invoke$arity$3(vec__10883,(1),null);
var attrs = cljs.core.merge.cljs$core$IFn$_invoke$arity$variadic(cljs.core.prim_seq.cljs$core$IFn$_invoke$arity$2([tag_attrs,map_attrs], 0));
return cljs.core.apply.cljs$core$IFn$_invoke$arity$4(cljs.core.vector,tag_name,attrs,content__$1);
});
/**
 * Given a well-formed hiccup form, recursively normalizes it, so that it and
 * all children elements will also be normalized. A normalized form is in the
 * form
 *   [tag attributes content1 ... contentN].
 * That is, an unadorned tag name (keyword, lowercase), all attributes in the
 * attribute map in the second element, and then any children. Any content
 * that is a sequence is also expanded out into the main sequence of content
 * items.
 */
hickory.hiccup_utils.normalize_form = (function hickory$hiccup_utils$normalize_form(form){
if(typeof form === 'string'){
return form;
} else {
var vec__10890 = hickory.hiccup_utils.normalize_element(form);
var seq__10891 = cljs.core.seq(vec__10890);
var first__10892 = cljs.core.first(seq__10891);
var seq__10891__$1 = cljs.core.next(seq__10891);
var tag = first__10892;
var first__10892__$1 = cljs.core.first(seq__10891__$1);
var seq__10891__$2 = cljs.core.next(seq__10891__$1);
var attrs = first__10892__$1;
var contents = seq__10891__$2;
return cljs.core.apply.cljs$core$IFn$_invoke$arity$4(cljs.core.vector,tag,attrs,cljs.core.map.cljs$core$IFn$_invoke$arity$2((function (p1__10886_SHARP_){
if(cljs.core.vector_QMARK_(p1__10886_SHARP_)){
return (hickory.hiccup_utils.normalize_form.cljs$core$IFn$_invoke$arity$1 ? hickory.hiccup_utils.normalize_form.cljs$core$IFn$_invoke$arity$1(p1__10886_SHARP_) : hickory.hiccup_utils.normalize_form.call(null,p1__10886_SHARP_));
} else {
return p1__10886_SHARP_;
}
}),hickory.hiccup_utils.expand_content_seqs(contents)));
}
});

//# sourceMappingURL=hickory.hiccup_utils.js.map
