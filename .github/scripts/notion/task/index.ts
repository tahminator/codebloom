import { Client } from "@notionhq/client";

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
