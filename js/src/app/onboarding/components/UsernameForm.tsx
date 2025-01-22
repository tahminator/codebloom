import { useSetLeetcodeUsername } from "@/app/onboarding/hooks";
import { Button, Card, Center, Group, Text, TextInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";

export default function UsernameForm() {
  const { mutate, isPending } = useSetLeetcodeUsername();

  const form = useForm({
    initialValues: {
      leetcodeUsername: "",
    },
  });

  const onSubmit = (data: { leetcodeUsername: string }) => {
    mutate(
      { leetcodeUsername: data.leetcodeUsername },
      {
        onSuccess: ({ success, message }) => {
          return notifications.show({
            message,
            color: success ? undefined : "red",
          });
        },
      }
    );
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
      }}
    >
      <form onSubmit={form.onSubmit(onSubmit)}>
        <Card
          radius="md"
          style={{
            width: "350px",
          }}
        >
          <Center>
            <Group mt="md" mb="xs">
              <Text
                gradient={{ from: "rgb(75,233,167)", to: "white" }}
                variant="gradient"
                style={{ display: "flex" }}
                fw={500}
                size="lg"
              >
                LeetCode Username
              </Text>
            </Group>
          </Center>

          <TextInput
            label="Username"
            placeholder="0pengu"
            radius="md"
            mt="md"
            required
            {...form.getInputProps("leetcodeUsername")}
            disabled={isPending}
          />
          <Button mt="md" mb="md" radius="md" type="submit" loading={isPending}>
            Submit
          </Button>

          <Text size="xs" c="dimmed">
            You can't change your LeetCode Username, so choose wisely. Any
            impersonation of an account that's not yours will be deleted.
          </Text>
        </Card>
      </form>
    </div>
  );
}
