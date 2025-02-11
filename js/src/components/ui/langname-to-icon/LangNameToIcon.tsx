import CppColored from "@/components/ui/icons/CppColored";
import JavaColored from "@/components/ui/icons/JavaColored";
import JavascriptColored from "@/components/ui/icons/JavascriptColored";
import PythonColored from "@/components/ui/icons/PythonColored";
import RustColored from "@/components/ui/icons/RustColored";
import TypescriptColored from "@/components/ui/icons/TypescriptColored";
import { BiCode, BiLogoGoLang } from "react-icons/bi";
import { FaPhp, FaSwift } from "react-icons/fa";
import {
  SiDart,
  SiElixir,
  SiHaskell,
  SiKotlin,
  SiPerl,
  SiRuby,
  SiScala,
} from "react-icons/si";

export const langNameToIcon = {
  java: JavaColored,
  python3: PythonColored,
  rust: RustColored,
  swift: FaSwift,
  php: FaPhp,
  typescript: TypescriptColored,
  javascript: JavascriptColored,
  cpp: CppColored,
  golang: BiLogoGoLang,
  ruby: SiRuby,
  kotlin: SiKotlin,
  dart: SiDart,
  elixir: SiElixir,
  scala: SiScala,
  perl: SiPerl,
  haskell: SiHaskell,
  default: BiCode, // Default icon for unsupported languages
} as const;

export type langNameKey = keyof typeof langNameToIcon;
