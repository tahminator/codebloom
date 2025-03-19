export type User = {
  id: string;
  discordId: string;
  discordName: string;
  leetcodeUsername: string | null;
  nickname: string | null;
};

export type UserWithScore = User & { totalScore: number };
