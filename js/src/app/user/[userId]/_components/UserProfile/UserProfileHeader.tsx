import UserProfileHeaderSkeleton from "@/app/user/[userId]/_components/UserProfile/UserProfileHeaderSkeleton";
import Toast from "@/components/ui/toast/Toast";
import { useUserProfileQuery } from "@/lib/api/queries/user";
import { theme } from "@/lib/theme";
import { Flex, Group, Stack, Title, Tooltip } from "@mantine/core";
import { IconCircleCheckFilled } from "@tabler/icons-react";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";
import { Link } from "react-router-dom";

export default function UserProfileHeader({
  leetcodeUsername,
}: {
  leetcodeUsername?: string;
}) {
  const { data, status } = useUserProfileQuery({ leetcodeUsername });

  if (status === "pending") {
    return <UserProfileHeaderSkeleton />;
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong. Please try again later." />
    );
  }

  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const userProfile = data.payload;
  return (
    <Flex direction={"row"} gap="xs" wrap={"wrap"} mb={"xs"}>
      <Stack justify="center" gap="xs">
        {userProfile.nickname ?
          <>
            <Tooltip
              label={
                "This user is a verified member of the Patina Discord server."
              }
              color={"dark.4"}
            >
              <Title size="h4" c="patina.4">
                <IconCircleCheckFilled
                  className="inline"
                  color={theme.colors.patina[4]}
                  z={5000000}
                  size={20}
                />{" "}
                {userProfile.nickname}
              </Title>
            </Tooltip>
          </>
        : <>
            <Group gap="2px">
              <FaDiscord
                style={{
                  color: "var(--mantine-color-blue-5)",
                  fontSize: "1.5rem",
                  paddingRight: "0",
                }}
              />
              <Title size="h4" c="blue.5">
                {userProfile.discordName}
              </Title>
            </Group>
          </>
        }
        <Link
          to={`https://leetcode.com/u/${userProfile.leetcodeUsername}`}
          className="hover:underline"
          style={{ display: "flex", alignItems: "center", gap: "4px" }}
        >
          <SiLeetcode
            style={{
              color: "var(--mantine-color-yellow-5)",
              fontSize: "1.5rem",
            }}
          />
          <Title size="h4" c="yellow.5">
            {userProfile.leetcodeUsername}
          </Title>
        </Link>
      </Stack>
    </Flex>
  );
}
