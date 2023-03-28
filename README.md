# convention 4 kubernetes: c4k-forgejo
[![Clojars Project](https://img.shields.io/clojars/v/org.domaindrivenarchitecture/c4k-forgejo.svg)](https://clojars.org/org.domaindrivenarchitecture/c4k-forgejo) [![pipeline status](https://gitlab.com/domaindrivenarchitecture/c4k-forgejo/badges/master/pipeline.svg)](https://gitlab.com/domaindrivenarchitecture/c4k-forgejo/-/commits/main) 

[<img src="https://domaindrivenarchitecture.org/img/delta-chat.svg" width=20 alt="DeltaChat"> chat over e-mail](mailto:buero@meissa-gmbh.de?subject=community-chat) | [<img src="https://meissa-gmbh.de/img/community/Mastodon_Logotype.svg" width=20 alt="team@social.meissa-gmbh.de"> team@social.meissa-gmbh.de](https://social.meissa-gmbh.de/@team) | [Website & Blog](https://domaindrivenarchitecture.org)

## Purpose

c4k-forgejo provides a k8s deployment file for forgejo containing:
* forgejo
* ingress having a letsencrypt managed certificate
* postgres database


## Try out

Click on the image to try out live in your browser:

[![Try it out](doc/tryItOut.png "Try out yourself")](https://domaindrivenarchitecture.org/pages/dda-provision/c4k-forgejo/)

Your input will stay in your browser. No server interaction is required.


## Forgejo setup

After having deployed the yaml-file generated by the c4k-forgejo module you need to complete the setup for forgejo:

* Open the URL of your forgejo-server, and you will be shown a configuration page.
* Adjust the settings according to your needs
* Add the administrator's data (name, password and email) and submit the page.
* The required database will be created and the forgejo setup will be completed.
* The SSH-URL for a repo has the format: "ssh://git@domain:2222/[username]/[repo].git
  Example: "git clone ssh://git@repo.test.meissa.de:2222/myuser/c4k-forgejo.git"


## License

Copyright © 2022 meissa GmbH
Licensed under the [Apache License, Version 2.0](LICENSE) (the "License")
Pls. find licenses of our subcomponents [here](doc/SUBCOMPONENT_LICENSE)