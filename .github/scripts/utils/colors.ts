export function black(s: string) {
  return `${BLACK}${s}${RESET}`;
}

export function red(s: string) {
  return `${RED}${s}${RESET}`;
}

export function green(s: string) {
  return `${GREEN}${s}${RESET}`;
}

export function yellow(s: string) {
  return `${YELLOW}${s}${RESET}`;
}

export function blue(s: string) {
  return `${BLUE}${s}${RESET}`;
}

export function magenta(s: string) {
  return `${MAGENTA}${s}${RESET}`;
}

export function cyan(s: string) {
  return `${CYAN}${s}${RESET}`;
}

export function white(s: string) {
  return `${WHITE}${s}${RESET}`;
}

export function gray(s: string) {
  return `${GRAY}${s}${RESET}`;
}

export function brightRed(s: string) {
  return `${BRIGHT_RED}${s}${RESET}`;
}

export function brightGreen(s: string) {
  return `${BRIGHT_GREEN}${s}${RESET}`;
}

export function brightYellow(s: string) {
  return `${BRIGHT_YELLOW}${s}${RESET}`;
}

export function brightBlue(s: string) {
  return `${BRIGHT_BLUE}${s}${RESET}`;
}

export function brightMagenta(s: string) {
  return `${BRIGHT_MAGENTA}${s}${RESET}`;
}

export function brightCyan(s: string) {
  return `${BRIGHT_CYAN}${s}${RESET}`;
}

export function brightWhite(s: string) {
  return `${BRIGHT_WHITE}${s}${RESET}`;
}

export function pink(s: string) {
  return `${PINK}${s}${RESET}`;
}

export function orange(s: string) {
  return `${ORANGE}${s}${RESET}`;
}

export function bold(s: string) {
  return `${BOLD}${s}${RESET}`;
}

export function dim(s: string) {
  return `${DIM}${s}${RESET}`;
}

export function italic(s: string) {
  return `${ITALIC}${s}${RESET}`;
}

export function underline(s: string) {
  return `${UNDERLINE}${s}${RESET}`;
}

const BLACK = "\x1b[30m";
const RED = "\x1b[31m";
const GREEN = "\x1b[32m";
const YELLOW = "\x1b[33m";
const BLUE = "\x1b[34m";
const MAGENTA = "\x1b[35m";
const CYAN = "\x1b[36m";
const WHITE = "\x1b[37m";
const GRAY = "\x1b[90m";

const BRIGHT_RED = "\x1b[91m";
const BRIGHT_GREEN = "\x1b[92m";
const BRIGHT_YELLOW = "\x1b[93m";
const BRIGHT_BLUE = "\x1b[94m";
const BRIGHT_MAGENTA = "\x1b[95m";
const BRIGHT_CYAN = "\x1b[96m";
const BRIGHT_WHITE = "\x1b[97m";

const PINK = "\x1b[38;5;213m";
const ORANGE = "\x1b[38;5;214m";

const RESET = "\x1b[0m";
const BOLD = "\x1b[1m";
const DIM = "\x1b[2m";
const ITALIC = "\x1b[3m";
const UNDERLINE = "\x1b[4m";
