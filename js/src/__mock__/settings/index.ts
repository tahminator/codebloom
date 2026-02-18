import { ApiURL } from "@/lib/api/common/apiURL";
import { http, HttpResponse } from "msw";

const auth = ApiURL.create("/api/auth/validate", {
  method: "GET",
});

export const authHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.json({
    success: true,
    message: "Authenticated!",
    payload: {
      session: {
        id: "session-1",
        userId: "",
        expiresAt: "",
      },
      user: {
        id: "user-1",
        admin: false,
        schoolEmail: null,
        discordId: "",
        discordName: "",
        leetcodeUsername: null,
        nickname: null,
        profileUrl: null,
        tags: [],
        verifyKey: "",
        achievements: [],
      },
    },
  } satisfies ReturnType<typeof auth.res>);
});

export const authWithSchoolHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.json({
    success: true,
    message: "Authenticated!",
    payload: {
      session: {
        id: "session-1",
        userId: "",
        expiresAt: "",
      },
      user: {
        id: "user-1",
        admin: false,
        schoolEmail: "user@college.edu",
        discordId: "",
        discordName: "",
        leetcodeUsername: null,
        nickname: null,
        profileUrl: null,
        tags: [],
        verifyKey: "",
        achievements: [],
      },
    },
  } satisfies ReturnType<typeof auth.res>);
});

export const authUnauthenticatedHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.json({
    success: false,
    message: "Not authenticated.",
  } satisfies ReturnType<typeof auth.res>);
});

export const authErrorHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.error();
});
