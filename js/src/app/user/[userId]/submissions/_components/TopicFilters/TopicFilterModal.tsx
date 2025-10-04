import { ApiUtils } from "@/lib/api/utils";
import { Box, Button, Chip, Flex, Popover, TextInput } from "@mantine/core";
import { useEffect, useMemo, useState } from "react";

type TopicFilterPopoverProps = {
  value: string[];
  onChange: (topics: string[]) => void;
};

export default function TopicFilterPopover({
  value,
  onChange,
}: TopicFilterPopoverProps) {
  const [opened, setOpened] = useState(false);
  const [search, setSearch] = useState("");
  const [local, setLocal] = useState<string[]>(value);

  const leetcodeTopics = ApiUtils.getAllTopicEnumMetadata();

  const filteredTopics = useMemo(
    () =>
      leetcodeTopics.filter((topic) =>
        topic.name.toLowerCase().includes(search.toLowerCase()),
      ),
    [search, leetcodeTopics],
  );

  useEffect(() => {
    setLocal(value);
  }, [value]);

  return (
    <Popover
      opened={opened}
      onChange={setOpened}
      position="bottom-start"
      trapFocus={false}
      closeOnEscape
      withinPortal={false}
      shadow="md"
      transitionProps={{ keepMounted: false }}
    >
      <Popover.Target>
        <Button
          fullWidth
          variant="outline"
          color="gray"
          onClick={() => setOpened((o) => !o)}
        >
          Topics
        </Button>
      </Popover.Target>
      <Popover.Dropdown style={{ width: 400 }}>
        <TextInput
          placeholder="search"
          value={search}
          onChange={(e) => setSearch(e.currentTarget.value)}
          mb="sm"
        />
        <Chip.Group multiple value={local} onChange={setLocal}>
          <Flex
            wrap="wrap"
            gap="sm"
            maw={400}
            style={{
              maxHeight: 300,
              overflowY: "auto",
            }}
          >
            {filteredTopics.map((topic) => (
              <Chip
                key={topic.enum}
                value={topic.enum}
                radius="xl"
                styles={(theme, { checked }) => ({
                  label: {
                    color: checked ? theme.white : "#b3b3b3",
                    fontWeight: 600,
                  },
                })}
              >
                {topic.name}
              </Chip>
            ))}
          </Flex>
        </Chip.Group>
        <Box
          mt="sm"
          style={{ display: "flex", justifyContent: "space-between" }}
        >
          <Button
            variant="subtle"
            color="gray"
            onClick={() => {
              setLocal([]);
              onChange([]);
            }}
          >
            Reset
          </Button>
          <Button
            onClick={() => {
              onChange(local);
              setOpened(false);
            }}
          >
            Save
          </Button>
        </Box>
      </Popover.Dropdown>
    </Popover>
  );
}
