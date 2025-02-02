import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

/**
 * This function allows us to coerce the URL param value string to the type of our default value.
 */
function coerce<T>(param: string, defaultValue: T): T | symbol {
  const paramType = typeof param;
  const defaultValueType = typeof defaultValue;

  if (defaultValueType === "string") {
    return paramType as T;
  }

  if (paramType != defaultValueType) {
    if (defaultValueType === "number") return Number(param) as T;
    if (defaultValueType === "boolean") return (param === "true") as T;
  }

  return Symbol();
}

/**
 * A custom React hook that will attach the state to the URL params.
 *
 * @param name The name of the key in the URL
 * @param defaultValue The default value of the param if none is present in the URL. The type of this variable will also be used to determine if the string value from the URL should be coerced into something else.
 * @param tieToUrl If you would like, you may disable tieing the values of this to a hook. This defaults to true.
 *
 * Returns a stateful value and a function to update it.
 */
export function useURLState<T>(
  name: string,
  defaultValue: T,
  tieToUrl = true,
  resetOnDefault = true
) {
  const [initial, setInitial] = useState(false);
  const [value, setValue] = useState<T>(defaultValue);
  const [searchParams, setSearchParams] = useSearchParams();

  // On initial mount, update the state with the URL params.
  useEffect(() => {
    if (tieToUrl) {
      const param = searchParams.get(name);

      if (param == null) {
        setValue(defaultValue);
      } else {
        const val = coerce(param, defaultValue);
        // If coercion of type fails, it will return Symbol.
        setValue(typeof val === "symbol" ? defaultValue : val);
      }

      setInitial(true);
    }
  }, [defaultValue, name, searchParams, tieToUrl]);

  // Update the URL with the new state, only if the initial value hasn't been set already.
  useEffect(() => {
    if (tieToUrl && initial) {
      if (value === defaultValue && resetOnDefault) {
        setSearchParams((prev) => {
          prev.delete(name);
          return prev;
        });
      } else {
        setSearchParams((prev) => {
          prev.set(name, String(value));
          return prev;
        });
      }
    }
  }, [
    defaultValue,
    initial,
    name,
    resetOnDefault,
    setSearchParams,
    tieToUrl,
    value,
  ]);

  return [value, setValue] as [T, React.Dispatch<React.SetStateAction<T>>];
}
