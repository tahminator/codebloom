import ProfilePicture from "@/app/user/[userId]/_components/UserProfile/ProfilePicture/ProfilePicture";
import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import UserTags from "@/app/user/[userId]/_components/UserProfile/UserTags/UserTags";
import MiniUserSubmissions from "@/app/user/[userId]/submissions/_components/UserSubmissions/MiniUserSubmissions";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { getUserSubmissionsUrl } from "@/lib/helper/leaderboardDateRange";
import { Center, Button, Flex, Box, Text } from "@mantine/core";
import { Link, useParams, useSearchParams } from "react-router-dom";

export default function UserProfilePage() {
  const { userId } = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const startDate = searchParams.get("startDate") ?? undefined;
  const endDate = searchParams.get("endDate") ?? undefined;
  const hasDateRange = !!startDate;

  if (!userId) {
    return <ToastWithRedirect to={-1} message={"This user ID is not valid."} />;
  }

  const dateRange = startDate ? { startDate, endDate } : undefined;
  const viewAllUrl = getUserSubmissionsUrl(userId, dateRange);

  return (
    <>
      {hasDateRange && (
        <Flex justify="flex-end" maw="1440px" w="100%" mx="auto" px="5%" pt="md">
          <Button
            variant="filled"
            color="red"
            size="compact-sm"
            onClick={() => {
              const newParams = new URLSearchParams(searchParams);
              newParams.delete("startDate");
              newParams.delete("endDate");
              setSearchParams(newParams);
            }}
          >
            Clear Date Range
          </Button>
        </Flex>
      )}
      <Center mt={hasDateRange ? "xs" : "xs"} pt={hasDateRange ? 0 : 20}></Center>
      <Center>
        <Flex
          direction={{ base: "column", sm: "row" }}
          w="100%"
          gap="md"
          maw="1440px"
          p="5%"
          pt="md"
          wrap="wrap"
        >
          <Box
            bg="gray.9"
            w="100%"
            p="md"
            bdrs="md"
            style={{
              flex: "1 1 260px",
              minWidth: 0,
              boxShadow: "0 4px 10px rgba(0,0,0,.5)",
            }}
          >
            <Flex direction="column" align="center" gap="sm" pt="20px">
              <ProfilePicture userId={userId} />
              <UserProfileHeader userId={userId} />
              <UserTags userId={userId} />
            </Flex>
          </Box>
          <Box
            bg="gray.9"
            w="100%"
            p="md"
            bdrs="md"
            style={{
              flex: "3 1 360px",
              minWidth: "75%",
              boxShadow: "0 4px 10px rgba(0,0,0,.5)",
            }}
          >
            <Flex
              direction={{ base: "column", md: "row" }}
              justify="space-between"
              w="100%"
              align="center"
              px="20px"
              gap="10px"
            >
              <Text size="clamp(1.2rem, 2vw, 2em)" fw={700} c="white">
                Recent Submissions
              </Text>
              <Button
                variant="light"
                component={Link}
                to={viewAllUrl}
              >
                View All
              </Button>
            </Flex>
            <Box>
              <MiniUserSubmissions userId={userId} />
            </Box>
          </Box>
        </Flex>
      </Center>
    </>
  );
}
