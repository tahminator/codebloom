import { Center, Skeleton, Table, Title } from "@mantine/core";

export default function MiniLeaderboardMobileSkeleton() {
  return (
    <>
      <Center>
        <Title
          style={{
            fontSize: "1rem",
            fontWeight: "bold",
            marginBottom: "1rem",
          }}
        >
          <Skeleton visible>Really long tVal name</Skeleton>
        </Title>
      </Center>
      <Center mb="md">
        <Skeleton visible width="100%" height="36px">
          <div style={{ width: "100%", height: "36px" }} />
        </Skeleton>
      </Center>
      <div
        className="flex flex-col sm:flex-row items-center sm:items-end justify-center gap-4"
        style={{ marginBottom: "2rem" }}
      >
        {Array(3)
          .fill(0)
          .map((_, index) => {
            const height = (() => {
              if (index === 0) return "210px";
              if (index === 1) return "185px";
              if (index === 2) return "170px";
            })();

            return (
              <Skeleton visible key={index} width={"300px"} height={height} />
            );
          })}
      </div>
      <Table>
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
                  <Skeleton visible width={"1rem"} height={"1rem"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"8rem"} height={"3rem"} />
                </Table.Td>
                <Table.Td>
                  <Skeleton visible width={"2rem"} height={"1rem"} />
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
    </>
  );
}
