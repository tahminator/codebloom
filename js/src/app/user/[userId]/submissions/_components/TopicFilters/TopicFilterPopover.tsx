import { ApiUtils } from "@/lib/api/utils";
import { Button, Chip, Flex, Popover, TextInput } from "@mantine/core";
import { useMemo, useState } from "react";

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

  const leetcodeTopics = ApiUtils.getAllTopicEntries();

  const filteredTopics = useMemo(
    () =>
      Object.fromEntries(
        Object.entries(leetcodeTopics).filter(([_, topic]) =>
          topic.name.toLowerCase().includes(search.toLowerCase()),
        ),
      ),
    [search, leetcodeTopics],
  );

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
          placeholder="Search Topic"
          value={search}
          onChange={(e) => setSearch(e.currentTarget.value)}
          mb="sm"
        />
        <Chip.Group multiple value={value} onChange={onChange}>
          <Flex
            wrap="wrap"
            gap="sm"
            maw={400}
            style={{
              maxHeight: 300,
              overflowY: "auto",
            }}
          >
            {Object.entries(filteredTopics).map(([key, topic]) => (
              <Chip
                key={topic.name}
                value={key}
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
        <Flex mt="sm" align="center">
          <Button
            variant="outline"
            color="white"
            w={"100%"}
            onClick={() => {
              onChange([]);
            }}
          >
            Reset
          </Button>
        </Flex>
      </Popover.Dropdown>
    </Popover>
  );
}
