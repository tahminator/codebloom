import { Coolify } from "coolify/src";

export async function initClient(bearerAuth: string, serverURL: string) {
  return new Coolify({
    bearerAuth,
    serverURL,
  });
}
