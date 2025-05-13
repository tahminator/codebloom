import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { Leaderboard } from "@/lib/api/types/leaderboard";
import { User } from "@/lib/api/types/user";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export const useToggleAdminMutation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    // Check the mutation function regarding `metadata`.
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
      ]) as UnknownApiResponse<Page<User[]>>;

      // Impossible, just handle it to make TS happy and narrow the type.
      if (!previousApiResponse.success) {
        return;
      }

      // Find the user and insert the new admin status to it's object.
      const newUsers = previousApiResponse.payload.items.map((user) =>
        user.id === userId ? { ...user, admin: toggleTo } : user,
      ) as User[];

      // Replace the data (users array) with the newUsers, and keep the rest of the Page items the same.
      //
      // Remember, ApiResponse<Page<T>> has a type that looks like this:
      //
      // {
      //  success: boolean;
      //  message: string;
      //  // This `data` is `Page`.
      //  data: {
      //    page: number;
      //    pageSize: number;
      //    ...
      //    // This is the actual array of users.
      //    data: T
      //  };
      // }
      const newPage = {
        ...previousApiResponse.payload,
        data: newUsers,
      } as Page<User[]>;

      // Insert this new Page type, and keep the success and message from the previous API response.
      queryClient.setQueryData(
        ["user", "all", metadata.page, metadata.debouncedQuery],
        { ...previousApiResponse, data: newPage },
      );

      // This is context that is passed on, so we can hold onto it and possibly put it back if needed.
      return { previousApiResponse };
    },
    onError: (_, newData, ctx) => {
      const { metadata } = newData;
      queryClient.setQueryData(
        ["user", "all", metadata.page, metadata.debouncedQuery],
        // Shallow copy because sometimes there is a bug with the object passed in by reference.
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

  const json = (await response.json()) as UnknownApiResponse<User>;

  return json;
}

export async function createLeaderboard(leaderboard: { name: string }) {
  const response = await fetch("/api/admin/leaderboard/create", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(leaderboard),
  });

  const json = (await response.json()) as UnknownApiResponse<Leaderboard>;
  return json;
}

export const useCreateLeaderboardMutation = () => {
  const queryClient = useQueryClient();
  return useMutation<UnknownApiResponse<Leaderboard>, Error, { name: string }>({
    mutationFn: createLeaderboard,
    onSuccess: async (data) => {
      if (data.success) {
        await queryClient.invalidateQueries({ queryKey: ["leaderboard"] });
      }
    },
  });
};
