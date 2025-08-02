export type Page<T> = {
  hasNextPage: boolean;
  items: T;
  pages: number;
  pageSize: number;
};

export type Indexed<T extends object> = {
  index: number;
} & T;
