import { TextInput } from "@mantine/core";
import { ChangeEventHandler } from "react";

/**
 * A controlled TextInput that can be tied to React state.
 */
export default function SearchBox({
  query,
  onChange,
  placeholder,
}: {
  query: string;
  onChange: ChangeEventHandler<HTMLInputElement>;
  placeholder: string;
}) {
  return (
    <TextInput
      value={query}
      placeholder={placeholder}
      pt={"md"}
      onChange={onChange}
      maw={"100%"}
      miw={"66%"}
    />
  );
}
