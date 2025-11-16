import { useDebouncedValue } from "@mantine/hooks";
import React, { useEffect, useState, useRef } from "react";
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
  const [searchParams, setSearchParams] = useSearchParams();
  const mountedRef = useRef(false);
  const lastUpdateTimeRef = useRef<number>(0);
  const updateCooldownMs = 50;

  const [value, setValue] = useState<T>(() => {
    if (!enabled) {
      return defaultValue;
    }

    const param = searchParams.get(name);

    if (param == null) {
      return defaultValue;
    }

    const val = coerce(param, defaultValue);
    const result = typeof val === "symbol" ? defaultValue : val;
    return result;
  });

  const [debouncedValue] = useDebouncedValue<T>(value, debounce);

  useEffect(() => {
    if (!enabled || !mountedRef.current) {
      return;
    }

    const timeSinceLastUpdate = Date.now() - lastUpdateTimeRef.current;
    if (timeSinceLastUpdate < updateCooldownMs) {
      return;
    }

    const param = searchParams.get(name);
    const urlValue =
      param == null ? defaultValue : (
        (() => {
          const val = coerce(param, defaultValue);
          return typeof val === "symbol" ? defaultValue : val;
        })()
      );

    const urlValueStr = String(urlValue);
    const currentValueStr = String(value);

    if (urlValueStr !== currentValueStr) {
      setValue(urlValue);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [searchParams, name, enabled, defaultValue]);

  useEffect(() => {
    if (!enabled) {
      return;
    }

    if (!mountedRef.current) {
      mountedRef.current = true;
      return;
    }

    if (debounce > 0 && debouncedValue !== value) {
      return;
    }

    lastUpdateTimeRef.current = Date.now();

    if (resetOnDefault && String(debouncedValue) === String(defaultValue)) {
      setSearchParams(
        (prev) => {
          if (prev.has(name)) {
            prev.delete(name);
          }
          return prev;
        },
        { replace: true },
      );
      return;
    }

    setSearchParams(
      (prev) => {
        const currentValue = prev.get(name);
        const newValue = String(debouncedValue);

        if (currentValue !== newValue) {
          prev.set(name, newValue);
        }
        return prev;
      },
      { replace: true },
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [
    debouncedValue,
    defaultValue,
    enabled,
    name,
    resetOnDefault,
    setSearchParams,
    debounce,
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
