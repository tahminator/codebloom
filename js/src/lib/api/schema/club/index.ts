import z from "zod";

export const clubVerificationFormSchema = z.object({
  password: z.string().trim().min(1).max(230),
});
