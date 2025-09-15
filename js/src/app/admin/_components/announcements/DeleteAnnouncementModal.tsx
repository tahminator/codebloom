import { useDeleteAnnouncementMutation } from "@/lib/api/queries/admin";
import { Modal, Text, Button } from "@mantine/core";
import { useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";

export default function DeleteAnnouncementModal({
  id,
  opened,
  onClose,
}: {
  id?: string;
  opened: boolean;
  onClose: () => void;
}) {
  const { mutate, status } = useDeleteAnnouncementMutation();

  const form = useForm({
    initialValues: {
      id: id,
    },
  });

  const onSubmit = async () => {
    if (!form.values.id) {
      notifications.show({
        message: "No active announcement to disable.",
        color: "red",
      });
      return;
    }

    mutate(
      { id: form.values.id },
      {
        onSuccess: async (data) => {
          notifications.show({
            message: data.message,
            color: data.success ? undefined : "red",
          });
          form.reset();
          onClose();
        },
      },
    );
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Disable Announcement">
      <form onSubmit={form.onSubmit(onSubmit)}>
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
