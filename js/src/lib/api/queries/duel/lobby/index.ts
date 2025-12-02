import { ApiURL } from "@/lib/api/common/apiURL";
import { useMutation } from "@tanstack/react-query";

export const useCreateLobbyMutation = () => {
  return useMutation({
    mutationFn: createLobby,
  });
};

async function createLobby() {
  const { url, method, res } = ApiURL.create("/api/duel/party/create", {
    method: "POST",
  });
  const response = await fetch(url, {
    method
  });

  const json = res(await response.json());

  if (json.success) {
    return {
      lobbyCode: "0XXXX" // Placeholder
    };
  }

  return { lobbyCode: "" };
}

export const useJoinLobbyMutation = () => {
  return useMutation({
    mutationFn: joinLobby,
  });
}

async function joinLobby() {
  const { url, method, res } = ApiURL.create("/api/duel/lobby/join", {
    method: "POST",
  });
  const response = await fetch(url, {
    method
  });

  const json = res(await response.json());

  if (json.success) {
    return {
      lobbyCode: "0XXXX" // Placeholder
    };
  }

  return { lobbyCode: "" };
}