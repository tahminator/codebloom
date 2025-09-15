import { useDeleteAnnouncementMutation } from "@/lib/api/queries/admin";
import { Modal, Text, Button } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { FormEvent } from "react";

export default function DeleteAnnouncementModal({
  id,
  opened,
  onClose,
}: {
  id: string;
  opened: boolean;
  onClose: () => void;
}) {
  const { mutate, status } = useDeleteAnnouncementMutation();

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault();
    e.stopPropagation();
    mutate(
      { id: id },
      {
        onSuccess: async (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });
          onClose();
        },
      },
    );
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Disable Announcement">
      <form onSubmit={onSubmit}>
        <Text mt={12}>
          Are you sure you want to disable the latest announcement?
        </Text>
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
  );
}
