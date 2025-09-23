type ReportObject = {
  err: (stackTrace: string) => void;
  log: (info: string) => void;
};

/**
 * Custom error reporter that will feed ingest logs & errors to the backend.
 *
 * @returns {ReportObject} A report object containing:
 * - `err` (function): Report an error using a stack trace (string).
 * - `log` (function): Report a log using some info (string).
 */
export function useReporter(): ReportObject {
  return { err, log };
}

const err: ReportObject["err"] = async (trace: string) => {
  fetch("/api/reporter/error", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      trace,
    }),
  });
};

const log: ReportObject["log"] = async (info: string) => {
  fetch("/api/reporter/log", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      info,
    }),
  });
};

// TODO - Figure out how to get source mapped / non minified info.
