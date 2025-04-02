import { useCallback, useEffect, useState } from "react";

/**
 * A custom React hook that counts a number down to 0.
 *
 * @param start - The initial starting value (in seconds). The default is 60 seconds.
 * @param interval - How frequently to count down (in milliseconds). The default value is 1000, so it counts down every second.
 *
 * Returns a stateful value and a function to reset the countdown to a new value.
 */
export default function useCountdown(start = 60, interval = 1000) {
  const [countdown, setCountdown] = useState(start);

  useEffect(() => {
    let timeoutId: number;
    if (countdown > 0) {
      timeoutId = setTimeout(() => {
        setCountdown((prev) => prev - Math.floor(interval) / 1000);
      }, interval);
    }
    return () => clearTimeout(timeoutId);
  }, [countdown, interval]);

  const reset = useCallback(
    (newValue: number) => {
      setCountdown(Math.floor(newValue));
    },
    [setCountdown],
  );

  return [countdown, reset] as [number, (newValue: number) => void];
}
