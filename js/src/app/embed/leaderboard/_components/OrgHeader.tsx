import { ApiUtils } from "@/lib/api/utils";
import { ApiTypeUtils } from "@/lib/api/utils/types";
import { Box, Center, Image, Text } from "@mantine/core";

type OrgHeaderProps = {
  orgTag?: ApiTypeUtils.FilteredTag;
};

export default function OrgHeader({ orgTag }: OrgHeaderProps) {
  const orgTagLink =
    orgTag ? ApiUtils.getMetadataByTagEnum(orgTag).icon : undefined;

  return (
    <Box p={"sm"}>
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
