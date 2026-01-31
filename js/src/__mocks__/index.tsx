import { successfulLeaderboardHandlers } from "@/__mocks__/leaderboard";
import { setupWorker } from "msw/browser";

export const handlers = [...successfulLeaderboardHandlers];
export const launchMockServer = () => setupWorker(...handlers);
