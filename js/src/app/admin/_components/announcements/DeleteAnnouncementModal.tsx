import { useDeleteAnnouncementMutation } from "@/lib/api/queries/admin";
import { disableAnnouncementSchema } from "@/lib/api/schema/admin";
import { Button, Modal, Text, TextInput } from "@mantine/core";
import { useForm, zodResolver } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useState } from "react";

export default function DeleteAnnouncementModal({ id }: { id?: string }) {
  const [isModalOpen, setModalOpen] = useState(false);
  const [currentId, setCurrentId] = useState<string>("");
  const { mutate, status } = useDeleteAnnouncementMutation();

  const form = useForm({
    validate: zodResolver(disableAnnouncementSchema),
    initialValues: {
      id: "",
    },
  });

  const toggleModal = async () => {
    setModalOpen((prev) => !prev);

    if (!isModalOpen) {
      setCurrentId(id ?? "");
      form.setFieldValue("id", id ?? "");
    }
  };

  const onSubmit = async () => {
    if (!currentId) {
      notifications.show({
        message: "No active announcement to disable.",
        color: "red",
      });
      return;
    }

    mutate(
      { id: currentId },
      {
        onSuccess: async (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });

          setCurrentId("");
          setModalOpen(false);
        },
      },
    );
  };

  return (
    <>
      <Button onClick={toggleModal} top={20} color="red">
        Disable Announcement
      </Button>
      <Modal
        opened={isModalOpen}
        onClose={toggleModal}
        title="Disable Announcement"
      >
        <form onSubmit={form.onSubmit(onSubmit)}>
          <Text mt={12}>
            Are you sure you want to disable the latest announcement?
          </Text>
          <TextInput
            {...form.getInputProps("id")}
            value={currentId}
            label="Announcement ID"
            error={form.errors.id}
            withAsterisk
            mt="sm"
            readOnly
          />
          <Button
            type="submit"
            size="xs"
            mt="sm"
            variant="outline"
            color="red"
            disabled={status === "pending"}
            loading={status === "pending"}
          >
            Disable
          </Button>
        </form>
      </Modal>
    </>
  );
}
