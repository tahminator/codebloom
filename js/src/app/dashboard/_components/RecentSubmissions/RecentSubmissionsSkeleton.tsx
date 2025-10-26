import { Card, Flex, Title, Skeleton, Text } from "@mantine/core";
import { FaDiscord } from "react-icons/fa";
import { SiLeetcode } from "react-icons/si";

export default function RecentSubmissionsSkeleton() {
  return (
    <Card withBorder padding={"md"} radius={"md"} miw={"31vw"} mih={"63vh"}>
      <Flex direction={"row"} justify={"space-between"} w={"100%"}>
        <Title order={4}>
          <Skeleton>Really long tVal name</Skeleton>
        </Title>
        <Skeleton w={"5.55rem"} h={"2.25rem"} />
      </Flex>
      <Flex direction={"column"} gap={"md"} m={"xs"}>
        {Array(5)
          .fill(0)
          .map((_, idx) => {
            return (
              <Skeleton key={idx}>
                <Flex
                  direction={"row"}
                  justify={"space-between"}
                  style={{
                    borderRadius: "4px",
                    padding: "var(--mantine-spacing-xs)",
                  }}
                  p={"xs"}
                >
                  <Text>{idx + 1}.</Text>
                  <Flex direction={"column"}>
                    <Text ta="center">
                      <FaDiscord
                        style={{
                          display: "inline",
                          marginLeft: "4px",
                          marginRight: "4px",
                        }}
                      />
                      tVal name
                    </Text>
                    <Text ta="center">
                      <SiLeetcode
                        style={{
                          display: "inline",
                          marginLeft: "4px",
                          marginRight: "4px",
                        }}
                      />
                      tVal name
                    </Text>
                  </Flex>
                  <Text>tVal score</Text>
                </Flex>
              </Skeleton>
            );
          })}
      </Flex>
    </Card>
  );
}
