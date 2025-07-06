import { Box, Button, Card, Center, Title } from "@mantine/core";
import { useState } from "react";

import SchoolEmailModal from "./SchoolEmailModal";

type SchoolVerifyProps = {
  schoolExists: boolean;
};

export default function SchoolVerifySettingsCard({
  schoolExists,
}: SchoolVerifyProps) {
  const [modalOpen, setModalOpen] = useState(false);
  const toggleModal = () => setModalOpen((prev) => !prev);
  return (
    <Box>
      <Card withBorder padding={"md"} radius={"md"}>
        <Center mb="md">
          <Title order={3}>Verify School</Title>
        </Center>
        {schoolExists ?
          <Center>
            <Button disabled onClick={toggleModal}>
              You are already verified!
            </Button>
          </Center>
        : <Center>
            <Button onClick={toggleModal}>Verify Now</Button>
          </Center>
        }
        <SchoolEmailModal enabled={modalOpen} toggle={toggleModal} />
      </Card>
    </Box>
  );
}
