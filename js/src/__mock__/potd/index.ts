import { ApiURL } from "@/lib/api/common/apiURL";
import { http, HttpResponse } from "msw";

const potd = ApiURL.create("/api/leetcode/potd", {
  method: "GET",
});

export const potdHandler = http.get(potd.url.toString(), () => {
  return HttpResponse.json({
    success: true,
    payload: {
      title: "Two Sum",
      slug: "two-sum",
      multiplier: 2,
      id: "",
      createdAt: "",
    },
    message: "",
  } satisfies ReturnType<typeof potd.res>);
});

export const potdFailedHandler = http.get(potd.url.toString(), () => {
  return HttpResponse.json({
    success: false,
    message: "No problem of the day available.",
  } satisfies ReturnType<typeof potd.res>);
});

export const potdErrorHandler = http.get(potd.url.toString(), () => {
  return HttpResponse.error();
});
