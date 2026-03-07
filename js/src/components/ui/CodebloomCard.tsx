import { Card, type CardProps } from "@mantine/core";

export type CodebloomCardProps = CardProps & {
  [key: string]: unknown;
};

export default function CodebloomCard({
  children,
  withBorder = true,
  padding = "md",
  radius = "md",
  shadow = "sm",
  ...rest
}: CodebloomCardProps) {
  return (
    <Card
      withBorder={withBorder}
      padding={padding}
      radius={radius}
      shadow={shadow}
      {...rest}
    >
      {children}
    </Card>
  );
}
