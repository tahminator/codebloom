import { successfulLeaderboardHandlers } from "@/__mock__/leaderboard";
import { setupWorker } from "msw/browser";

export const handlers = [...successfulLeaderboardHandlers];
export const launchMockServer = async () => {
  const worker = setupWorker(...handlers);
  await worker.start();
};
