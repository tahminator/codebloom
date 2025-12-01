import {
  fetchEventSource as originalFetch,
  FetchEventSourceInit,
} from "@microsoft/fetch-event-source";

/**
 * A wrapper around {@link https://www.npmjs.com/package/@microsoft/fetch-event-source @microsoft/fetch-event-source}
 * that accepts a URL object.
 */
export async function fetchEventSource(
  input: URL | RequestInfo,
  init: FetchEventSourceInit,
): Promise<void> {
  const url = input instanceof URL ? input.toString() : input;

  return originalFetch(url, init);
}
