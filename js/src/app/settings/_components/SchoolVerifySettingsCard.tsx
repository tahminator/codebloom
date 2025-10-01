import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { ApiUtils } from "@/lib/api/utils";
import {
  Box,
  Button,
  Card,
  Title,
  Text,
  List,
  Image,
  Flex,
} from "@mantine/core";
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
    <>
      <DocumentTitle title={`CodeBloom - Verify School`} />
      <DocumentDescription description={`CodeBloom - Verify school email`} />
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
                  <List.Item key={school.name}>
                    <Flex gap={"sm"} direction={"row"}>
                      <Image src={school.icon} alt={school.alt} h={20} w={20} />
                      {school.name}
                    </Flex>
                  </List.Item>
                ))}
              </List>
            </Text>
            <Box pt={"sm"} pl={"md"}>
              <Button mt="sm" onClick={toggleModal} disabled={schoolExists}>
                {schoolExists ? "You are already verified!" : "Verify Now"}
              </Button>
            </Box>
            <SchoolEmailModal enabled={modalOpen} toggle={toggleModal} />
          </Box>
        </Card>
      </Box>
    </>
  );
}
