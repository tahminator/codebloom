import { ApiURL } from "@/lib/api/common/apiURL";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

export const useClubQuery = ({ clubSlug }: { clubSlug: string }) => {
  return useQuery({
    queryKey: ["club", clubSlug],
    queryFn: () => fetchClubDto({ clubSlug }),
  });
};

async function fetchClubDto({ clubSlug }: { clubSlug: string }) {
  const { url, method, res } = ApiURL.create("/api/club/{clubSlug}", {
    method: "GET",
    params: {
      clubSlug,
    },
  });
  const response = await fetch(url, {
    method,
  });

  return res(await response.json());
}

export const useVerifyPasswordMutation = (clubSlug: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: verifyPassword,
    onSettled: () => {
      queryClient.invalidateQueries({ queryKey: ["auth"] });
      queryClient.invalidateQueries({ queryKey: ["club", clubSlug] });
    },
  });
};

async function verifyPassword({
  userId,
  password,
  clubSlug,
}: {
  userId: string;
  password: string;
  clubSlug: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/club/verify", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
    body: req({
      userId,
      password,
      clubSlug,
    }),
  });

  const json = res(await response.json());

  return json;
}
