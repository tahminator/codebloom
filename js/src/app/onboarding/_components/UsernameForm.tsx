import {
  useAuthKeyQuery,
  useSetLeetcodeUsername,
} from "@/app/onboarding/hooks";
import {
  Button,
  Card,
  Center,
  Flex,
  Group,
  Loader,
  Text,
  TextInput,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { Link, useNavigate } from "react-router-dom";

export default function UsernameForm() {
  const navigate = useNavigate();

  const { data, status } = useAuthKeyQuery();
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
          notifications.show({
            message,
            color: success ? undefined : "red",
          });
          if (success) {
            navigate("/dashboard");
          }
          return;
        },
      },
    );
  };

  return (
    <Flex justify={"center"} align={"center"} h={"100vh"} gap={"lg"}>
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

          <Text size={"sm"} ta={"center"}>
            You must set the following key inside of your "Summary" section on
            Leetcode:
          </Text>
          <Text ta={"center"} pt={"xs"}>
            {status === "pending" && <Loader />}
            {status === "error" &&
              "Sorry, something went wrong. Please try again later."}
            {status === "success" && data.data}
          </Text>

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
            You can't change your LeetCode Username, so choose wisely. You may
            remove this key after you have been verified.{" "}
          </Text>
          <Button variant={"subtle"} component={Link} to={"/dashboard"}>
            Go back
          </Button>
        </Card>
      </form>
    </Flex>
  );
}
