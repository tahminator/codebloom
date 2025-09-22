import { ApiUtils } from "@/lib/api/utils";
import { Box, Button, Card, Title, Text, List } from "@mantine/core";
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
        <Box m={"md"}>
          <Title order={3} p={"md"}>
            Verify School
          </Title>
          <Text pt={"md"} pl={"md"}>
            Verify your school email here for access to our school specific
            leaderboards.
          </Text>
          <Text pt={"md"} pl={"md"}>
            Supported schools:
            <List>
              {ApiUtils.getAllSupportedTagEnumMetadata().map((school) => (
                <List.Item key={school.name}>{school.name}</List.Item>
              ))}
            </List>
          </Text>
          <Box pt={"sm"} pl={"md"}>
            <Button mt="sm" onClick={toggleModal} disabled={schoolExists}>
              {schoolExists ? "You are already verified!" : "Verify Now"}
            </Button>
          </Box>
        </Box>
        <SchoolEmailModal enabled={modalOpen} toggle={toggleModal} />
      </Card>
    </Box>
  );
}
