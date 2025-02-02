import { BiCode, BiLogoGoLang } from "react-icons/bi";
import {
  FaCss3,
  FaHtml5,
  FaJava,
  FaNodeJs,
  FaPhp,
  FaPython,
  FaRust,
  FaSwift,
} from "react-icons/fa";
import {
  SiCplusplus,
  SiDart,
  SiElixir,
  SiHaskell,
  SiJavascript,
  SiKotlin,
  SiPerl,
  SiRuby,
  SiScala,
  SiTypescript,
} from "react-icons/si";

export const langNameToIcon = {
  java: FaJava,
  python3: FaPython,
  rust: FaRust,
  swift: FaSwift,
  php: FaPhp,
  html: FaHtml5,
  css: FaCss3,
  nodejs: FaNodeJs,
  typescript: SiTypescript,
  javascript: SiJavascript,
  cpp: SiCplusplus,
  golang: BiLogoGoLang,
  ruby: SiRuby,
  kotlin: SiKotlin,
  dart: SiDart,
  elixir: SiElixir,
  scala: SiScala,
  perl: SiPerl,
  haskell: SiHaskell,
  default: BiCode, // Default icon for unsupported languages
};

export type langNameKey = keyof typeof langNameToIcon;
