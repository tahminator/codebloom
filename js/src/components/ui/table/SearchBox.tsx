import { TextInput, TextInputProps } from "@mantine/core";
import { ChangeEventHandler } from "react";

/**
 * A controlled TextInput that can be tied to React state.
 */
export default function SearchBox({
  query,
  onChange,
  placeholder,
  smallPadding,
  ...props
}: {
  query: string;
  onChange: ChangeEventHandler<HTMLInputElement>;
  smallPadding?: boolean;
  placeholder: string;
} & TextInputProps) {
  return (
    <TextInput
      value={query}
      placeholder={placeholder}
      pt={smallPadding ? undefined : "md"}
      onChange={onChange}
      maw={"100%"}
      miw={"66%"}
      {...props}
    />
  );
}
