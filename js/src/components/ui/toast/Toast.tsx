import { DefaultMantineColor } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { ReactNode, useEffect } from "react";

/**
 * A custom React function that shows a notification in a React way.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function Toast({
  message,
  color,
}: {
  message: ReactNode;
  color?: DefaultMantineColor;
}) {
  useEffect(() => {
    notifications.show({
      message,
      color,
    });
  }, [color, message]);

  return <></>;
}
