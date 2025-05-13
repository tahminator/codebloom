type SuccessType<T> = {
  success: true;
  message: string;
  payload: T;
};

type ErrorType = {
  success: false;
  message: string;
};

export type ApiResponse<T> = SuccessType<T> | ErrorType;
