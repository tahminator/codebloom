import type {
  App_service_spec,
  App_spec,
  Apps_image_source_spec,
  App_variable_definition,
} from "../.github/scripts/node_modules/@digitalocean/dots";

const DIGITALOCEAN_BASE_IMAGE: Apps_image_source_spec = {
  registry: "tahminator",
  registryType: "DOCKER_HUB",
  repository: "codebloom",
  // override tag
  tag: "latest",
};

const DIGITALOCEAN_BASE_SERVICE: App_service_spec = {
  name: "codebloom",
  healthCheck: {
    failureThreshold: 9,
    httpPath: "/api",
    periodSeconds: 10,
    successThreshold: 1,
    timeoutSeconds: 1,
  },
  livenessHealthCheck: {
    failureThreshold: 9,
    httpPath: "/api",
    periodSeconds: 10,
    successThreshold: 1,
    timeoutSeconds: 1,
  },
  httpPort: 8080,
  instanceCount: 1,
  instanceSizeSlug: "apps-s-1vcpu-1gb-fixed",
};

const DIGITALOCEAN_BASE_SPEC: App_spec = {
  region: "nyc",
  ingress: {
    rules: [
      {
        component: {
          name: "codebloom",
        },
        match: {
          path: {
            prefix: "/",
          },
        },
      },
    ],
  },
};

export function prodSpec(envs: App_variable_definition[]): App_spec {
  return {
    ...DIGITALOCEAN_BASE_SPEC,
    name: "codebloom-prod",
    services: [
      {
        ...DIGITALOCEAN_BASE_SERVICE,
        instanceSizeSlug: "apps-s-1vcpu-2gb",
        image: {
          ...DIGITALOCEAN_BASE_IMAGE,
          tag: "latest",
        },
        envs,
      },
    ],
    domains: [
      {
        domain: "codebloom.patinanetwork.org",
        type: "PRIMARY",
      },
    ],
  };
}

export function stgSpec(envs: App_variable_definition[]): App_spec {
  return {
    ...DIGITALOCEAN_BASE_SPEC,
    name: "codebloom-staging",
    services: [
      {
        ...DIGITALOCEAN_BASE_SERVICE,
        image: {
          ...DIGITALOCEAN_BASE_IMAGE,
          tag: "staging-latest",
        },
        envs,
      },
    ],
    domains: [
      {
        domain: "stg.codebloom.patinanetwork.org",
        type: "PRIMARY",
      },
    ],
  };
}
