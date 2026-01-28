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
  List,
  Button,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";

const schools = ApiUtils.getAllSchoolTagEnumMetadata();
const steps = [
  "Sign up for CodeBloom with your Discord account",
  "Follow the onboarding instructions to link your LeetCode account with CodeBloom",
  "Head to Settings (top right icon > Settings)",
  "Register with your university email to join your school's leaderboard",
];

const SCHOOLS_PER_ROW = Math.floor(1200 / (150 + 32));
const INITIAL_SCHOOLS_COUNT = SCHOOLS_PER_ROW;

export default function SchoolSection() {
  const [showAll, { toggle }] = useDisclosure(false);

  const visibleSchools =
    showAll ? schools : schools.slice(0, INITIAL_SCHOOLS_COUNT);

  return (
    <Box py="xl" mb="xl">
      <Container size="xl">
        <Center mb="xl">
          <Title order={3} mb="2rem">
            Supported Universities & Colleges
          </Title>
        </Center>
        <Stack gap="md" align="center">
          <Flex wrap="wrap" gap="2rem" justify="center" maw="1200px">
            {visibleSchools.map((school) => (
              <Stack key={school.apiKey} align="center" gap={8} w={150}>
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
          {schools.length > INITIAL_SCHOOLS_COUNT && (
            <Button variant="default" onClick={toggle}>
              {showAll ? "Show Less" : "Show All"}
            </Button>
          )}
        </Stack>
        <Divider my="xl" />
        <Center mt="xl">
          <Stack gap="md" align="center" w="100%" maw={720}>
            <Title order={4} c="#4cffb0" ta="center">
              Join your University Leaderboard (4 Easy Steps):
            </Title>
            <List type="ordered" listStyleType="decimal" spacing="sm">
              {steps.map((step, index) => (
                <List.Item key={index}>
                  <Text size="md">{step}</Text>
                </List.Item>
              ))}
            </List>
          </Stack>
        </Center>
      </Container>
    </Box>
  );
}
