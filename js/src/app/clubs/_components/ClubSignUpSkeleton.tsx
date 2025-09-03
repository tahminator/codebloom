import { Card, Center, Skeleton, Stack } from "@mantine/core";

export default function ClubSignUpSkeleton() {
  return (
    <Center style={{ minHeight: "70vh", padding: 16 }}>
      <Card
        shadow="sm"
        radius="lg"
        p="xl"
        withBorder
        miw={360}
        maw={480}
        w="100%"
      >
        <Stack gap="md">
          {/* Splash image placeholder */}
          <Skeleton height={120} width={160} mx="auto" radius="md" />

          {/* Title */}
          <Skeleton height={28} width="70%" mx="auto" />

          {/* Subtitle text */}
          <Skeleton height={18} width="60%" mx="auto" />

          {/* Input field */}
          <Skeleton height={44} radius="md" />

          {/* Button */}
          <Skeleton height={36} radius="md" />
        </Stack>
      </Card>
    </Center>
  );
}
