import { QuestionTopicTopic } from "@/lib/api/types/autogen/schema";
import { ApiTypeUtils } from "@/lib/api/utils/types";

export const TOPIC_METADATA_LIST: Record<
  QuestionTopicTopic,
  ApiTypeUtils.QuestionTopicTopicMetadata
> = {
  STACK: { enum: "STACK", name: "Stack" },
  DATA_STREAM: {
    enum: "DATA_STREAM",
    name: "Data Stream",
  },
  REJECTION_SAMPLING: {
    enum: "REJECTION_SAMPLING",
    name: "Rejection Sampling",
  },
  GEOMETRY: { enum: "GEOMETRY", name: "Geometry" },
  COUNTING: { enum: "COUNTING", name: "Counting" },
  DESIGN: { enum: "DESIGN", name: "Design" },
  PROBABILITY_AND_STATISTICS: {
    enum: "PROBABILITY_AND_STATISTICS",
    name: "Probability and Statistics",
  },
  MINIMUM_SPANNING_TREE: {
    enum: "MINIMUM_SPANNING_TREE",
    name: "Minimum Spanning Tree",
  },
  LINE_SWEEP: { enum: "LINE_SWEEP", name: "Line Sweep" },
  NUMBER_THEORY: {
    enum: "NUMBER_THEORY",
    name: "Number Theory",
  },
  ROLLING_HASH: {
    enum: "ROLLING_HASH",
    name: "Rolling Hash",
  },
  SEGMENT_TREE: {
    enum: "SEGMENT_TREE",
    name: "Segment Tree",
  },
  BICONNECTED_COMPONENT: {
    enum: "BICONNECTED_COMPONENT",
    name: "Biconnected Component",
  },
  MONOTONIC_STACK: {
    enum: "MONOTONIC_STACK",
    name: "Monotonic Stack",
  },
  ITERATOR: { enum: "ITERATOR", name: "Iterator" },
  QUEUE: { enum: "QUEUE", name: "Queue" },
  RADIX_SORT: { enum: "RADIX_SORT", name: "Radix Sort" },
  BUCKET_SORT: {
    enum: "BUCKET_SORT",
    name: "Bucket Sort",
  },
  SHELL: { enum: "SHELL", name: "Shell Sort" },
  MEMOIZATION: {
    enum: "MEMOIZATION",
    name: "Memoization",
  },
  STRING: { enum: "STRING", name: "String" },
  PREFIX_SUM: { enum: "PREFIX_SUM", name: "Prefix Sum" },
  CONCURRENCY: {
    enum: "CONCURRENCY",
    name: "Concurrency",
  },
  DATABASE: { enum: "DATABASE", name: "Database" },
  SHORTEST_PATH: {
    enum: "SHORTEST_PATH",
    name: "Shortest Path",
  },
  SORTING: { enum: "SORTING", name: "Sorting" },
  LINKED_LIST: {
    enum: "LINKED_LIST",
    name: "Linked List",
  },
  SLIDING_WINDOW: {
    enum: "SLIDING_WINDOW",
    name: "Sliding Window",
  },
  SUFFIX_ARRAY: {
    enum: "SUFFIX_ARRAY",
    name: "Suffix Array",
  },
  DOUBLY_LINKED_LIST: {
    enum: "DOUBLY_LINKED_LIST",
    name: "Doubly Linked List",
  },
  SIMULATION: { enum: "SIMULATION", name: "Simulation" },
  ORDERED_SET: {
    enum: "ORDERED_SET",
    name: "Ordered Set",
  },
  GRAPH: { enum: "GRAPH", name: "Graph" },
  MATH: { enum: "MATH", name: "Math" },
  ORDERED_MAP: {
    enum: "ORDERED_MAP",
    name: "Ordered Map",
  },
  GAME_THEORY: {
    enum: "GAME_THEORY",
    name: "Game Theory",
  },
  DYNAMIC_PROGRAMMING: {
    enum: "DYNAMIC_PROGRAMMING",
    name: "Dynamic Programming",
  },
  RECURSION: { enum: "RECURSION", name: "Recursion" },
  MONOTONIC_QUEUE: {
    enum: "MONOTONIC_QUEUE",
    name: "Monotonic Queue",
  },
  MATRIX: { enum: "MATRIX", name: "Matrix" },
  RESERVOIR_SAMPLING: {
    enum: "RESERVOIR_SAMPLING",
    name: "Reservoir Sampling",
  },
  MERGE_SORT: { enum: "MERGE_SORT", name: "Merge Sort" },
  COMBINATORICS: {
    enum: "COMBINATORICS",
    name: "Combinatorics",
  },
  INTERACTIVE: {
    enum: "INTERACTIVE",
    name: "Interactive",
  },
  BINARY_TREE: {
    enum: "BINARY_TREE",
    name: "Binary Tree",
  },
  RANDOMIZED: { enum: "RANDOMIZED", name: "Randomized" },
  BITMASK: { enum: "BITMASK", name: "Bitmask" },
  BREADTH_FIRST_SEARCH: {
    enum: "BREADTH_FIRST_SEARCH",
    name: "Breadth-First Search",
  },
  STRING_MATCHING: {
    enum: "STRING_MATCHING",
    name: "String Matching",
  },
  GREEDY: { enum: "GREEDY", name: "Greedy" },
  BRAINTEASER: {
    enum: "BRAINTEASER",
    name: "Brainteaser",
  },
  BACKTRACKING: {
    enum: "BACKTRACKING",
    name: "Backtracking",
  },
  BIT_MANIPULATION: {
    enum: "BIT_MANIPULATION",
    name: "Bit Manipulation",
  },
  UNION_FIND: { enum: "UNION_FIND", name: "Union-Find" },
  BINARY_SEARCH_TREE: {
    enum: "BINARY_SEARCH_TREE",
    name: "Binary Search Tree",
  },
  TWO_POINTERS: {
    enum: "TWO_POINTERS",
    name: "Two Pointers",
  },
  ARRAY: { enum: "ARRAY", name: "Array" },
  DEPTH_FIRST_SEARCH: {
    enum: "DEPTH_FIRST_SEARCH",
    name: "Depth-First Search",
  },
  EULERIAN_CIRCUIT: {
    enum: "EULERIAN_CIRCUIT",
    name: "Eulerian Circuit",
  },
  TREE: { enum: "TREE", name: "Tree" },
  BINARY_SEARCH: {
    enum: "BINARY_SEARCH",
    name: "Binary Search",
  },
  STRONGLY_CONNECTED_COMPONENT: {
    enum: "STRONGLY_CONNECTED_COMPONENT",
    name: "Strongly Connected Component",
  },
  ENUMERATION: {
    enum: "ENUMERATION",
    name: "Enumeration",
  },
  HEAP_PRIORITY_QUEUE: {
    enum: "HEAP_PRIORITY_QUEUE",
    name: "Heap / Priority Queue",
  },
  DIVIDE_AND_CONQUER: {
    enum: "DIVIDE_AND_CONQUER",
    name: "Divide and Conquer",
  },
  HASH_FUNCTION: {
    enum: "HASH_FUNCTION",
    name: "Hash Function",
  },
  HASH_TABLE: { enum: "HASH_TABLE", name: "Hash Table" },
  TRIE: { enum: "TRIE", name: "Trie" },
  TOPOLOGICAL_SORT: {
    enum: "TOPOLOGICAL_SORT",
    name: "Topological Sort",
  },
  QUICKSELECT: {
    enum: "QUICKSELECT",
    name: "Quickselect",
  },
  BINARY_INDEXED_TREE: {
    enum: "BINARY_INDEXED_TREE",
    name: "Binary Indexed Tree",
  },
  COUNTING_SORT: {
    enum: "COUNTING_SORT",
    name: "Counting Sort",
  },
  UNKNOWN: { enum: "UNKNOWN", name: "Unknown" },
};
