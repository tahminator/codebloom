import { useAuthQuery } from "@/lib/api/queries/auth";
import { Button } from "@mantine/core";

export function GotoAdminPageButton() {
  const { data, status } = useAuthQuery();
  if (status !== "success") {
    return null;
  }
  if (!data.isAdmin) {
    return null;
  }
  return (
    <Button
      component="a"
      href="/admin"
      mt={0}
      size="xs"
      variant="outline"
      style={{}}
    >
      Admin
    </Button>
  );
}
