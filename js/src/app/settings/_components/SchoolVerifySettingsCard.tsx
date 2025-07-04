import { User } from "@/lib/api/types/user";
import { Box, Button, Card, Center, Text, Title } from "@mantine/core";
import { useState } from "react";

import SchoolEmailModal from "./SchoolEmailModal";

export default function SchoolVerifySettingsCard({ user }: { user: User }) {
  const [modalOpen, setModalOpen] = useState(false);
  const toggleModal = () => setModalOpen((prev) => !prev);
  return (
    <Box>
      <Card withBorder padding={"md"} radius={"md"}>
        <Center mb="md">
          <Title order={3}>Verify School</Title>
        </Center>

        <Center mb="sm">
          <Text> Welcome to the settings page, {user.nickname} </Text>
        </Center>

        <Center>
          <Button onClick={toggleModal}>Verify Now</Button>
        </Center>

        <SchoolEmailModal enabled={modalOpen} toggle={toggleModal} />
      </Card>
    </Box>
  );
}
