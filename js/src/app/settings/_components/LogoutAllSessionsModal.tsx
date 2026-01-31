import {
  Box,
  Button,
  CloseButton,
  Flex,
  Modal,
  Text,
  Title,
} from "@mantine/core";
import { Link } from "react-router-dom";

type LogoutAllSessionsModalProps = {
  enabled: boolean;
  toggle: () => void;
};

export default function LogoutAllSessionsModal({
  enabled,
  toggle,
}: LogoutAllSessionsModalProps) {
  return (
    <Modal opened={enabled} onClose={toggle} withCloseButton={false}>
      <Flex justify="space-between" align="center" mb="md">
        <Title order={4}>Log Out All Sessions</Title>
        <CloseButton onClick={toggle} />
      </Flex>
      <Box p={"lg"}>
        <Text p="md">
          Are you sure you want to log out of all sessions? This will sign you
          out on all devices and browsers.
        </Text>
        <Flex gap="sm" justify="flex-end" p="md">
          <Button variant="default" onClick={toggle}>
            Cancel
          </Button>
          <Button
            component={Link}
            to={"/api/auth/logout/all"}
            reloadDocument
            color="red"
          >
            Log Out All Sessions
          </Button>
        </Flex>
      </Box>
    </Modal>
  );
}
