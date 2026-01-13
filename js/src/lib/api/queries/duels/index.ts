import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { ApiURL } from "../../common/apiURL";

// Start
export const useStartDuelMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: startDuel,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    },
  });
};

async function startDuel() {
  const { url, method, res } = ApiURL.create("/api/duel/start", {
    method: "POST",
  });

  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}

// Leave
export const useLeavePartyMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: leaveParty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    },
  });
};

async function leaveParty() {
  const { url, method, res } = ApiURL.create("/api/duel/party/leave", {
    method: "POST",
  });

  const response = await fetch(url, { method });

  return res(await response.json());
}

// Join
export const useJoinPartyMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: joinParty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    },
  });
};

async function joinParty(joinPartyBody: { partyCode: string }) {
  const { url, method, req, res } = ApiURL.create("/api/duel/party/join", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
    body: req({ partyCode: joinPartyBody.partyCode }),
  });
  return res(await response.json());
}

// Create
export const useCreatePartyMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createParty,
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["party"] });
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

// End
export const useEndPartyMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: endParty,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["party"] });
    },
  });
};

async function endParty() {
  const { url, method, res } = ApiURL.create("/api/duel/end", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
  });
  return res(await response.json());
}

// Current
export const useGetCurrentDuelOrPartyQuery = () => {
  return useQuery({
    queryKey: ["party"],
    queryFn: getCurrentDuelOrParty,
  });
};

async function getCurrentDuelOrParty() {
  const { url, method, res } = ApiURL.create("/api/duel/current", {
    method: "GET",
  });
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}
