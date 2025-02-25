export type LeaderboardEntry = {
  id: string;
  name: string;
  createdAt: string;
  deletedAt: string | null;
  users: {
    id: string;
    discordId: string;
    discordName: string;
    leetcodeUsername: string;
    totalScore: number;
    nickname: string | null;
  }[];
};
