import { useDebouncedValue } from "@mantine/hooks";
import React, { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

type UseUrlStateOptions = {
  enabled?: boolean;
  resetOnDefault?: boolean;
  debounce?: number;
};

const defaultProps = {
  enabled: true,
  resetOnDefault: true,
  debounce: 0,
};

/**
 * A custom React hook that will attach the state to the URL params.
 *
 * @param name The name of the key in the URL
 * @param defaultValue The default value of the param if none is present in the URL. The type of this variable will also be used to determine if the string value from the URL should be coerced into something else.
 * @param enabled If you would like, you may disable tieing the values of this to a hook. This defaults to true.
 * @param resetOnDefault If the value equals to the default value (either on mount or if it returns back to the default value), clear the URL param. This defaults to true.
 * @param debounce If you would like to tie the URL state to an expensive operation, such as search queries. The default value is 0 (disabled)
 *
 * Returns a stateful value and a function to update it, as well as an optional third value with the debounced value.
 */
export function useURLState<T>(
  name: string,
  defaultValue: T,
  {
    enabled = defaultProps.enabled,
    resetOnDefault = defaultProps.resetOnDefault,
    debounce = defaultProps.debounce,
  }: UseUrlStateOptions = defaultProps,
) {
  const [initial, setInitial] = useState(true);
  const [value, setValue] = useState<T>(() => defaultValue);
  const [debouncedValue] = useDebouncedValue<T>(value, debounce);
  const [searchParams, setSearchParams] = useSearchParams();

  // On initial mount, update the state with the URL params. This hook will not run if the hook is not enabled or the initial value has already been provided.
  useEffect(() => {
    if (!enabled || !initial) {
      return;
    }

    const param = searchParams.get(name);

    // No value found in the URL.
    if (param == null) {
      setValue(defaultValue);
      setInitial(false);
      return;
    }

    const val = coerce(param, defaultValue);
    // If coercion of type fails, it will return Symbol.
    setValue(typeof val === "symbol" ? defaultValue : val);
    setInitial(false);
  }, [defaultValue, name, searchParams, enabled, initial]);

  // Update the URL with the new state, only if the initial value hasn't been set already and if the hook is enabled.
  useEffect(() => {
    if (!enabled || initial) {
      return;
    }

    if (debouncedValue != value) {
      return;
    }

    // If resetOnDefault is enabled, clear the key.
    if (debouncedValue === defaultValue && resetOnDefault) {
      setSearchParams(
        (prev) => {
          prev.delete(name);
          return prev;
        },
        { replace: true },
      );
      return;
    }

    setSearchParams(
      (prev) => {
        prev.set(name, String(debouncedValue));
        return prev;
      },
      { replace: true },
    );
  }, [
    debouncedValue,
    defaultValue,
    enabled,
    initial,
    name,
    resetOnDefault,
    setSearchParams,
    value,
  ]);

  return [value, setValue, debouncedValue] as [
    T,
    React.Dispatch<React.SetStateAction<T>>,
    T,
  ];
}

/**
 * This function allows us to coerce the URL param value string to the type of our default value. If coercion fails, a Symbol is returned so that the error case can be handled.
 */
function coerce<T>(param: string, defaultValue: T): T | symbol {
  try {
    // const paramType = typeof param;
    const defaultValueType = typeof defaultValue;

    // The default param value is always string, so no type coercion is needed.
    if (defaultValueType === "string") {
      return param as T;
    }

    if (defaultValueType === "number") {
      const possibleNumber = Number(param);
      // NaN will result in a parse failure, so it is not returned.
      if (Number.isNaN(possibleNumber)) {
        return Symbol();
      }
      return possibleNumber as T;
    }

    if (defaultValueType === "boolean") return (param === "true") as T;

    return Symbol();
  } catch (e) {
    console.log(e);
    return Symbol();
  }
}
