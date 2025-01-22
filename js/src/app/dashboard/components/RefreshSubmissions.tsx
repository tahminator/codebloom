import { useUsersTotalPoints } from "@/app/dashboard/hooks";
import { Button } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useEffect, useState } from "react";

export default function RefreshSubmissions() {
  const { mutate, isPending } = useUsersTotalPoints();
  const [isDisabled, setIsDisabled] = useState(false);
  const [message, setMessage] = useState(0);

  const form = useForm({
    initialValues: {
      leetcodeUsername: "",
    },
  });

  useEffect(() => {
    let timer: number | undefined;
    if (message > 0) {
      timer = setInterval(() => {
        setMessage((prev: number) => prev - 1);
      }, 1000);
    } else if (message === 0 && isDisabled) {
      setIsDisabled(false);
    }
    return () => clearInterval(timer);
  }, [message, isDisabled]);

  const onSubmit = () => {
    if (isDisabled) {
      return;
    }
    mutate(undefined, {
      onSuccess: ({ success, message: responseMessage }) => {
        notifications.show({
          message: responseMessage,
          color: success ? undefined : "red",
        });
        if (typeof responseMessage === "number") {
          setIsDisabled(true);
          setMessage(responseMessage);
        }
      },
    });
  };

  return (
    <>
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          justifyContent: "center",
          alignItems: "center",
        }}
      >
        <form onSubmit={form.onSubmit(onSubmit)}>
          <Button
            radius="md"
            mt="md"
            mb="md"
            size="md"
            onClick={onSubmit}
            loading={isPending}
            disabled={isDisabled}
          >
            {isDisabled
              ? `Please wait ${message} seconds to refresh again.`
              : "Click here to refresh your latest submissions!"}
          </Button>
        </form>
      </div>
    </>
  );
}
