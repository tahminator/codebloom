import ErrorStackParser from "error-stack-parser";
import StackTraceGPS from "stacktrace-gps";

/**
 * Convert minified error traces into a full stack trace using
 * source maps.
 */
export async function getMappedStackTrace(error: Error): Promise<string> {
  const rawFrames = ErrorStackParser.parse(error);
  const gps = new StackTraceGPS();

  const mappedFrames = await Promise.all(
    rawFrames.map(async (frame) => {
      try {
        return await gps.getMappedLocation(frame);
      } catch {
        return frame;
      }
    }),
  );

  return mappedFrames
    .map((frame) => {
      const fn = frame.functionName || "<anonymous>";
      const file = frame.fileName?.replace(location.origin, "") ?? "<unknown>";
      const line = frame.lineNumber ?? "?";
      const col = frame.columnNumber ?? "?";
      return `    at ${fn} (${file}:${line}:${col})`;
    })
    .join("\n");
}
