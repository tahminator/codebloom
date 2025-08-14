import LruCache from "@/lib/reporting/LruCache";
import { useEffect, useRef, useCallback, ReactNode, Component } from "react";

import { getMappedStackTrace } from "./sourcemap";

export default function ErrorReporter({ children }: { children?: ReactNode }) {
  const errors = useRef(new LruCache(50));
  const flushTimeout = useRef<number | null>(null);
  const needsFlush = useRef(false);

  const fireAndForgetReport = useCallback(() => {
    if (!errors.current.size()) return;

    fetch("/api/reporting", {
      method: "POST",
      body: JSON.stringify({ traces: errors.current.values() }),
      headers: { "Content-Type": "application/json" },
    });

    errors.current.clear();
  }, []);

  const scheduleReport = useCallback(() => {
    if (flushTimeout.current) {
      needsFlush.current = true;
      return;
    }

    flushTimeout.current = window.setTimeout(() => {
      fireAndForgetReport();
      flushTimeout.current = null;

      if (needsFlush.current) {
        needsFlush.current = false;
        scheduleReport();
      }
    }, 2000);
  }, [fireAndForgetReport]);

  const addError = useCallback(
    (key: string) => {
      if (!errors.current.has(key)) {
        errors.current.add(key);
        scheduleReport();
      }
    },
    [scheduleReport],
  );

  useEffect(() => {
    globalThis.onerror = (message, _, __, ___, error) => {
      console.log("window.onerror running");
      if (!error) {
        console.warn(
          "Error object is missing when attempting to report error from window.onerror",
        );
        return false;
      }
      getMappedStackTrace(error).then(addError);
      return false;
    };

    window.onunhandledrejection = ({ reason }) => {
      console.log("window.onunhandledrejection running");
      if (reason instanceof Error) {
        console.log("reason object is instanceof Error");
        getMappedStackTrace(reason).then(addError);
      } else {
        console.warn("reason object is not instanceof Error");
        const key = reason?.stack ?? JSON.stringify(reason);
        addError(key);
      }
      return false;
    };

    return () => {
      window.onerror = null;
      window.onunhandledrejection = null;
      if (flushTimeout.current) clearTimeout(flushTimeout.current);
    };
  }, [addError]);

  return <ErrorBoundary onError={addError}>{children}</ErrorBoundary>;
}

class ErrorBoundary extends Component<{
  children?: ReactNode;
  onError: (key: string) => void;
}> {
  componentDidCatch(error: Error) {
    getMappedStackTrace(error).then(this.props.onError);
  }

  render() {
    return this.props.children;
  }
}
