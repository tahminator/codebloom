type SuccessType<T> = {
  success: true;
  message: string;
  data: T;
};

type ErrorType = {
  success: false;
  message: string;
};

export type ApiResponse<T> = SuccessType<T> | ErrorType;
