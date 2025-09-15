import { useLatestAnnouncement } from "@/lib/api/queries/announcement";
import { Button } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

import DeleteAnnouncementModal from "./DeleteAnnouncementModal";

export default function DeleteAnnouncementButton() {
  const { data, status } = useLatestAnnouncement();
  const [opened, { open, close }] = useDisclosure(false);

  if (status === "pending") {
    return <></>;
  }

  if (status == "error") {
    return <></>;
  }

  if (!data.success || !data.payload) {
    return <></>;
  }

  if (!data.payload.id) {
    return <></>;
  }

  const id = data.payload.id;

  return (
    <>
      <Button top={20} color="red" onClick={open}>
        Delete Announcement
      </Button>
      <DeleteAnnouncementModal opened={opened} onClose={close} id={id} />
    </>
  );
}
