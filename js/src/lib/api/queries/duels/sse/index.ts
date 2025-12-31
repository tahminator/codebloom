import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { ApiURL } from "@/lib/api/common/apiURL";
import { fetchEventSource } from "@/lib/api/common/fetchEventSource";
import { Api } from "@/lib/api/types";
import { useEffect, useMemo, useState } from "react";

type DuelStreamData = {
  data: UnknownApiResponse<Api<"DuelData">> | null;
  isConnected: boolean;
  error: Error | null;
};

type FinalDuelStreamData = DuelStreamData & {
  status: "success" | "pending" | "error";
};

export const useDuelData = (lobbyCode: string): FinalDuelStreamData => {
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
            const data = res(JSON.parse(ev.data));
            setState((prev) => ({ ...prev, data: data, isConnected: true }));
          } catch (e) {
            console.error("Failed to parse SSE message", e);
            throw e;
          }
        },

        onerror(err) {
          if (err instanceof Error) {
            if (err.message) {
              setState((prev) => ({ ...prev, error: err, isConnected: false }));
            }
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
      setState((prev) => ({ ...prev, isConnected: false, error: null }));
    };
  }, [lobbyCode]);

  const status = useMemo(() => {
    if (state.data && state.isConnected) {
      return "success";
    }

    if (!state.isConnected && state.error) {
      return "error";
    }

    return "pending";
  }, [state.data, state.error, state.isConnected]);

  return { ...state, status };
};
