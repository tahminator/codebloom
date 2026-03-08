import type { PageObjectResponse } from "@notionhq/client";

import { sendMessage } from "utils/send-message";

export async function _validateNotionTask(
  task: PageObjectResponse,
  taskContent: string,
  prId: number,
): Promise<void> {
  const errors: string[] = [];

  if (!/\bAC\b/.test(taskContent)) {
    errors.push(
      "Acceptance Criteria (AC) is not provided in the ticket description.",
    );
  }

  const assigneeProperty = task.properties["Assignee"];
  if (
    !assigneeProperty ||
    assigneeProperty.type !== "people" ||
    assigneeProperty.people.length === 0
  ) {
    errors.push("Ticket does not have an assignee.");
  }

  const featureProperty = task.properties["Feature"];
  if (
    !featureProperty ||
    featureProperty.type !== "relation" ||
    featureProperty.relation.length === 0
  ) {
    errors.push("Ticket does not have a feature assigned.");
  }

  const priorityProperty = task.properties["Priority"];
  if (
    !priorityProperty ||
    priorityProperty.type !== "select" ||
    !priorityProperty.select
  ) {
    errors.push("Ticket does not have a priority defined.");
  }

  if (errors.length > 0) {
    const message = `## Ticket Validation Failed\n\nThe following issues were found with the attached ticket:\n\n${errors.map((e) => `- ${e}`).join("\n")}`;
    await sendMessage(prId, message);
    const errorList = errors.map((e) => `  - ${e}`).join("\n");
    console.error(
      `Ticket validation failed with ${errors.length} error(s):\n${errorList}`,
    );
    process.exit(1);
  }
}
