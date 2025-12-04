package com.patina.codebloom.common.db.models.question.topic;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeetcodeTopicEnum {
    STACK("stack"),
    DATA_STREAM("data-stream"),
    REJECTION_SAMPLING("rejection-sampling"),
    GEOMETRY("geometry"),
    COUNTING("counting"),
    DESIGN("design"),
    PROBABILITY_AND_STATISTICS("probability-and-statistics"),
    MINIMUM_SPANNING_TREE("minimum-spanning-tree"),
    LINE_SWEEP("line-sweep"),
    NUMBER_THEORY("number-theory"),
    ROLLING_HASH("rolling-hash"),
    SEGMENT_TREE("segment-tree"),
    BICONNECTED_COMPONENT("biconnected-component"),
    MONOTONIC_STACK("monotonic-stack"),
    ITERATOR("iterator"),
    QUEUE("queue"),
    RADIX_SORT("radix-sort"),
    BUCKET_SORT("bucket-sort"),
    SHELL("shell"),
    MEMOIZATION("memoization"),
    STRING("string"),
    PREFIX_SUM("prefix-sum"),
    CONCURRENCY("concurrency"),
    DATABASE("database"),
    SHORTEST_PATH("shortest-path"),
    SORTING("sorting"),
    LINKED_LIST("linked-list"),
    SLIDING_WINDOW("sliding-window"),
    SUFFIX_ARRAY("suffix-array"),
    DOUBLY_LINKED_LIST("doubly-linked-list"),
    SIMULATION("simulation"),
    ORDERED_SET("ordered-set"),
    GRAPH("graph"),
    MATH("math"),
    ORDERED_MAP("ordered-map"),
    GAME_THEORY("game-theory"),
    DYNAMIC_PROGRAMMING("dynamic-programming"),
    RECURSION("recursion"),
    MONOTONIC_QUEUE("monotonic-queue"),
    MATRIX("matrix"),
    RESERVOIR_SAMPLING("reservoir-sampling"),
    MERGE_SORT("merge-sort"),
    COMBINATORICS("combinatorics"),
    INTERACTIVE("interactive"),
    BINARY_TREE("binary-tree"),
    RANDOMIZED("randomized"),
    BITMASK("bitmask"),
    BREADTH_FIRST_SEARCH("breadth-first-search"),
    STRING_MATCHING("string-matching"),
    GREEDY("greedy"),
    BRAINTEASER("brainteaser"),
    BACKTRACKING("backtracking"),
    BIT_MANIPULATION("bit-manipulation"),
    UNION_FIND("union-find"),
    BINARY_SEARCH_TREE("binary-search-tree"),
    TWO_POINTERS("two-pointers"),
    ARRAY("array"),
    DEPTH_FIRST_SEARCH("depth-first-search"),
    EULERIAN_CIRCUIT("eulerian-circuit"),
    TREE("tree"),
    BINARY_SEARCH("binary-search"),
    STRONGLY_CONNECTED_COMPONENT("strongly-connected-component"),
    ENUMERATION("enumeration"),
    HEAP_PRIORITY_QUEUE("heap-priority-queue"),
    DIVIDE_AND_CONQUER("divide-and-conquer"),
    HASH_FUNCTION("hash-function"),
    HASH_TABLE("hash-table"),
    TRIE("trie"),
    TOPOLOGICAL_SORT("topological-sort"),
    QUICKSELECT("quickselect"),
    BINARY_INDEXED_TREE("binary-indexed-tree"),
    COUNTING_SORT("counting-sort"),
    UNKNOWN("unknown");

    private final String leetcodeEnum;

    /**
     * Converts a string value to the corresponding LeetcodeTopicEnum.
     *
     * <p>This method performs a case-sensitive search through all enum values to find a match with the provided string
     * value. The comparison is done against the internal leetcode.com topic identifier format.
     *
     * @param value the string representation of the leetcode topic (e.g., "array", "dynamic-programming")
     * @return the matching LeetcodeTopicEnum, or {@link #UNKNOWN} if no match is found
     * @see #getLeetcodeEnum()
     */
    public static LeetcodeTopicEnum fromValue(final String value) {
        return Arrays.stream(LeetcodeTopicEnum.values())
                .filter(topic -> topic.getLeetcodeEnum().equals(value))
                .findFirst()
                .orElse(LeetcodeTopicEnum.UNKNOWN);
    }
}
