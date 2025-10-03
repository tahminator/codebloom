import { useVerifySchoolMutation } from "@/lib/api/queries/auth/school";
import { Box, Button, Checkbox, Modal, Stack } from "@mantine/core";
import { useState } from "react";

type TopicModalProps = {
  enabled: boolean;
  toggle: () => void;
};

const leetcodeTopics = [
  "stack",
  "data-stream",
  "rejection-sampling",
  "geometry",
  "counting",
  "design",
  "probability-and-statistics",
  "minimum-spanning-tree",
  "line-sweep",
  "number-theory",
  "rolling-hash",
  "segment-tree",
  "biconnected-component",
  "monotonic-stack",
  "iterator",
  "queue",
  "radix-sort",
  "bucket-sort",
  "shell",
  "memoization",
  "string",
  "prefix-sum",
  "concurrency",
  "database",
  "shortest-path",
  "sorting",
  "linked-list",
  "sliding-window",
  "suffix-array",
  "doubly-linked-list",
  "simulation",
  "ordered-set",
  "graph",
  "math",
  "ordered-map",
  "game-theory",
  "dynamic-programming",
  "recursion",
  "monotonic-queue",
  "matrix",
  "reservoir-sampling",
  "merge-sort",
  "combinatorics",
  "interactive",
  "binary-tree",
  "randomized",
  "bitmask",
  "breadth-first-search",
  "string-matching",
  "greedy",
  "brainteaser",
  "backtracking",
  "bit-manipulation",
  "union-find",
  "binary-search-tree",
  "two-pointers",
  "array",
  "depth-first-search",
  "eulerian-circuit",
  "tree",
  "binary-search",
  "strongly-connected-component",
  "enumeration",
  "heap-priority-queue",
  "divide-and-conquer",
  "hash-function",
  "hash-table",
  "trie",
  "topological-sort",
  "quickselect",
  "binary-indexed-tree",
  "counting-sort",
  "unknown",
];

export default function TopicFilterModal({ enabled, toggle }: TopicModalProps) {
  const { mutate, status } = useVerifySchoolMutation(); // will change when i get backend merged
  const [selected, setSelected] = useState<string[]>([]);

  return (
    <Modal opened={enabled} onClose={toggle} size="lg" title="Select Topics">
      <Box p="md">
        <Checkbox.Group
          value={selected}
          onChange={setSelected}
          label="Choose one or more topics"
        >
          <Stack gap="xs">
            {leetcodeTopics.map((topic) => (
              <Checkbox key={topic} value={topic} label={topic} />
            ))}
          </Stack>
        </Checkbox.Group>
        <Button
          mt="lg"
          onClick={() => {
            console.log("Selected topics:", selected);
            toggle();
          }}
        >
          Save
        </Button>
      </Box>
    </Modal>
  );
}
