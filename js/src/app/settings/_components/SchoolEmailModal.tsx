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
          <Text>
            Verify your student email to gain access to school-specific
            competitions!
          </Text>
        </Center>
      </Box>
    </Modal>
  );
}
