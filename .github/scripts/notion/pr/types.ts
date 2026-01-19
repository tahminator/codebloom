import { type PageObjectResponse } from "@notionhq/client";

export type NotionTaskObject = {
  task: PageObjectResponse;
  taskContent: string;
};
