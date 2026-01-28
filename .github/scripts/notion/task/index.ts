import { Client, type PageObjectResponse } from "@notionhq/client";

export async function _getNotionTaskById(
  client: Client,
  dbId: string,
  id: number,
) {
  try {
    const { results } = await client.dataSources.query({
      filter: {
        property: "ID",
        unique_id: {
          equals: id,
        },
      },
      data_source_id: dbId,
    });

    const ticket = results[0];

    if (!ticket) {
      throw new Error("Ticket with ID does not exist.");
    }

    return ticket;
  } catch (e) {
    console.error("Ticket cannot be retrieved\n", e);
    process.exit(-100);
  }
}

export async function _updateNotionTaskWithPrLink(
  client: Client,
  page: PageObjectResponse,
  taskId: number,
  prId: number,
) {
  const PR_KEY = "PRs (AUTO)";

  try {
    const prField = page.properties[PR_KEY];

    if (prField?.type !== "rich_text") {
      throw new Error("PR Field is not of rich_text type");
    }

    const prLink = `https://github.com/tahminator/codebloom/pull/${prId}`;

    if (prField.rich_text.find((r) => r.plain_text === prLink)) {
      console.log("PR ID is already inside of Notion task, skipping update...");
      return;
    }

    prField.rich_text.push({
      type: "text",
      text: {
        content: prLink,
        link: null,
      },
      plain_text: prLink,
      href: null,
      annotations: {
        bold: false,
        italic: false,
        strikethrough: false,
        underline: false,
        code: false,
        color: "default",
      },
    });

    await client.pages.update({
      page_id: page.id,
      properties: {
        [PR_KEY]: {
          type: "rich_text",
          rich_text: [
            ...prField.rich_text.map((r) => ({
              type: "text" as const,
              text: {
                content: r.plain_text,
              },
            })),
          ],
        },
      },
    });
  } catch (e) {
    console.error("failed to update notion task with pr link", e);
    process.exit(1);
  }
}
