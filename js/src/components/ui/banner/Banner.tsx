import PrettyCounter from "@/components/ui/pretty-counter/PrettyCounter";
import useCountdown from "@/lib/hooks/useCountdown";
import { Flex, Text, Transition } from "@mantine/core";
import { useMounted } from "@mantine/hooks";

export default function Banner({
  message,
  counter,
}: {
  message: string;
  counter?: Date;
}) {
  const mounted = useMounted();

  const time = (() => {
    if (!counter) {
      return 0;
    }

    return Math.floor((counter.getTime() - Date.now()) / 1000);
  })();
  const [countdown] = useCountdown(time);

  return (
    <Transition keepMounted mounted={mounted} transition={"scale-y"}>
      {(style) => (
        <Flex
          bg={"green"}
          ta={"center"}
          direction={"column"}
          align={"center"}
          style={style}
          justify={"center"}
          gap={"sm"}
          p={"10px"}
        >
          <Text size={"lg"}>{message}</Text>
          {counter && <PrettyCounter size={"lg"} time={countdown} />}
        </Flex>
      )}
    </Transition>
  );
}
