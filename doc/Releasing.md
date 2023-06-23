# Release process

## ... for testing (snapshots)

Make sure your clojars.org credentials are correctly set in your ~/.lein/profiles.clj file.

``` bash
git add .
git commit
```

``` bash
lein deploy # or lein deploy clojars
```

## ... for stable release

Make sure tags are protected in gitlab:
Repository Settings -> Protected Tags -> set \*.\*.\* as tag and save.

``` bash
git checkout main # for old projects replace main with master
git add .
git commit 
```

Execute tests

``` bash
shadow-cljs compile test
node target/node-tests.js
lein test
```

Release with type (NONE, PATCH, MINOR, MAJOR):
``` bash
RELEASE_TYPE=[TYPE] pyb prepare_release after_publish

```

Done.
