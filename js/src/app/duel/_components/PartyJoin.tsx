import { useJoinPartyMutation } from "@/lib/api/queries/duels";
import { partyCodeSchema } from "@/lib/api/schema/duel";
import { Button, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useNavigate } from "react-router-dom";
import z from "zod";

export default function PartyJoin() {
  const { mutate } = useJoinPartyMutation();
  const navigate = useNavigate();

  const form = useForm({
    validate: zodResolver(partyCodeSchema),
    initialValues: {
      joinCode: "",
    },
  });

  const onSubmit = (values: z.infer<typeof partyCodeSchema>) => {
    const code = values.joinCode;
    if (!code) {
      notifications.show({
        message: "Please enter a party code",
        color: "red",
      });
      return;
    }
    mutate(
      { partyCode: code },
      {
        onSuccess: (data) => {
          if (data.success) {
            navigate(`/duel/${code}`);
          } else {
            form.setFieldError("joinCode", data.message);
          }
        },
      },
    );
  };

  return (
    <form onSubmit={form.onSubmit(onSubmit)}>
      <TextInput
        {...form.getInputProps("joinCode")}
        w={"100%"}
        placeholder="Enter Party Code"
        radius="md"
        pb="xs"
        error={form.errors.joinCode}
        styles={{
          input: {
            textAlign: "center",
          },
        }}
      />
      <Button fullWidth radius="md" type="submit">
        Join
      </Button>
    </form>
  );
}
