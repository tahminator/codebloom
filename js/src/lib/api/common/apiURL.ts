import { operations, paths } from "@/lib/api/types/autogen/schema";

type PathsKey = keyof paths;
type PathsMethods<TKey extends PathsKey> = paths[TKey];
type PathsMethodKey<TKey extends PathsKey> = keyof PathsMethods<TKey>;
type PathMethodResult<
  TPathKey extends PathsKey,
  TPathMethodKey extends PathsMethodKey<TPathKey>,
> = PathsMethods<TPathKey>[TPathMethodKey];

type OperationsKey = keyof operations;
type OperationsPath<TKey extends OperationsKey> = operations[TKey];

type OperationFromPathMethodResult<TResult> = {
  [K in OperationsKey]: OperationsPath<K> extends TResult ? K : never;
}[OperationsKey];

type PathParamToOperations<
  TPathKey extends PathsKey,
  TMethod extends PathsMethodKey<TPathKey>,
> = OperationFromPathMethodResult<PathMethodResult<TPathKey, TMethod>>;

type PathOperationsToPathParams<TOperationsKey extends OperationsKey> =
  OperationsPath<TOperationsKey>["parameters"]["path"];

type PathOperationsToPathQueries<TOperationsKey extends OperationsKey> =
  OperationsPath<TOperationsKey>["parameters"]["query"];

export class ApiURL<
  TPathKey extends PathsKey,
  TPathMethod extends PathsMethodKey<TPathKey>,
> {
  private readonly _url: URL;

  public static create<
    const TPathKey extends PathsKey,
    const TPathMethod extends PathsMethodKey<TPathKey>,
  >(
    path: TPathKey,
    options: {
      method: TPathMethod;
      params?: PathOperationsToPathParams<
        PathParamToOperations<TPathKey, TPathMethod>
      >;
      query?: PathOperationsToPathQueries<
        PathParamToOperations<TPathKey, TPathMethod>
      >;
    },
  ): ApiURL<TPathKey, TPathMethod> {
    return new ApiURL(path, options);
  }

  private constructor(
    path: TPathKey,
    options?: {
      method: PathsMethodKey<TPathKey>;
      params?: PathOperationsToPathParams<
        PathParamToOperations<TPathKey, TPathMethod>
      >;
      query?: PathOperationsToPathQueries<
        PathParamToOperations<TPathKey, TPathMethod>
      >;
    },
  ) {
    const { params, query } = options ?? {};

    let resolved: string = path;
    if (params) {
      for (const [k, v] of Object.entries(params)) {
        resolved = resolved.replace(`{${k}}`, encodeURIComponent(String(v)));
      }

      if (/\{[^}]+\}/.test(resolved)) {
        throw new Error(`Missing path params for: ${path}`);
      }
    }

    console.log("hiiiii");
    let url: URL;
    console.log(resolved);
    try {
      url = new URL(resolved, window.location.origin);
    } catch (e) {
      console.log(e);
      throw e;
    }

    this._url = url;

    if (!query) {
      return;
    }

    for (const [k, v] of Object.entries(query)) {
      if (v != null) {
        url.searchParams.set(k, String(v));
      }
    }
  }

  /**
   * Return final URL state post-creation.
   */
  get url(): URL {
    return this._url;
  }

  toString(): string {
    return this._url.toString();
  }
}
