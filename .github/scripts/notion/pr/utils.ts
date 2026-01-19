import type {
  BlockObjectResponse,
  RichTextItemResponse,
} from "@notionhq/client/build/src/api-endpoints";

import { isFullBlock, type Client } from "@notionhq/client";

export async function _fetchBlocks(
  client: Client,
  blockId: string,
  indent = "",
): Promise<string[]> {
  const response = await client.blocks.children.list({
    block_id: blockId,
  });

  const lines: string[] = [];

  for (const block of response.results) {
    if (!isFullBlock(block)) continue;

    const markdown = blockToMarkdown(block, indent);

    if (markdown) {
      lines.push(markdown);
    }

    if (block.has_children) {
      const childLines = await _fetchBlocks(client, block.id, indent + "  ");
      lines.push(...childLines);
    }
  }

  return lines;
}

function extractPlainText(richText: RichTextItemResponse[]): string {
  return richText.map((item) => item.plain_text).join("");
}

function blockToMarkdown(block: BlockObjectResponse, indent: string): string {
  switch (block.type) {
    case "paragraph":
      return indent + extractPlainText(block.paragraph.rich_text);

    case "bulleted_list_item":
      return (
        indent + "- " + extractPlainText(block.bulleted_list_item.rich_text)
      );

    case "numbered_list_item":
      return (
        indent + "1. " + extractPlainText(block.numbered_list_item.rich_text)
      );

    case "heading_1":
      return indent + "# " + extractPlainText(block.heading_1.rich_text);

    case "heading_2":
      return indent + "## " + extractPlainText(block.heading_2.rich_text);

    case "heading_3":
      return indent + "### " + extractPlainText(block.heading_3.rich_text);

    case "quote":
      return indent + "> " + extractPlainText(block.quote.rich_text);

    case "to_do": {
      const checked = block.to_do.checked ? "[x]" : "[ ]";
      return indent + checked + " " + extractPlainText(block.to_do.rich_text);
    }

    case "toggle":
      return indent + extractPlainText(block.toggle.rich_text);

    case "callout":
      return indent + extractPlainText(block.callout.rich_text);

    case "code": {
      const language = block.code.language || "";
      const code = extractPlainText(block.code.rich_text);
      return `${indent}\`\`\`${language}\n${code}\n${indent}\`\`\``;
    }

    default:
      return "";
  }
}
