
## Run Eclipse Che using minikube

Prerequisites: `kubectl`, `helm`, `minikube`, and `chectl` installed.

Start a cluster:

```
minikube start --memory 4096 --vm-driver=hyperv --docker-opt userland-proxy=false --addons=ingress
```

Determine IP Address:

```
minikube ip
```

Start Che server:

```
chectl server:start -p minikube --self-signed-cert -b <minikube ip>.nip.io
```
