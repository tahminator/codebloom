import { AdminSchema } from "@/app/admin/_components/types";
import Toast from "@/components/ui/toast/Toast";
import { useCreateLeaderboardMutation } from "@/lib/api/queries/admin";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import { Modal, Button, TextInput, Loader } from "@mantine/core";
import { zodResolver, useForm } from "@mantine/form";
import { useState } from "react";
import { z } from "zod";

function NewLeaderboardForm() {
  const [isModalOpen, setModalOpen] = useState(false);
  const { data, status } = useCurrentLeaderboardMetadataQuery();
  const { mutate } = useCreateLeaderboardMutation();

  const form = useForm({
    validate: zodResolver(AdminSchema()),
    initialValues: {
      name: "",
      confirmation: "",
    },
  });

  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  const onSubmit = (values: z.infer<ReturnType<typeof AdminSchema>>) => {
    mutate(values.name);
  };

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message={"Something went wrong."} />;
  }

  if (!data || !("data" in data) || !data.data) {
    return <Toast message={"No leaderboard data found."} />;
  }
  const currentLeaderboardName = data.data.name;

  const isValid =
    form.values.confirmation === currentLeaderboardName &&
    form.values.name !== currentLeaderboardName;

  return (
    <div>
      <Button variant="outline" onClick={toggleModal}>
        Create
      </Button>
      <Modal
        opened={isModalOpen}
        onClose={toggleModal}
        title={"Create New Leaderboard"}
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
            disabled={!isValid}
          >
            Submit
          </Button>
        </form>
      </Modal>
    </div>
  );
}

export default NewLeaderboardForm;
