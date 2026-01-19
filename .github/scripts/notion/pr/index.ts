import type { NotionTaskObject } from "notion/pr/types";

import { Client, isFullPage } from "@notionhq/client";
import { $ } from "bun";
import { _fetchBlocks } from "notion/pr/utils";
import { sendMessage } from "utils/send-message";

import { _getNotionTaskById } from "../task";

export async function checkNotionPrAndGetTask(
  notionPat: string,
  prId: number,
  notionDbId: string,
): Promise<NotionTaskObject> {
  const client = new Client({
    auth: notionPat,
  });

  const title = await (async () => {
    const res = await $`gh pr view ${prId} --json title`.text();
    const json = JSON.parse(res) as { title?: string } | undefined;

    if (!json || !json.title) {
      await sendMessage(prId, "Failed to parse Notion ID");
      process.exit(1);
    }

    return json.title;
  })();

  const ticketNum = parseInt(title, 10);

  if (!Number.isNaN(ticketNum) && (ticketNum <= 0 || ticketNum > 999)) {
    await sendMessage(prId, `No numeric prefix found in PR title: ${title}`);
    process.exit(1);
  }

  const task = await _getNotionTaskById(client, notionDbId, ticketNum);

  if (!isFullPage(task)) {
    sendMessage(prId, "Notion task is not expected page type");
    process.exit(1);
  }

  console.log(task);

  const blocks = await _fetchBlocks(client, task.id);

  return {
    task,
    taskContent: blocks.join("\n"),
  };
}
