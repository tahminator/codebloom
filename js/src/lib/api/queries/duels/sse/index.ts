import { ApiURL } from "@/lib/api/common/apiURL";
import { fetchEventSource } from "@/lib/api/common/fetchEventSource";

export const getDuelData = async (lobbyCode: string) => {
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
        return;
      }
    },

    onmessage(ev) {
      try {
        res(JSON.parse(ev.data));
      } catch (e) {
        console.error("Failed to parse SSE message", e);
      }
    },

    onerror(err) {
      if (err.message) {
        throw err;
      }
    },

    onclose() {},
  });
};
