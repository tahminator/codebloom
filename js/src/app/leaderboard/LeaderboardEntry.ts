export type LeaderboardEntry = {
    id: string;
    name: string;
    createdAt: string;
    deletedAt: string;
    users: {
        id: string;
        discordId: string;
        discordName: string;
        leetcodeUsername: string;
        totalScore: number;
    }[];
};