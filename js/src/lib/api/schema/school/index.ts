import { z } from "zod";

export const schoolVerificationForm = z.object({
  email: z.string().trim().email().min(1).max(230),
});
