import { adminSchema } from "@/app/admin/_components/types";
import { useCreateLeaderboardMutation } from "@/lib/api/queries/admin";
import { Modal, Button, TextInput } from "@mantine/core";
import { useForm, zodResolver } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useState } from "react";
import { z } from "zod";

interface NewLeaderboardFormBodyProps {
  currentLeaderboardName: string;
}

function NewLeaderboardFormBody({
  currentLeaderboardName,
}: NewLeaderboardFormBodyProps) {
  const [isModalOpen, setModalOpen] = useState(false);
  const { mutate } = useCreateLeaderboardMutation();
  const form = useForm({
    validate: zodResolver(adminSchema(currentLeaderboardName)),
    initialValues: {
      name: "",
      confirmation: "",
    },
  });
  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  const onSubmit = (values: z.infer<ReturnType<typeof adminSchema>>) => {
    mutate(
      { name: values.name },
      {
        onSuccess: async (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });
          if (data.success) {
            form.reset();
            setModalOpen(false);
          }
        },
      },
    );
  };

  return (
    <>
      <Button variant="outline" onClick={toggleModal}>
        Create
      </Button>
      <Modal
        opened={isModalOpen}
        onClose={toggleModal}
        title="Create New Leaderboard"
      >
        <form onSubmit={form.onSubmit(onSubmit)}>
          <TextInput
            {...form.getInputProps("name")}
            label="New leaderboard name"
            error={form.errors.name}
            mb="sm"
          />
          <TextInput
            {...form.getInputProps("confirmation")}
            label={`Type "${currentLeaderboardName}" to confirm`}
            error={form.errors.confirmation}
          />
          <Button
            type="submit"
            size="xs"
            mt="sm"
            variant="outline"
            disabled={!form.isValid()}
          >
            Submit
          </Button>
        </form>
      </Modal>
    </>
  );
}

export default NewLeaderboardFormBody;
