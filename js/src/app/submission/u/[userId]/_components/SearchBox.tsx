import { TextInput } from "@mantine/core";
import { Dispatch, SetStateAction } from "react";

export default function SearchBox({
  query,
  setQuery,
}: {
  query: string;
  setQuery: Dispatch<SetStateAction<string>>;
}) {
  return (
    <TextInput
      value={query}
      placeholder={"Search for submission title"}
      pt={"md"}
      onChange={(event) => setQuery(event.currentTarget.value)}
      maw={"100%"}
      miw={"66%"}
    />
  );
}
