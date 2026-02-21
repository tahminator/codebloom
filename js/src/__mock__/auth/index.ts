import { ApiURL } from "@/lib/api/common/apiURL";
import { http, HttpResponse } from "msw";

const auth = ApiURL.create("/api/auth/validate", {
  method: "GET",
});

export const authSuccessHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.json({
    success: true,
    message: "Authenticated!",
    payload: {
      session: {
        id: "session-1",
        userId: "user-1",
        expiresAt: new Date().toISOString(),
      },
      user: {
        id: "user-1",
        schoolEmail: "test@example.com",
        admin: false,
        leetcodeUsername: "testuser",
        discordId: "",
        discordName: "",
        nickname: null,
        profileUrl: null,
        tags: [],
        verifyKey: "",
        achievements: [],
      },
    },
  } satisfies ReturnType<typeof auth.res>);
});

export const authErrorHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.error();
});

export const authUnauthenticatedHandler = http.get(auth.url.toString(), () => {
  return HttpResponse.json(
    {
      success: false,
      message: "Not authenticated",
    },
    { status: 401 },
  );
});
