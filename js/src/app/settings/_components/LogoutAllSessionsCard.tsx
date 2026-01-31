import LogoutAllSessionsModal from "@/app/settings/_components/LogoutAllSessionsModal";
import { Box, Button, Card, Title, Text } from "@mantine/core";
import { useState } from "react";

export default function LogoutAllSessionsCard() {
  const [modalOpen, setModalOpen] = useState(false);
  const toggleModal = () => setModalOpen((prev) => !prev);

  return (
    <Box>
      <Card withBorder padding={"md"} radius={"md"}>
        <Box m={"md"}>
          <Title order={3} p={"md"}>
            Log Out All Sessions
          </Title>
          <Text pt={"md"} pl={"md"}>
            Log out of all active sessions across all devices and browsers. You
            will need to log in again on each device.
          </Text>
          <Box pt={"sm"} pl={"md"}>
            <Button mt="sm" color="red" onClick={toggleModal}>
              Log Out All Sessions
            </Button>
          </Box>
          <LogoutAllSessionsModal enabled={modalOpen} toggle={toggleModal} />
        </Box>
      </Card>
    </Box>
  );
}
