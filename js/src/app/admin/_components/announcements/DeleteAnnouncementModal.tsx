import { useDeleteAnnouncementMutation } from "@/lib/api/queries/admin";
import { useLatestAnnouncement } from "@/lib/api/queries/announcement";
import { disableAnnouncementSchema } from "@/lib/api/schema/admin";
import { Button, Modal, Text, TextInput } from "@mantine/core";
import { useForm, zodResolver } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useEffect, useRef, useState } from "react";

export default function DeleteAnnouncementModal() {
  const [isModalOpen, setModalOpen] = useState(false);
  const { mutate, status } = useDeleteAnnouncementMutation();
  const { data } = useLatestAnnouncement();

  const justDeletedRef = useRef(false);

  useEffect(() => {
    if (data?.payload?.id) {
      justDeletedRef.current = false;
    }
  }, [data?.payload?.id]);

  const form = useForm({
    validate: zodResolver(disableAnnouncementSchema),
    initialValues: {
      id: "",
    },
  });

  const toggleModal = async () => {
    setModalOpen((prev) => !prev);

    if (!isModalOpen) {
      if (justDeletedRef.current) {
        form.setFieldValue("id", "");
      } else {
        const latestId = data?.payload?.id ?? "";
        form.setFieldValue("id", latestId);
      }
    }
  };

  const onSubmit = async () => {
    const latestId = data?.payload?.id ?? "";

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
            justDeletedRef.current = true;
            form.reset();
            form.setFieldValue("id", "");
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
