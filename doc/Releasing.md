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

Open package.json, find ":version" keyword and remove "-SNAPSHOT" from version number.

``` bash
git add .
git commit -m "Release [version]"
lein release
git push --follow-tags
```

Open package.json again, increase version increment by one and add "-SNAPSHOT".

``` bash
git commit -am "version bump"
git push
```

Done.
