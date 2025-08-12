import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import Header from "@/components/ui/header/Header";
import { Flex, Box, Center, Text, Button } from "@mantine/core";
import { Link, useNavigate, useParams } from "react-router-dom";

import ProfilePicture from "./_components/UserProfile/ProfilePicture/ProfilePicture";
import MiniUserSubmissions from "./_components/UserSubmissions/MiniUserSubmissions";

export default function UserProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();
  // const { data, status } = useUserProfileQuery({ userId });

  return (
    <>
      <Header />
      <Center mt={"xs"} pt="20">
        <Button
          variant={"outline"}
          onClick={() => {
            navigate(-1);
          }}
        >
          ← Go back
        </Button>
      </Center>
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
          {/* Left card */}
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
              <ProfilePicture />
              <UserProfileHeader userId={userId} />
            </Flex>
          </Box>

          {/* Right card */}
          <Box
            bg="gray.9"
            w="100%"
            p="md"
            bdrs="md"
            style={{
              flex: "3 1 360px", // prefer bigger, but allow shrink
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
                to={`/user/${userId}/submissions`}
              >
                View All
              </Button>
            </Flex>

            <Box >
              <MiniUserSubmissions userId={userId} />
            </Box>
          </Box>
        </Flex>
      </Center>
    </>
  );
}
