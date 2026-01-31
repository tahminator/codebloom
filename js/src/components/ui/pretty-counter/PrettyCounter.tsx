import { Text, TextProps } from "@mantine/core";

const formatCountdown = (seconds: number): string => {
  const days = Math.floor(seconds / (24 * 60 * 60));
  const hours = Math.floor((seconds % (24 * 60 * 60)) / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;

  return `${days}d ${hours}h ${minutes}m ${secs}s`;
};

type CounterProps = TextProps & { time: number };

export default function PrettyCounter({ time, ...props }: CounterProps) {
  return (
    <Text {...props} data-testid="PrettyCounter">
      {formatCountdown(time)}
    </Text>
  );
}
