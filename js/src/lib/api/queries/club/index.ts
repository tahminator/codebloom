import { useMutation, useQuery } from "@tanstack/react-query";

import { UnknownApiResponse } from "../../common/apiResponse";
import { ClubDto } from "../../types/club";
import { UserTag } from "../../types/usertag";

export const useClubQuery = ({ clubSlug }: { clubSlug?: string }) => {
  return useQuery({
    queryKey: ["club", clubSlug],
    queryFn: () => fetchClubDto({ clubSlug }),
  });
};

async function fetchClubDto({ clubSlug }: { clubSlug?: string }) {
  const response = await fetch(`/api/club/${clubSlug}`);

  const json = (await response.json()) as UnknownApiResponse<ClubDto>;

  return json;
}

export const useVerifyPasswordMutation = () => {
  return useMutation({
    mutationFn: verifyPassword,
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
  const response = await fetch("/api/club/verify", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      userId,
      password,
      clubSlug,
    }),
  });

  const json = (await response.json()) as UnknownApiResponse<UserTag>;

  return json;
}
