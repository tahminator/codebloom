import { z } from "zod";
export const partyCodeSchema = z.object({
  joinCode: z
    .string()
    .trim()
    .length(6, "Party code must be exactly 6 characters."),
});
