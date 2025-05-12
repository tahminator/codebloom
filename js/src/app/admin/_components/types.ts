import { z } from "zod";

export const AdminSchema = () =>
  z.object({
    name: z.string().trim().min(1).max(16),
    confirmation: z.string().trim(),
  });
