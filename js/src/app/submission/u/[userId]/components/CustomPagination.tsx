import { Button, Flex, TextInput } from "@mantine/core";
import { useState } from "react";

const MAX_VISIBLE_PAGES = 5;

const getPageNumbers = (currentPage: number, totalPages: number) => {
  // If there are less pages than the maximum, just render the numbers regularly.
  if (totalPages <= MAX_VISIBLE_PAGES) {
    return [...Array(totalPages)].map((_, i) => i + 1);
  }

  // If start of the list, render from the left.
  if (currentPage <= MAX_VISIBLE_PAGES - 2) {
    return [1, 2, 3, 4, totalPages];
  }

  // If end of the list, render from the right.
  if (currentPage >= MAX_VISIBLE_PAGES + 2) {
    return [1, totalPages - 3, totalPages - 2, totalPages - 1, totalPages];
  }

  return [1, currentPage - 1, currentPage, currentPage + 1, totalPages];
};

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
  const [customPage, setCustomPage] = useState("");

  const handleCustomPageSubmit = () => {
    const pageNum = Math.max(1, Math.min(pages, Number(customPage))); // Clamp value
    if (!isNaN(pageNum)) {
      goTo(pageNum);
      setCustomPage(""); // Reset input after navigation
    }
  };

  return (
    <Flex align={"center"} direction={"column"} gap={"8px"}>
      <Flex direction={"row"} gap={"4px"}>
        {pageNumbers.map((page, index) => (
          <Button
            key={index}
            onClick={() => goTo(page)}
            fw={currentPage === page ? "bold" : "normal"}
            bg={currentPage === page ? "gray" : "white"}
          >
            {page}
          </Button>
        ))}
      </Flex>
      <TextInput
        value={customPage}
        onChange={(e) => setCustomPage(e.target.value)}
        onKeyDown={(e) => e.key === "Enter" && handleCustomPageSubmit()}
        placeholder="Enter page here..."
        ta={"center"}
      />
    </Flex>
  );
}
