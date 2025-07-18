import { useVerifySchoolMutation } from "@/lib/api/queries/auth/school";
import { schoolVerificationForm } from "@/lib/api/schema/school";
import { Box, Button, Modal, Text, TextInput } from "@mantine/core";
import { useForm, zodResolver } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { z } from "zod";

type SchoolModalProps = {
  enabled: boolean;
  toggle: () => void;
};

export default function SchoolEmailModal({
  enabled,
  toggle,
}: SchoolModalProps) {
  const { mutate, status } = useVerifySchoolMutation();
  const form = useForm({
    validate: zodResolver(schoolVerificationForm),
    initialValues: {
      email: "",
    },
  });

  const onSubmit = (values: z.infer<typeof schoolVerificationForm>) => {
    const id = notifications.show({
      message: "Verifying email... ",
      color: "blue",
    });
    mutate(
      { email: values.email },
      {
        onSuccess: async (data) => {
          notifications.update({
            id,
            message: data.message,
            color: data.success ? undefined : "red",
          });
          if (data.success) {
            form.reset();
          }
        },
      },
    );
  };

  return (
    <Modal opened={enabled} onClose={toggle} size={"lg"}>
      <Box p={"lg"}>
        <Text p="md">
          Verify your student email to gain access to school-specific
          competitions!
        </Text>

        <form onSubmit={form.onSubmit(onSubmit)}>
          <TextInput
            {...form.getInputProps("email")}
            placeholder="Enter your school email"
            error={form.errors.email}
            p="md"
          />

          <Button
            type="submit"
            size="xs"
            ml="md"
            disabled={!form.isValid("email") || status === "pending"}
            loading={status === "pending"}
          >
            Submit
          </Button>
        </form>
      </Box>
    </Modal>
  );
}
