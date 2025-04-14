import { Center, Skeleton, Table, Title } from "@mantine/core";

/**
 * @todo - Could possibly scan the URL for page number and define different skeletons based off of that.
 */
export default function LeaderboardSkeleton() {
  return (
    <div style={{ padding: "1rem" }}>
      <Center>
        <Title
          order={3}
          style={{
            fontSize: "1rem",
            fontWeight: "bold",
            marginBottom: "1rem",
          }}
          className="text-center sm:text-lg"
        >
          <Skeleton visible>Really long tVal name</Skeleton>
        </Title>
      </Center>
      <div
        className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
        style={{ marginBottom: "2rem" }}
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
              <Skeleton visible key={index} width={"300px"} height={height} />
            );
          })}
      </div>
      <div
        style={{
          display: "flex",
          justifyContent: "flex-end",
          marginBottom: "1rem",
        }}
      >
        <Skeleton visible width={"100px"} height={"36px"}>
          <div style={{ width: "100%", height: "36px" }} />
        </Skeleton>
      </div>
      <Center mb="md">
        <Skeleton visible width="100%" height="36px">
          <div style={{ width: "100%", height: "36px" }} />
        </Skeleton>
      </Center>
      <Table horizontalSpacing="lg">
        <Table.Thead>
          <Table.Tr>
            <Table.Th>
              <Skeleton visible width={"1rem"} height={"0.75rem"} />
            </Table.Th>
            <Table.Th>
              <Skeleton visible width={"3rem"} height={"0.75rem"} />
            </Table.Th>
            <Table.Th>
              <Skeleton visible width={"2rem"} height={"0.75rem"} />
            </Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {Array(17)
            .fill(0)
            .map((_, index) => (
              <Table.Tr key={index}>
                <Table.Td>
                  <Skeleton visible width={"1rem"} height={"1rem"} pl={"lg"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"8rem"} height={"3rem"} pl={"lg"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"2rem"} height={"1rem"} mr={"xs"} />
                </Table.Td>
              </Table.Tr>
            ))}
        </Table.Tbody>
      </Table>
    </div>
  );
}
