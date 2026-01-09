import { ApiUtils } from "@/lib/api/utils";
import {
  Box,
  Container,
  Flex,
  Stack,
  Title,
  Text,
  Center,
  Image,
  Divider,
} from "@mantine/core";

export default function SchoolSection() {
  const schools = ApiUtils.getAllSupportedTagEnumMetadata();
  const steps = [
    "Sign up for CodeBloom with your Discord account",
    "Follow the onboarding instructions to link your LeetCode account with Codebloom",
    "Head to Settings (top right icon > Settings)",
    "Register with your university email to join your school's leaderboard",
  ];

  return (
    <Box py="xl" mb="xl">
      <Container size="xl">
        <Center mb="xl">
          <Title order={3} style={{ marginBottom: "2rem" }}>
            Supported Universities & Colleges
          </Title>
        </Center>
        <Center>
          <Flex
            wrap="wrap"
            gap="2rem"
            justify="center"
            style={{
              maxWidth: "1200px",
            }}
          >
            {schools.map((school) => (
              <Stack
                key={school.apiKey}
                align="center"
                gap={8}
                style={{ width: "150px", flexShrink: 0 }}
              >
                <Image
                  src={school.icon}
                  alt={school.alt}
                  h={81}
                  w={81}
                  fit="contain"
                />
                <Text size="sm" fw={550} ta="center">
                  {school.name}
                </Text>
              </Stack>
            ))}
          </Flex>
        </Center>
        <Divider my="xl" />
        <Center mt="xl">
          <Stack
            gap="md"
            align="center"
            style={{ width: "100%", maxWidth: 720 }}
          >
            <Title order={4} style={{ color: "#4cffb0", textAlign: "center" }}>
              Join in just a few simple steps:
            </Title>
            <ol
              style={{
                listStyleType: "decimal",
                paddingInlineStart: "1.25rem",
                marginTop: "1rem",
              }}
            >
              {steps.map((step, index) => (
                <li key={index} style={{ marginBottom: "0.75rem" }}>
                  <Text size="md">{step}</Text>
                </li>
              ))}
            </ol>
          </Stack>
        </Center>
      </Container>
    </Box>
  );
}
