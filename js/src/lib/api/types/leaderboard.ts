export type Leaderboard = {
  id: string;
  name: string;
  createdAt: string;
  deletedAt: string | null;
  shouldExpireBy: string | null;
};
