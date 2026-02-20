import UserSearch from "@/app/user/all/_components/UserSearch";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Box } from "@mantine/core";

export default function UserSearchPage() {
  return (
    <>
      <DocumentTitle title="CodeBloom - Search Users" />
      <DocumentDescription description="CodeBloom - Search for users" />
      <Box>
        <UserSearch />
      </Box>
    </>
  );
}
