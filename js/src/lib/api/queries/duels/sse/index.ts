import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { ApiURL } from "@/lib/api/common/apiURL";
import { fetchEventSource } from "@/lib/api/common/fetchEventSource";
import { useGetCurrentDuelOrPartyQuery } from "@/lib/api/queries/duels";
import { Api } from "@/lib/api/types";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect } from "react";

export const useMyDuelOrPartyData = () => {
  const { data } = useGetCurrentDuelOrPartyQuery();
  const d = useDuelOrPartyData(data?.payload?.code || "");

  return d;
};

export const useDuelOrPartyData = (lobbyCode: string) => {
  const queryClient = useQueryClient();
  const { url, method, res, queryKey } = ApiURL.create(
    "/api/duel/{lobbyCode}/sse",
    {
      method: "POST",
      params: {
        lobbyCode,
      },
    },
  );

  const query = useQuery<UnknownApiResponse<Api<"DuelData">>>({
    queryKey,
  });

  useEffect(() => {
    if (!lobbyCode) {
      return;
    }

    const controller = new AbortController();

    fetchEventSource(url, {
      method,
      headers: {
        "Content-Type": "application/json",
      },
      signal: controller.signal,

      onmessage(ev) {
        try {
          const data = res(JSON.parse(ev.data));
          queryClient.setQueryData(queryKey, data);
        } catch (e) {
          console.error("Failed to parse SSE message", e);
        }
      },

      onerror(err) {
        console.error("SSE error", err);
      },
    });

    return () => {
      controller.abort();
    };
  }, [lobbyCode, queryClient]);

  return query;
};
