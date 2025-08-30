import { Box, Center, Image, Text } from "@mantine/core";

export default function GwcBrandHeader() {
  return (
    <Box p={"sm"}>
      <Center>
        <Image src="/Gwc_Logo.png" alt="GWC" h={44} w="auto" radius="xs" />
        <Text fw={700} size="xl" p={"md"}>
          ×
        </Text>
        <Image src="/logo.png" alt="Codebloom" h={44} w="auto" radius="xs" />
      </Center>
    </Box>
  );
}
