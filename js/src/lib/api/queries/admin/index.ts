import { ApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { User } from "@/lib/api/types/user";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useToggleAdminMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: toggleUserAdmin,
    onMutate: async (newData) => {
      const { userId, toggleTo, metadata } = newData;
      await queryClient.cancelQueries({
        queryKey: ["user", "all", metadata.page, metadata.debouncedQuery],
      });

      const previousApiResponse = queryClient.getQueryData([
        "user",
        "all",
        metadata.page,
        metadata.debouncedQuery,
      ]) as ApiResponse<Page<User[]>>;

      // Impossible, just handle it to make TS happy and narrow the type.
      if (!previousApiResponse.success) {
        return;
      }

      const newUsers = previousApiResponse.data.data.map((user) =>
        user.id === userId ? { ...user, admin: toggleTo } : user,
      ) as User[];

      const newPage = { ...previousApiResponse.data, data: newUsers } as Page<
        User[]
      >;

      queryClient.setQueryData(
        ["user", "all", metadata.page, metadata.debouncedQuery],
        { ...previousApiResponse, data: newPage },
      );

      return { previousApiResponse };
    },
    onError: (_, newData, ctx) => {
      const { metadata } = newData;
      queryClient.setQueryData(
        ["user", "all", metadata.page, metadata.debouncedQuery],
        { ...ctx?.previousApiResponse },
      );
    },
    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: ["user", "all"],
      });
    },
  });
};

async function toggleUserAdmin({
  userId,
  toggleTo,
}: {
  userId: string;
  toggleTo: boolean;
  /**
   * This metadata property isn't used in the fetcher function at all.
   * Instead, it is used in order to pinpoint exactly what data we will be mutating
   * in the optimistic update.
   */
  metadata: {
    page: number;
    debouncedQuery: string;
  };
}) {
  // If you want to test the optimistic updates, uncomment the lines below.
  //
  // await new Promise((res, rej) => {
  //   setTimeout(() => {
  //     res(-1);
  //   }, 10000);
  // });
  // throw new Error("hi");

  const response = await fetch("/api/admin/user/admin/toggle", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      id: userId,
      toggleTo,
    }),
  });

  const json = (await response.json()) as ApiResponse<User>;

  return json;
}
