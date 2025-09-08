import { z } from "zod";

export const newAnnouncementSchema = z.object({
  message: z.string().trim().min(1).max(230),
  showTimer: z.boolean(),
  expiresAt: z.string(),
});

export const disableAnnouncementSchema = z.object({
  id: z.string().min(1, "Announcement ID is required"),
});
