# Forgejo Runner Setup & Config

## Runner Installation

  [Via OCI Image](https://forgejo.org/docs/next/admin/runner-installation/#oci-image-installation)  
- Wir verwenden offline registration da wir eine Kubernetes Resource erstellen
    - Vermutlich können wir im ersten Schritt händisch ein secret generieren und im gopass unterbringen
        - Das secret wird dann an die forgejo instanz und den runner verteilt

## Runner Config

- Es wird dind (Docker in Docker) verwendet
- Configuration [Via configmap](https://code.forgejo.org/forgejo/runner/issues/132#issuecomment-4848)
- Registrierung
    - Runner -> Forgejo Instanz
    -```
     forgejo forgejo-cli actions register --name runner-name --scope myorganization \
         --secret 7c31591e8b67225a116d4a4519ea8e507e08f71f
     ```
    - Forgejo Instanz -> Runner
        -
            ```
            forgejo-runner create-runner-file --instance https://example.conf \
                    --secret 7c31591e8b67225a116d4a4519ea8e507e08f71f
            ```
    - The secret must be a 40-character long string of hexadecimal numbers. 
        The first 16 characters will be used as an identifier for the runner,   
        while the rest is the actual secret.  
- Cache
    - Wird in der config definiert

## Links

- Labels: https://forgejo.org/docs/latest/admin/actions/#choosing-labels
- Runner Config: https://forgejo.org/docs/next/admin/runner-installation/#configuration
- Runner Kubernetes Resource Examples: https://code.forgejo.org/forgejo/runner/src/branch/main/examples/kubernetes/dind-docker.yaml
