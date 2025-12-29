import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import {
  Box,
  Card,
  Title,
  Text
} from "@mantine/core";

export default function ChangeImageSettingsCard(){
  return (
    <>
      <DocumentTitle title={`CodeBloom - Changed Image`} />
      <DocumentDescription description="Instructions on how to update your profile picture." />
      <Box>
        <Card withBorder padding={"md"} radius={"md"}>
          <Box m={"md"}>
            <Title order={3} p={"md"}>
              Change Image
            </Title>
            <Text pt={"md"} pl={"md"}>
              To update your profile picture, please change your icon on LeetCode.
            </Text>
            <Text pt={"md"} pl={"md"}>
              After updating, simply log out and log back into CodeBloom for the changes to take effect.
            </Text>
          </Box>
        </Card>
      </Box>
    </>
  );
}