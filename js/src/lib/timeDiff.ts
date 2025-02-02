/**
 * This is a simple utility function that takes a Date object and returns a string representing the time difference between the input time and the current time.
 * @param datetimeObject - The Date object to compare the current time with.
 * @returns A string representing the time difference between the current time and the time represented by the Date object.
 */

export function timeDiff(datetimeObject: Date): string {
  // Get the current time
  const timenow = new Date();

  // Calculate the difference in milliseconds
  const diff = timenow.getTime() - datetimeObject.getTime();

  // Define time intervals in milliseconds
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const year = 365 * day;

  // Check for different conditions
  if (diff < minute) {
    return "now";
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}m ago`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}h ago`;
  } else if (diff < week) {
    return `${Math.floor(diff / day)}d ago`;
  } else if (diff < year) {
    return `${Math.floor(diff / week)}w ago`;
  } else {
    return String(datetimeObject.getFullYear());
  }
}
