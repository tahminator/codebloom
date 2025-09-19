import { UnknownApiResponse } from "@/lib/api/common/apiResponse";
import { Page } from "@/lib/api/common/page";
import { Announcement } from "@/lib/api/types/announcement";
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
      queryClient.cancelQueries({
        queryKey: ["user", "all", metadata.page, metadata.debouncedQuery],
      });

      const previousApiResponse = queryClient.getQueryData<
        UnknownApiResponse<Page<User[]>>
      >(["user", "all", metadata.page, metadata.debouncedQuery]);

      // Impossible, just handle it to make TS happy and narrow the type.
      if (!previousApiResponse || !previousApiResponse.success) {
        return;
      }

      // Find the user and insert the new admin status to it's object.
      const newUsers: User[] = previousApiResponse.payload.items.map((user) =>
        user.id === userId ? { ...user, admin: toggleTo } : user,
      );

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
      const newPage: Page<User[]> = {
        ...previousApiResponse.payload,
        items: newUsers,
      };

      // Insert this new Page type, and keep the success and message from the previous API response.
      queryClient.setQueryData<UnknownApiResponse<Page<User[]>>>(
        ["user", "all", metadata.page, metadata.debouncedQuery],
        { ...previousApiResponse, payload: newPage },
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
        queryClient.invalidateQueries({ queryKey: ["leaderboard"] });
      }
    },
  });
};

/**
 * Create a brand new announcement, invalidating the previous announcement.
 */
export const useCreateAnnouncementLeaderboardMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: createAnnouncement,
    onSuccess: async (data) => {
      if (data.success) {
        queryClient.invalidateQueries({ queryKey: ["announcement"] });
      }
    },
  });
};

async function createAnnouncement({
  message,
  expiresAt,
  showTimer,
}: {
  message: string;
  expiresAt: string;
  showTimer: boolean;
}) {
  const response = await fetch("/api/admin/announcement/create", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      message,
      expiresAt,
      showTimer,
    }),
  });

  return (await response.json()) as UnknownApiResponse<Announcement>;
}

/**
 * Disable the current announcement.
 */
export const useDeleteAnnouncementMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: deleteAnnouncement,
    onSuccess: async (data) => {
      if (data.success) {
        queryClient.invalidateQueries({
          queryKey: ["announcement"],
        });
      }
    },
  });
};

async function deleteAnnouncement({ id }: { id: string }) {
  const response = await fetch("/api/admin/announcement/disable", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      id,
    }),
  });

  return (await response.json()) as UnknownApiResponse<string>;
}
