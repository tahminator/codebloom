import { useUsersTotalPoints } from "@/app/dashboard/hooks";
import { Button, Text } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useEffect, useState } from "react";

export default function RefreshSubmissions() {
  const { mutate, isPending } = useUsersTotalPoints();
  const [isDisabled, setIsDisabled] = useState(false);
  const [countdown, setCountdown] = useState(0);

  const form = useForm({
    initialValues: {
      leetcodeUsername: "",
    },
  });

  useEffect(() => {
    if (countdown > 0) {
      setTimeout(() => {
        setCountdown((prev) => prev - 1);
      }, 1000);
      return;
    }

    setIsDisabled(false);
  }, [countdown, isDisabled]);

  const onSubmit = () => {
    mutate(undefined, {
      onSuccess: ({ success, message: responseMessage }) => {
        // Hack to avoid breaking the standard ApiResponse flow.
        if (typeof responseMessage === "number") {
          setIsDisabled(true);
          setCountdown(responseMessage);
          return;
        }

        notifications.show({
          message: responseMessage,
          color: success ? undefined : "red",
        });
      },
    });
  };

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <Button
        fullWidth
        radius="md"
        mt="md"
        mb="md"
        size="md"
        onClick={onSubmit}
        loading={isPending}
        disabled={isDisabled}
        ta={"center"}
      >
        {isDisabled
          ? `Please wait ${countdown} seconds to refresh again.`
          : "Click here to refresh your latest submissions!"}
      </Button>
      <Text
        c={"dimmed"}
        style={{
          maxWidth: "640px",
        }}
        ta={"center"}
      >
        Please note that we automatically query Leetcode's APIs behind the
        scenes to make sure you can stay on that LeetCode grind without having
        to press this button everytime!
      </Text>
    </form>
  );
}
