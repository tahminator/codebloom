export type Question = {
  id: string;
  userId: string;
  questionSlug: string;
  questionTitle: string;
  questionDifficulty: "Easy" | "Medium" | "Hard";
  questionNumber: number;
  questionLink: string;
  description: string;
  // TODO - Will have to cross-reference this to the backend (as of right now, pointsAwarded can't actually be null, but that may change and break the frontend code.)
  pointsAwarded: number;
  acceptanceRate: number;
  runtime: string;
  memory: string;
  code: string;
  language: string;
  submittedAt: string;
  createdAt: string;
};
