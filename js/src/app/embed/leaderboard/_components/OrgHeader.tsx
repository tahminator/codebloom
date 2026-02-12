import { Tag } from "@/lib/api/types/schema";
import { ApiUtils } from "@/lib/api/utils";
import { Box, Center, Image, Text } from "@mantine/core";

type OrgHeaderProps = {
  orgTag?: Tag;
};

export default function OrgHeader({ orgTag }: OrgHeaderProps) {
  const orgTagLink =
    orgTag ? ApiUtils.getMetadataByTagEnum(orgTag).icon : undefined;

  return (
    <Box>
      <Center>
        {orgTagLink && (
          <>
            <Image src={orgTagLink} alt={orgTag} h={44} w="auto" radius="xs" />
            <Text fw={700} size="xl" p={"md"}>
              ×
            </Text>
          </>
        )}
        <Image src="/logo.png" alt="Codebloom" h={44} w="auto" radius="xs" />
      </Center>
    </Box>
  );
}
