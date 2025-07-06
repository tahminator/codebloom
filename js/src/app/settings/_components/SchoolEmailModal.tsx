import { Box, Center, Modal, Text } from "@mantine/core";

type SchoolModalProps = {
  enabled: boolean;
  toggle: () => void;
};

export default function SchoolEmailModal({
  enabled,
  toggle,
}: SchoolModalProps) {
  return (
    <Modal opened={enabled} onClose={toggle}>
      <Box>
        <Center>
          <Text>This is the modal where you will verify yourself.</Text>
        </Center>
      </Box>
    </Modal>
  );
}
