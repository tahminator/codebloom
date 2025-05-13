import { Prettify } from "@/lib/prettify";

type SuccessType<T> = Prettify<{
  success: true;
  message: string;
  payload: T;
}>;

type ErrorType = Prettify<{
  success: false;
  message: string;
}>;

type ApiResponse<T> = SuccessType<T> | ErrorType;
