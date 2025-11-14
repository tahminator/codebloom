import { useMutation, useQueryClient } from "@tanstack/react-query";

import { ApiURL } from "../../common/apiURL";

export const useCreateParty = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createParty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    },
  });
};

async function createParty() {
  const { url, method, res } = ApiURL.create("/api/duel/party/create", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
  });
  return res(await response.json());
}


export const useJoinParty = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: joinParty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    }
  })
}

async function joinParty(joinPartyBody: { code: string }) {
  const { url, method, res } = ApiURL.create(
    "/api/duel/lobby/join",
    {
      method: "POST",
    }
  );
  const response = await fetch(url, {
    method,
    body: joinPartyBody.code
  })
  return res(await response.json());
}