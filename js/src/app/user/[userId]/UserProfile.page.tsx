import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import Header from "@/components/ui/header/Header";
import { Flex, Box, Center, Text, Button} from "@mantine/core";
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
        <Flex
            direction={{ base: "column", sm: "row" }}
            gap="md"
            justify="center"
            wrap="wrap"
            w="100%"
            p="100px"
            pt="md"
        >
            <Box
                style={{
                    flexBasis: "200px",
                    flexGrow: 1,
                    boxShadow: "0 4px 10px rgba(0, 0, 0, .5)"
                }}
                bg="gray.9"
                p="md"
                bdrs="md"
            >
                <Flex direction="column" align="center" gap="sm" pt={"20px"}>
                    <ProfilePicture />

                    <UserProfileHeader userId={userId}/>
                </Flex>
            </Box>

            <Box
                style={{
                    flexGrow: 3,
                    minWidth: "400px",    // wraps under 400px
                    boxShadow: "0 4px 10px rgba(0, 0, 0, .5)"
                }}
                bg="gray.9"
                p="md"
                bdrs="md"
            >
                <Center mb="md">
                    <Text size="xl" fw={700} c="yellow.5">
                        Recent Submissions
                    </Text>
                </Center>
                <MiniUserSubmissions userId={userId} />

                <Button
                    fullWidth
                    mt="sm"
                    variant="outline"
                    component={Link}
                    to={`/user/${userId}/submissions`}
                >
                    All Submissions
                </Button>
            </Box>
        </Flex>
    </>
  );

}
