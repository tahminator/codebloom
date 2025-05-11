import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useToggleAdminMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: toggleUserAdmin,
    onMutate: async (newAdminStatus) => {
      await queryClient.cancelQueries({
        queryKey: ["submission", "user", "all"],
      });

      const users = queryClient.getQueryData(["submission", "user", "all"]);

      console.log(users);
    },
  });
};

async function toggleUserAdmin({
  userId,
  toggleTo,
}: {
  userId: string;
  toggleTo: boolean;
}) {
  const res = await fetch("/api/admin/user/toggle", {
    method: "POST",
    body: JSON.stringify({
      id: userId,
      toggleTo,
    }),
  });

  return;
}
