import { Button, Flex } from "@mantine/core";

const MAX_VISIBLE_PAGES = 7;

const getPageNumbers = (currentPage: number, totalPages: number) => {
  if (totalPages <= MAX_VISIBLE_PAGES) {
    return [...Array(totalPages)].map((_, i) => i + 1);
  }

  const startPages = [1, 2];
  const endPages = [totalPages - 1, totalPages];

  if (currentPage <= 4) {
    return [...startPages, 3, 4, 5, "...", totalPages];
  }

  if (currentPage >= totalPages - 3) {
    return [
      1,
      "...",
      totalPages - 4,
      totalPages - 3,
      totalPages - 2,
      ...endPages,
    ];
  }

  return [
    1,
    "...",
    currentPage - 1,
    currentPage,
    currentPage + 1,
    "...",
    totalPages,
  ];
};

/**
 * Don't call this directly, unless you are trying to build your own pagination component.
 * Instead, use `Paginator`
 */
export default function CustomPagination({
  goTo,
  pages,
  currentPage,
}: {
  goTo: (pageNumber: number) => void;
  pages: number;
  currentPage: number;
}) {
  const pageNumbers = getPageNumbers(currentPage, pages);

  return (
    <Flex align={"center"} direction={"column"} gap={"8px"}>
      <Flex direction={"row"} gap={"4px"}>
        {pageNumbers.map((page, index) => (
          <Button
            size={"compact-sm"}
            disabled={typeof page === "string" || currentPage === page}
            color="yellow"
            key={index}
            onClick={() => goTo(typeof page === "string" ? 0 : page)}
          >
            {page}
          </Button>
        ))}
      </Flex>
    </Flex>
  );
}
