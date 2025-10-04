type KeyToTupleObj<T> = { [K in keyof T]: [K, T[K]] };
type Tuple<T> = KeyToTupleObj<T>[keyof T];

declare global {
  interface ObjectConstructor {
    /**
     * Generic implementation of {@link Object.entries} that will maintain the actual
     * type value of each key and the value.
     */
    typedEntries<T extends Record<PropertyKey, unknown>>(
      obj: T,
    ): Array<Tuple<T>>;

    /**
     * Generic implementation of {@link Object.keys} that will maintain the actual
     * type value of each key.
     */
    typedKeys<T extends Record<PropertyKey, unknown>>(obj: T): Array<keyof T>;
  }
}

Object.typedEntries = function <T extends Record<PropertyKey, unknown>>(
  obj: T,
): Array<Tuple<T>> {
  return Object.entries(obj) as Array<Tuple<T>>;
};

Object.typedKeys = function <T extends Record<PropertyKey, unknown>>(
  obj: T,
): Array<keyof T> {
  return Object.keys(obj) as Array<keyof T>;
};
