import { ApiURL } from "@/lib/api/common/apiURL";
import d from "dayjs";
import { http, HttpResponse } from "msw";

export const MOCK_LEADERBOARD_NAME = 'std::string name = "leaderboard name"';
export const MOCK_LEADERBOARD_LANGUAGE = "cpp";
export const MOCK_LEADERBOARD_EXPIRE = d().add(10, "days").toISOString();

const editLeaderboard = ApiURL.create("/api/admin/leaderboard/current", {
  method: "PUT",
});

export const editLeaderboardSuccessHandler = http.put(
  editLeaderboard.url.toString(),
  () => {
    return HttpResponse.json({
      success: true,
      payload: {},
      message: "Leaderboard updated successfully",
    } satisfies ReturnType<typeof editLeaderboard.res>);
  },
);
