import { ApiURL } from "@/lib/api/common/apiURL";
import d from "dayjs";
import { http, HttpResponse } from "msw";
import { v4 as uuid } from "uuid";

const currentMetadata = ApiURL.create("/api/leaderboard/current/metadata", {
  method: "GET",
});

export const MOCK_LEADERBOARD_ID = uuid();
const metadataById = ApiURL.create(
  "/api/leaderboard/{leaderboardId}/metadata",
  {
    method: "GET",
    params: {
      leaderboardId: MOCK_LEADERBOARD_ID,
    },
  },
);

export const currentMetadataNoSyntaxHighlightingHandler = http.get(
  currentMetadata.url.toString(),
  () => {
    return HttpResponse.json({
      payload: {
        id: uuid(),
        createdAt: d().toISOString(),
        shouldExpireBy: d().add(10, "days").toISOString(),
        syntaxHighlightingLanguage: null,
        name: "haiii",
        deletedAt: null,
      },
      success: true,
      message: "Leaderboard metadata loaded!",
    } satisfies ReturnType<typeof currentMetadata.res>);
  },
);
export const metadataByIdNoSyntaxHighlightingHandler = http.get(
  metadataById.url.toString(),
  () => {
    return HttpResponse.json({
      payload: {
        id: uuid(),
        createdAt: d().toISOString(),
        shouldExpireBy: d().add(10, "days").toISOString(),
        syntaxHighlightingLanguage: null,
        name: "haiii",
        deletedAt: null,
      },
      success: true,
      message: "Leaderboard metadata loaded!",
    } satisfies ReturnType<typeof metadataById.res>);
  },
);

export const successfulLeaderboardHandlers = [
  http.get(currentMetadata.url.toString(), () => {
    return HttpResponse.json({
      payload: {
        id: uuid(),
        createdAt: d().toISOString(),
        shouldExpireBy: d().add(10, "days").toISOString(),
        syntaxHighlightingLanguage: "cpp",
        name: 'std::string november = "hello world"',
        deletedAt: null,
      },
      success: true,
      message: "Leaderboard metadata loaded!",
    } satisfies ReturnType<typeof currentMetadata.res>);
  }),
  http.get(metadataById.url.toString(), () => {
    return HttpResponse.json({
      payload: {
        id: uuid(),
        createdAt: d().toISOString(),
        shouldExpireBy: d().add(10, "days").toISOString(),
        syntaxHighlightingLanguage: "cpp",
        name: 'std::string november = "hello world"',
        deletedAt: null,
      },
      success: true,
      message: "Leaderboard metadata loaded!",
    } satisfies ReturnType<typeof metadataById.res>);
  }),
];

export const failedLeaderboardHandlers = [
  http.get(currentMetadata.url.toString(), () => {
    return HttpResponse.json({
      success: false,
      message: "Leaderboard metadata failed to load",
    } satisfies ReturnType<typeof currentMetadata.res>);
  }),
  http.get(metadataById.url.toString(), () => {
    return HttpResponse.json({
      success: false,
      message: "Leaderboard metadata failed to load",
    } satisfies ReturnType<typeof metadataById.res>);
  }),
];

export const catastrophicLeaderboardHandlers = [
  http.get(currentMetadata.url.toString(), () => {
    return HttpResponse.error();
  }),
  http.get(metadataById.url.toString(), () => {
    return HttpResponse.error();
  }),
];
