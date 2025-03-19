import { useSearchParams } from "react-router-dom";

export function useAuthCallbackInfo() {
  const [searchParams] = useSearchParams();

  const success = (() => {
    const val = searchParams.get("success");
    console.log(val);

    if (val === "true") {
      return true;
    }

    if (val === "false") {
      return false;
    }

    return null;
  })();

  const message = searchParams.get("message");

  return {
    success: success,
    message: message,
  };
}
