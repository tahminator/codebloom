import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { ApiURL } from "@/lib/api/common/apiURL";
import { fetchEventSource } from "@/lib/api/common/fetchEventSource";
import { Api } from "@/lib/api/types";
import { useEffect, useState } from "react";

type DuelStreamData = {
  data: UnknownApiResponse<Api<"DuelData">> | null;
  isConnected: boolean;
  error: Error | null;
};

export const useDuelData = (lobbyCode: string) => {
  const [state, setState] = useState<DuelStreamData>({
    data: null,
    isConnected: false,
    error: null,
  });

  useEffect(() => {
    if (!lobbyCode) {
      return;
    }

    const controller = new AbortController();

    const connect = async () => {
      const { url, method, res } = ApiURL.create("/api/duel/{lobbyCode}/sse", {
        method: "POST",
        params: {
          lobbyCode: lobbyCode,
        },
      });

      const controller = new AbortController();

      await fetchEventSource(url, {
        method,
        headers: {
          "Content-Type": "application/json",
        },
        signal: controller.signal,

        async onopen(response) {
          if (response.ok) {
            setState((prev) => ({ ...prev, isConnected: true, error: null }));
            return;
          }
        },

        onmessage(ev) {
          try {
            const data = res(ev.data);
            setState((prev) => ({ ...prev, data: data, isConnected: true }));
          } catch (e) {
            console.error("Failed to parse SSE message", e);
          }
        },

        onerror(err) {
          if (err.message) {
            setState((prev) => ({ ...prev, error: err, isConnected: false }));
            throw err;
          }
        },

        onclose() {
          setState((prev) => ({ ...prev, isConnected: false }));
        },
      });
    };
    connect();

    return () => {
      controller.abort();
      setState((prev) => ({ ...prev, isConnected: false }));
    };
  }, [lobbyCode]);

  return state;
};
