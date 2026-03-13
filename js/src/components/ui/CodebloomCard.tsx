import {
  Card,
  type CardProps,
  createPolymorphicComponent,
} from "@mantine/core";
import { forwardRef } from "react";

export type CodebloomCardProps = CardProps;

const _CodebloomCard = forwardRef<HTMLDivElement, CodebloomCardProps>(
  (
    {
      children,
      withBorder = true,
      padding = "md",
      radius = "md",
      shadow = "sm",
      ...props
    },
    ref,
  ) => (
    <Card
      ref={ref}
      withBorder={withBorder}
      padding={padding}
      radius={radius}
      shadow={shadow}
      {...props}
    >
      {children}
    </Card>
  ),
);

_CodebloomCard.displayName = "CodebloomCard";

const CodebloomCard = createPolymorphicComponent<"div", CodebloomCardProps>(
  _CodebloomCard,
);
export default CodebloomCard;
