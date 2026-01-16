const CYAN = "\x1b[36m";
const RESET = "\x1b[0m";
const DIM = "\x1b[2m";

export function cyan(s: string) {
  return `${CYAN}${s}${RESET}`;
}

export function dim(s: string) {
  return `${DIM}${s}${RESET}`;
}
