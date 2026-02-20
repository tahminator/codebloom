import { z } from "zod";

export const reportIssueSchema = z.object({
  title: z.string().trim().min(1, "Title is required"),
  description: z
    .string()
    .trim()
    .min(10, "Description must be at least 10 characters")
    .max(10000, "Description must not exceed 10000 characters"),
  email: z.string().trim().email("Invalid email").max(254, "Invalid email"),
});
