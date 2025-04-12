import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Loader, Title } from "@mantine/core";

export default function AdminIndex() {
  const { data, status } = useAuthQuery();
  if(status == "pending"){
    return (
        <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }
  if(status == "error"){
    return <Toast message = {"Something went wrong."} />;
  }
  const {isAdmin} = data;
    if(!isAdmin){
        return <ToastWithRedirect message = {"You are not authorized to view this page."} to = "/" />;
        }
  return (
    <div>
      <Title order={1} ta="center">
        Admin Page
      </Title>
    </div>
  );
}
