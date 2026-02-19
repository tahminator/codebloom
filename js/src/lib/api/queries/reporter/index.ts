import { ApiURL } from "@/lib/api/common/apiURL";
import { useMutation } from "@tanstack/react-query";

export const useSubmitFeedbackMutation = () => {
  return useMutation({
    mutationFn: submitFeedback,
  });
};

async function submitFeedback({
  title,
  description,
  email,
}: {
  title: string;
  description: string;
  email: string;
}) {
  const { url, method, req, res } = ApiURL.create("/api/reporter/feedback", {
    method: "POST",
  });
  const response = await fetch(url, {
    method,
    body: req({
      title,
      description,
      email,
    }),
  });

  return res(await response.json());
}
