import { Box, Card, Center, Flex, Skeleton, Table } from "@mantine/core";

export default function MiniLeaderboardSkeleton() {
  return (
    <Box pos={"relative"} p={"xs"}>
      <Center mb="md">
        <Skeleton visible width="100%" height="36px">
          <div style={{ width: "100%", height: "36px" }} />
        </Skeleton>
      </Center>
      <Flex
        direction={{ base: "column", xs: "row" }}
        align={{ base: "center", xs: "flex-end" }}
        justify="center"
        gap="md"
        mb="xl"
      >
        {Array(3)
          .fill(0)
          .map((_, index) => {
            const height = (() => {
              if (index === 1) return "210px";
              if (index === 0) return "185px";
              if (index === 2) return "170px";
            })();

            return (
              <Skeleton visible key={index}>
                <Card
                  withBorder
                  shadow="sm"
                  radius="md"
                  className={`border-2 flex flex-col items-center justify-center`}
                  style={{
                    height,
                    width: "200px",
                  }}
                />
              </Skeleton>
            );
          })}
      </Flex>
      <Table horizontalSpacing="lg">
        <Table.Thead>
          <Table.Tr>
            <Table.Th></Table.Th>
            <Table.Th>
              <Skeleton visible width={"3rem"} height={"0.75rem"} />
            </Table.Th>
            <Table.Th>
              <Skeleton visible width={"2rem"} height={"0.75rem"} />
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {Array(2)
            .fill(0)
            .map((_, index) => (
              <Table.Tr key={index}>
                <Table.Td>
                  <Skeleton visible width={"1rem"} height={"1rem"} mx={"xs"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"8rem"} height={"3rem"} mr={"xs"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"2rem"} height={"1rem"} mr={"xs"} />
                </Table.Td>
              </Table.Tr>
            ))}
        </Table.Tbody>
      </Table>
      <Center mb="md" mt={"md"}>
        <Skeleton visible width="100%" height="36px">
          <div style={{ width: "100%", height: "36px" }} />
        </Skeleton>
      </Center>
    </Box>
  );
}
