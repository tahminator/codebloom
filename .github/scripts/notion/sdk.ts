import { Client } from "@notionhq/client";

export function getNotionClient(notionPat: string): Client {
  return new Client({
    auth: notionPat,
  });
}
