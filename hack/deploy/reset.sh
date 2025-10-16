#!/bin/bash
set -e

TT_ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
source "$TT_ROOT/utils.sh"

namespace="$1"

echo "Deleting train-ticket resources in namespace: $namespace"

# Delete K8s yamls
if [ -d "deployment/kubernetes-manifests/quickstart-k8s/yamls" ]; then
  kubectl delete -f deployment/kubernetes-manifests/quickstart-k8s/yamls -n $namespace --ignore-not-found
fi

# Delete Skywalking (if deployed)
if [ -d "deployment/kubernetes-manifests/skywalking" ]; then
  kubectl delete -f deployment/kubernetes-manifests/skywalking -n $namespace --ignore-not-found
fi

# Uninstall Helm releases
helm ls -n $namespace | grep '^ts-' | awk '{print $1}' | xargs -r helm uninstall -n $namespace
helm uninstall rabbitmq -n $namespace || true
helm uninstall nacos -n $namespace || true
helm uninstall nacosdb -n $namespace || true
helm uninstall tsdb -n $namespace || true

echo "Cleanup complete."
