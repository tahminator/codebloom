import { useAuthQuery } from "@/lib/api/queries/auth";
import { Title } from "@mantine/core";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function AdminIndex() {
  const { data, status } = useAuthQuery();
  const navigate = useNavigate();

  useEffect(() => {
    if (status === "success" && !data.isAdmin) {
      navigate("/");
    }
  }, [data, status, navigate]);

  if (status == "pending" || !data?.isAdmin) {
    return null;
  }
  return (
    <div>
      <Title order={1} ta="center">
        Admin Page
      </Title>
    </div>
  );
}
