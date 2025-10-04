import { QuestionTopicTopic } from "@/lib/api/types/autogen/schema";
import { ApiTypeUtils } from "@/lib/api/utils/types";

export const TOPIC_METADATA_LIST: Record<
  QuestionTopicTopic,
  ApiTypeUtils.QuestionTopicTopicMetadata
> = {
  [QuestionTopicTopic.STACK]: { enum: "STACK", name: "Stack" },
  [QuestionTopicTopic.DATA_STREAM]: {
    enum: "DATA_STREAM",
    name: "Data Stream",
  },
  [QuestionTopicTopic.REJECTION_SAMPLING]: {
    enum: "REJECTION_SAMPLING",
    name: "Rejection Sampling",
  },
  [QuestionTopicTopic.GEOMETRY]: { enum: "GEOMETRY", name: "Geometry" },
  [QuestionTopicTopic.COUNTING]: { enum: "COUNTING", name: "Counting" },
  [QuestionTopicTopic.DESIGN]: { enum: "DESIGN", name: "Design" },
  [QuestionTopicTopic.PROBABILITY_AND_STATISTICS]: {
    enum: "PROBABILITY_AND_STATISTICS",
    name: "Probability and Statistics",
  },
  [QuestionTopicTopic.MINIMUM_SPANNING_TREE]: {
    enum: "MINIMUM_SPANNING_TREE",
    name: "Minimum Spanning Tree",
  },
  [QuestionTopicTopic.LINE_SWEEP]: { enum: "LINE_SWEEP", name: "Line Sweep" },
  [QuestionTopicTopic.NUMBER_THEORY]: {
    enum: "NUMBER_THEORY",
    name: "Number Theory",
  },
  [QuestionTopicTopic.ROLLING_HASH]: {
    enum: "ROLLING_HASH",
    name: "Rolling Hash",
  },
  [QuestionTopicTopic.SEGMENT_TREE]: {
    enum: "SEGMENT_TREE",
    name: "Segment Tree",
  },
  [QuestionTopicTopic.BICONNECTED_COMPONENT]: {
    enum: "BICONNECTED_COMPONENT",
    name: "Biconnected Component",
  },
  [QuestionTopicTopic.MONOTONIC_STACK]: {
    enum: "MONOTONIC_STACK",
    name: "Monotonic Stack",
  },
  [QuestionTopicTopic.ITERATOR]: { enum: "ITERATOR", name: "Iterator" },
  [QuestionTopicTopic.QUEUE]: { enum: "QUEUE", name: "Queue" },
  [QuestionTopicTopic.RADIX_SORT]: { enum: "RADIX_SORT", name: "Radix Sort" },
  [QuestionTopicTopic.BUCKET_SORT]: {
    enum: "BUCKET_SORT",
    name: "Bucket Sort",
  },
  [QuestionTopicTopic.SHELL]: { enum: "SHELL", name: "Shell Sort" },
  [QuestionTopicTopic.MEMOIZATION]: {
    enum: "MEMOIZATION",
    name: "Memoization",
  },
  [QuestionTopicTopic.STRING]: { enum: "STRING", name: "String" },
  [QuestionTopicTopic.PREFIX_SUM]: { enum: "PREFIX_SUM", name: "Prefix Sum" },
  [QuestionTopicTopic.CONCURRENCY]: {
    enum: "CONCURRENCY",
    name: "Concurrency",
  },
  [QuestionTopicTopic.DATABASE]: { enum: "DATABASE", name: "Database" },
  [QuestionTopicTopic.SHORTEST_PATH]: {
    enum: "SHORTEST_PATH",
    name: "Shortest Path",
  },
  [QuestionTopicTopic.SORTING]: { enum: "SORTING", name: "Sorting" },
  [QuestionTopicTopic.LINKED_LIST]: {
    enum: "LINKED_LIST",
    name: "Linked List",
  },
  [QuestionTopicTopic.SLIDING_WINDOW]: {
    enum: "SLIDING_WINDOW",
    name: "Sliding Window",
  },
  [QuestionTopicTopic.SUFFIX_ARRAY]: {
    enum: "SUFFIX_ARRAY",
    name: "Suffix Array",
  },
  [QuestionTopicTopic.DOUBLY_LINKED_LIST]: {
    enum: "DOUBLY_LINKED_LIST",
    name: "Doubly Linked List",
  },
  [QuestionTopicTopic.SIMULATION]: { enum: "SIMULATION", name: "Simulation" },
  [QuestionTopicTopic.ORDERED_SET]: {
    enum: "ORDERED_SET",
    name: "Ordered Set",
  },
  [QuestionTopicTopic.GRAPH]: { enum: "GRAPH", name: "Graph" },
  [QuestionTopicTopic.MATH]: { enum: "MATH", name: "Math" },
  [QuestionTopicTopic.ORDERED_MAP]: {
    enum: "ORDERED_MAP",
    name: "Ordered Map",
  },
  [QuestionTopicTopic.GAME_THEORY]: {
    enum: "GAME_THEORY",
    name: "Game Theory",
  },
  [QuestionTopicTopic.DYNAMIC_PROGRAMMING]: {
    enum: "DYNAMIC_PROGRAMMING",
    name: "Dynamic Programming",
  },
  [QuestionTopicTopic.RECURSION]: { enum: "RECURSION", name: "Recursion" },
  [QuestionTopicTopic.MONOTONIC_QUEUE]: {
    enum: "MONOTONIC_QUEUE",
    name: "Monotonic Queue",
  },
  [QuestionTopicTopic.MATRIX]: { enum: "MATRIX", name: "Matrix" },
  [QuestionTopicTopic.RESERVOIR_SAMPLING]: {
    enum: "RESERVOIR_SAMPLING",
    name: "Reservoir Sampling",
  },
  [QuestionTopicTopic.MERGE_SORT]: { enum: "MERGE_SORT", name: "Merge Sort" },
  [QuestionTopicTopic.COMBINATORICS]: {
    enum: "COMBINATORICS",
    name: "Combinatorics",
  },
  [QuestionTopicTopic.INTERACTIVE]: {
    enum: "INTERACTIVE",
    name: "Interactive",
  },
  [QuestionTopicTopic.BINARY_TREE]: {
    enum: "BINARY_TREE",
    name: "Binary Tree",
  },
  [QuestionTopicTopic.RANDOMIZED]: { enum: "RANDOMIZED", name: "Randomized" },
  [QuestionTopicTopic.BITMASK]: { enum: "BITMASK", name: "Bitmask" },
  [QuestionTopicTopic.BREADTH_FIRST_SEARCH]: {
    enum: "BREADTH_FIRST_SEARCH",
    name: "Breadth-First Search",
  },
  [QuestionTopicTopic.STRING_MATCHING]: {
    enum: "STRING_MATCHING",
    name: "String Matching",
  },
  [QuestionTopicTopic.GREEDY]: { enum: "GREEDY", name: "Greedy" },
  [QuestionTopicTopic.BRAINTEASER]: {
    enum: "BRAINTEASER",
    name: "Brainteaser",
  },
  [QuestionTopicTopic.BACKTRACKING]: {
    enum: "BACKTRACKING",
    name: "Backtracking",
  },
  [QuestionTopicTopic.BIT_MANIPULATION]: {
    enum: "BIT_MANIPULATION",
    name: "Bit Manipulation",
  },
  [QuestionTopicTopic.UNION_FIND]: { enum: "UNION_FIND", name: "Union-Find" },
  [QuestionTopicTopic.BINARY_SEARCH_TREE]: {
    enum: "BINARY_SEARCH_TREE",
    name: "Binary Search Tree",
  },
  [QuestionTopicTopic.TWO_POINTERS]: {
    enum: "TWO_POINTERS",
    name: "Two Pointers",
  },
  [QuestionTopicTopic.ARRAY]: { enum: "ARRAY", name: "Array" },
  [QuestionTopicTopic.DEPTH_FIRST_SEARCH]: {
    enum: "DEPTH_FIRST_SEARCH",
    name: "Depth-First Search",
  },
  [QuestionTopicTopic.EULERIAN_CIRCUIT]: {
    enum: "EULERIAN_CIRCUIT",
    name: "Eulerian Circuit",
  },
  [QuestionTopicTopic.TREE]: { enum: "TREE", name: "Tree" },
  [QuestionTopicTopic.BINARY_SEARCH]: {
    enum: "BINARY_SEARCH",
    name: "Binary Search",
  },
  [QuestionTopicTopic.STRONGLY_CONNECTED_COMPONENT]: {
    enum: "STRONGLY_CONNECTED_COMPONENT",
    name: "Strongly Connected Component",
  },
  [QuestionTopicTopic.ENUMERATION]: {
    enum: "ENUMERATION",
    name: "Enumeration",
  },
  [QuestionTopicTopic.HEAP_PRIORITY_QUEUE]: {
    enum: "HEAP_PRIORITY_QUEUE",
    name: "Heap / Priority Queue",
  },
  [QuestionTopicTopic.DIVIDE_AND_CONQUER]: {
    enum: "DIVIDE_AND_CONQUER",
    name: "Divide and Conquer",
  },
  [QuestionTopicTopic.HASH_FUNCTION]: {
    enum: "HASH_FUNCTION",
    name: "Hash Function",
  },
  [QuestionTopicTopic.HASH_TABLE]: { enum: "HASH_TABLE", name: "Hash Table" },
  [QuestionTopicTopic.TRIE]: { enum: "TRIE", name: "Trie" },
  [QuestionTopicTopic.TOPOLOGICAL_SORT]: {
    enum: "TOPOLOGICAL_SORT",
    name: "Topological Sort",
  },
  [QuestionTopicTopic.QUICKSELECT]: {
    enum: "QUICKSELECT",
    name: "Quickselect",
  },
  [QuestionTopicTopic.BINARY_INDEXED_TREE]: {
    enum: "BINARY_INDEXED_TREE",
    name: "Binary Indexed Tree",
  },
  [QuestionTopicTopic.COUNTING_SORT]: {
    enum: "COUNTING_SORT",
    name: "Counting Sort",
  },
  [QuestionTopicTopic.UNKNOWN]: { enum: "UNKNOWN", name: "Unknown" },
};
