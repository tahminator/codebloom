import { successfulLeaderboardHandlers } from "@/__mocks__/leaderboard";
import { setupWorker } from "msw/browser";

export const handlers = [...successfulLeaderboardHandlers];
export const launchMockServer = () => {
  const worker = setupWorker(...handlers);
  return worker.start();
};
