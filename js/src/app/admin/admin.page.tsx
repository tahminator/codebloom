import Toast from "@/components/ui/toast/Toast.tsx";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect.tsx";
import { useAuthQuery } from "@/lib/api/queries/auth/index.ts";
import { Loader } from "@mantine/core";

import AdminIndex from "./_components/AdminIndex.tsx";

export default function AdminPage() {
  const { data, status } = useAuthQuery();

  if (status == "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status == "error") {
    return <Toast message={"Something went wrong."} />;
  }

  const { isAdmin } = data;

  if (!isAdmin) {
    return (
      <ToastWithRedirect
        message={"You are not authorized to view this page."}
        to="/"
      />
    );
  }
  return <AdminIndex />;
}
