import { useSubmitFeedbackMutation } from "@/lib/api/queries/reporter";
import { reportIssueSchema } from "@/lib/api/schema/reporter";
import {
  Button,
  Card,
  Center,
  Flex,
  Group,
  Text,
  TextInput,
  Textarea,
} from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { zodResolver } from "mantine-form-zod-resolver";
import { useNavigate } from "react-router-dom";
import { z } from "zod";

export default function ReportIssue() {
  const navigate = useNavigate();
  const { mutate, isPending } = useSubmitFeedbackMutation();

  const form = useForm({
    initialValues: {
      title: "",
      description: "",
      email: "",
    },
    validate: zodResolver(reportIssueSchema),
  });

  const onSubmit = (data: z.infer<typeof reportIssueSchema>) => {
    mutate(data, {
      onSuccess: ({ success, message }) => {
        notifications.show({
          message,
          color: success ? undefined : "red",
        });
        if (success) {
          form.reset();
          navigate("/dashboard");
        }
      },
    });
  };

  return (
    <Flex justify={"center"} align={"center"} h={"90vh"}>
      <form onSubmit={form.onSubmit(onSubmit)}>
        <Card
          style={{
            width: "380px",
          }}
        >
          <Center>
            <Group mt="md" mb="xs">
              <Text
                gradient={{ from: "rgb(75,233,167)", to: "white" }}
                variant="gradient"
                fw={400}
                size="lg"
              >
                Report an Issue
              </Text>
            </Group>
          </Center>
          <TextInput
            label="Title"
            placeholder="Briefly describe the issue"
            radius="md"
            mt="md"
            required
            {...form.getInputProps("title")}
            disabled={isPending}
          />
          <Textarea
            label="Description"
            placeholder="Provide detailed information about the issue"
            radius="md"
            mt="md"
            required
            minRows={4}
            {...form.getInputProps("description")}
            disabled={isPending}
          />
          <TextInput
            label="Email"
            placeholder="your@email.com"
            radius="md"
            mt="md"
            required
            type="email"
            {...form.getInputProps("email")}
            disabled={isPending}
          />
          <Button mt="md" mb="md" radius="md" type="submit" loading={isPending}>
            Submit Report
          </Button>
          <Button variant={"subtle"} onClick={() => navigate(-1)}>
            Go back
          </Button>
        </Card>
      </form>
    </Flex>
  );
}
