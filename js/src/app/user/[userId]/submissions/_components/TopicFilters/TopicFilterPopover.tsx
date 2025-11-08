import { QuestionTopicDtoTopic } from "@/lib/api/types/autogen/schema";
import { ApiUtils } from "@/lib/api/utils";
import { Button, Chip, Flex, Popover, Text, TextInput } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useMemo, useState } from "react";

type TopicFilterPopoverProps = {
  value: QuestionTopicDtoTopic[];
  onChange: (topics: QuestionTopicDtoTopic[]) => void;
  onClear: () => void;
};

const leetcodeTopics = ApiUtils.getAllTopicEntries();

export default function TopicFilterPopover({
  value,
  onChange,
  onClear,
}: TopicFilterPopoverProps) {
  const [opened, { toggle }] = useDisclosure(false);
  const [search, setSearch] = useState("");

  const filteredTopics = useMemo(
    () =>
      Object.entries(leetcodeTopics).filter(([key, topic]) => {
        if (value.includes(key as QuestionTopicDtoTopic)) {
          return true;
        }

        return ApiUtils.matchTopic(topic, search);
      }),
    [value, search],
  );

  return (
    <Popover
      opened={opened}
      position="bottom-start"
      closeOnEscape
      withinPortal={false}
      shadow="md"
      transitionProps={{ keepMounted: false }}
    >
      <Popover.Target>
        <Button fullWidth variant="light" color="gray" onClick={toggle}>
          Topics
        </Button>
      </Popover.Target>
      <Popover.Dropdown w={400}>
        <TextInput
          placeholder="Search Topic"
          value={search}
          onChange={(e) => setSearch(e.currentTarget.value)}
          mb="sm"
        />
        <Chip.Group
          multiple
          value={value}
          onChange={(e) => {
            onChange(e as QuestionTopicDtoTopic[]);
          }}
        >
          <Flex
            wrap="wrap"
            gap="sm"
            maw={400}
            mah={300}
            style={{
              overflowY: "auto",
            }}
          >
            {filteredTopics.length > 0 ?
              filteredTopics.map(([key, topic]) => (
                <Chip
                  key={key}
                  value={key}
                  radius="xl"
                  styles={{
                    label: {
                      fontSize: "12px",
                    },
                  }}
                >
                  {topic.name}
                </Chip>
              ))
            : <Text c={"gray"} size={"sm"}>
                No matching topics
              </Text>
            }
          </Flex>
        </Chip.Group>
        <Flex mt="sm" align="center">
          <Button variant={"filled"} color="red" w={"100%"} onClick={onClear}>
            Reset
          </Button>
        </Flex>
      </Popover.Dropdown>
    </Popover>
  );
}
