export type Page<T> = {
  hasNextPage: boolean;
  data: T;
  pages: number;
  pageSize: number;
};
