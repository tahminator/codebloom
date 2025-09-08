import { useState } from "react";
import { Button, Modal, Text, TextInput } from "@mantine/core";
import { useForm, zodResolver } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useDeleteAnnouncementMutation } from "@/lib/api/queries/admin";
import { disableAnnouncementSchema } from "@/lib/api/schema/admin";
import { useLatestAnnouncement } from "@/lib/api/queries/announcement";

export default function DeleteAnnouncementModal() {
  const [isModalOpen, setModalOpen] = useState(false);
  const { mutate, status } = useDeleteAnnouncementMutation();
  const { data } = useLatestAnnouncement();

  const form = useForm({
    validate: zodResolver(disableAnnouncementSchema),
    initialValues: {
      id: "",
    },
  });

  const toggleModal = async () => {
    setModalOpen((prev) => !prev);

    if (!isModalOpen) {
      const latestId = data?.payload?.id ?? "";
      form.setFieldValue("id", latestId);
    }
  };

  const onSubmit = async () => {
    let latestId = data?.payload?.id ?? "";

    if (!latestId) {
      notifications.show({
        message: "No active announcement to disable.",
        color: "red",
      });
      return;
    }

    mutate(
      { id: latestId },
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
