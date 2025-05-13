export type Page<T> = {
  hasNextPage: boolean;
  items: T;
  pages: number;
  pageSize: number;
};
