import { z } from "zod";
export const adminSchema = (leaderboardName: string) =>
  z.object({
    name: z.string().trim().min(1).max(16),
    confirmation: z
      .string()
      .trim()
      .refine((s) => s == leaderboardName),
    shouldExpireBy: z.string().nullable(),
    syntaxHighlightingLanguage: z.string().nullable(),
  });