import LogoutAllSessionsModal from "@/app/settings/_components/LogoutAllSessionsModal";
import { Box, Button, Card, Title, Text } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

export default function LogoutAllSessionsCard() {
  const [modalOpen, { open: openModal, close: closeModal }] =
    useDisclosure(false);

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
            <Button mt="sm" color="red" onClick={openModal}>
              Log Out All Sessions
            </Button>
          </Box>
          <LogoutAllSessionsModal enabled={modalOpen} close={closeModal} />
        </Box>
      </Card>
    </Box>
  );
}
