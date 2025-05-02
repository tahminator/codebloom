import { z } from "zod";

export const AdminSchema = (expectedName: string) =>
  z
    .object({
      name: z.string().trim().min(1).max(16),
      confirmation: z.string().trim().min(1).max(16),
    })
    .refine((data) => data.confirmation === expectedName, {
      message: "Confirmation name does not match",
      path: ["confirmation"],
    });
