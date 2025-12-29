import { Box, Card, Title, Text } from "@mantine/core";

export default function ChangeImageSettingsCard() {
  return (
    <>
      <Box>
        <Card withBorder padding={"md"} radius={"md"}>
          <Box m={"md"}>
            <Title order={3} p={"md"}>
              Change Image
            </Title>
            <Text pt={"md"} pl={"md"}>
              To update your profile picture, please change your icon on
              LeetCode.
            </Text>
            <Text pt={"md"} pl={"md"}>
              After updating, simply log out and log back into CodeBloom for the
              changes to take effect.
            </Text>
          </Box>
        </Card>
      </Box>
    </>
  );
}
