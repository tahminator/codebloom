import { ApiURL } from "@/lib/api/common/apiURL";
import { http, HttpResponse } from "msw";

const submitFeedback = ApiURL.create("/api/reporter/feedback", {
  method: "POST",
});

export const submitFeedbackSuccessHandler = http.post(
  submitFeedback.url.toString(),
  () => {
    return HttpResponse.json({
      success: true,
      payload: {},
      message: "Feedback submitted successfully!",
    } satisfies ReturnType<typeof submitFeedback.res>);
  },
);

export const submitFeedbackFailureHandler = http.post(
  submitFeedback.url.toString(),
  () => {
    return HttpResponse.json({
      success: false,
      payload: undefined,
      message: "Description must be at least 10 characters.",
    } satisfies ReturnType<typeof submitFeedback.res>);
  },
);

export const submitFeedbackErrorHandler = http.post(
  submitFeedback.url.toString(),
  () => {
    return HttpResponse.error();
  },
);
