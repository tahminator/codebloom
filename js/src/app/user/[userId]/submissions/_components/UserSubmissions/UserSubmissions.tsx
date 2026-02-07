import DateRangePopover from "@/app/user/[userId]/submissions/_components/DateRangePopover/DateRangePopover";
import TopicFilterPopover from "@/app/user/[userId]/submissions/_components/TopicFilters/TopicFilterPopover";
import UserSubmissionsSkeleton from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissionsSkeleton";
import FilterDropdown from "@/components/ui/dropdown/FilterDropdown";
import FilterDropdownItem from "@/components/ui/dropdown/FilterDropdownItem";
import {
  langNameKey,
  langNameToIcon,
} from "@/components/ui/langname-to-icon/LangNameToIcon";
import Paginator from "@/components/ui/table/Paginator";
import SearchBox from "@/components/ui/table/SearchBox";
import Toast from "@/components/ui/toast/Toast";
import { useUserSubmissionsQuery } from "@/lib/api/queries/user";
import { ApiUtils } from "@/lib/api/utils";
import { timeDiff } from "@/lib/timeDiff";
import {
  Badge,
  Box,
  Overlay,
  Text,
  Stack,
  Group,
  Card,
  Flex,
} from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";
import { useMemo } from "react";
import { Link } from "react-router-dom";

export default function UserSubmissions({ userId }: { userId: string }) {
  const {
    data,
    status,
    page,
    goBack,
    goForward,
    isPlaceholderData,
    goTo,
    searchQuery,
    setSearchQuery,
    pointFilter,
    togglePointFilter,
    topics,
    setTopics,
    clearTopics,
    startDate,
    endDate,
    setStartDate,
    setEndDate,
  } = useUserSubmissionsQuery({
    userId,
    tieToUrl: true,
    pageSize: 15,
  });

  const selectedTopicsSet = useMemo(() => new Set(topics), [topics]);

  const isMobile = useMediaQuery("(max-width: 768px)");

  if (status === "pending") {
    return (
      <>
        <UserSubmissionsSkeleton />
      </>
    );
  }

  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong when trying to fetch user's submissions. Please try again later." />
    );
  }

  // const pageData = data.payload;
  const pageData = {
    "hasNextPage": true,
    "items": [
      {
        "id": "c3e9329f-2253-45e3-a391-9de6c5ad121f",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "two-sum",
        "questionTitle": "Two Sum",
        "questionDifficulty": "Easy",
        "questionNumber": 1,
        "questionLink": "https://leetcode.com/problems/two-sum",
        "description": "<p>Given an array of integers <code>nums</code>&nbsp;and an integer <code>target</code>, return <em>indices of the two numbers such that they add up to <code>target</code></em>.</p>\n\n<p>You may assume that each input would have <strong><em>exactly</em> one solution</strong>, and you may not use the <em>same</em> element twice.</p>\n\n<p>You can return the answer in any order.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [2,7,11,15], target = 9\n<strong>Output:</strong> [0,1]\n<strong>Explanation:</strong> Because nums[0] + nums[1] == 9, we return [0, 1].\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [3,2,4], target = 6\n<strong>Output:</strong> [1,2]\n</pre>\n\n<p><strong class=\"example\">Example 3:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [3,3], target = 6\n<strong>Output:</strong> [0,1]\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>2 &lt;= nums.length &lt;= 10<sup>4</sup></code></li>\n\t<li><code>-10<sup>9</sup> &lt;= nums[i] &lt;= 10<sup>9</sup></code></li>\n\t<li><code>-10<sup>9</sup> &lt;= target &lt;= 10<sup>9</sup></code></li>\n\t<li><strong>Only one valid answer exists.</strong></li>\n</ul>\n\n<p>&nbsp;</p>\n<strong>Follow-up:&nbsp;</strong>Can you come up with an algorithm that is less than <code>O(n<sup>2</sup>)</code><font face=\"monospace\">&nbsp;</font>time complexity?",
        "pointsAwarded": 0,
        "acceptanceRate": 0.568,
        "createdAt": "2026-01-02T04:06:56.518811",
        "submittedAt": "2026-01-02T03:48:21",
        "runtime": "0 ms",
        "memory": "18.8 MB",
        "code": "class Solution:\n    def twoSum(self, nums: List[int], target: int) -> List[int]:\n        D = {}\n\n        for i in range(len(nums)):\n            if not(nums[i] in D):\n                D[target - nums[i]] = i\n            else:\n                return [D[nums[i]], i]",
        "language": "python3",
        "submissionId": "1871830166",
        "topics": [
          {
            "id": "c244b891-a70a-46a4-a963-13b1bedc4e5b",
            "questionId": "c3e9329f-2253-45e3-a391-9de6c5ad121f",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2026-01-02T04:06:56.541231"
          },
          {
            "id": "49eca606-b2f4-4f90-9d1a-1853551fab47",
            "questionId": "c3e9329f-2253-45e3-a391-9de6c5ad121f",
            "topicSlug": "hash-table",
            "topic": "HASH_TABLE",
            "createdAt": "2026-01-02T04:06:56.544226"
          }
        ]
      },
      {
        "id": "fa10d4b3-f4b6-429b-bce6-ad5d7b428849",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "count-negative-numbers-in-a-sorted-matrix",
        "questionTitle": "Count Negative Numbers in a Sorted Matrix",
        "questionDifficulty": "Easy",
        "questionNumber": 1476,
        "questionLink": "https://leetcode.com/problems/count-negative-numbers-in-a-sorted-matrix",
        "description": "<p>Given a <code>m x n</code> matrix <code>grid</code> which is sorted in non-increasing order both row-wise and column-wise, return <em>the number of <strong>negative</strong> numbers in</em> <code>grid</code>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n\n<pre>\n<strong>Input:</strong> grid = [[4,3,2,-1],[3,2,1,-1],[1,1,-1,-2],[-1,-1,-2,-3]]\n<strong>Output:</strong> 8\n<strong>Explanation:</strong> There are 8 negatives number in the matrix.\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> grid = [[3,2],[1,0]]\n<strong>Output:</strong> 0\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>m == grid.length</code></li>\n\t<li><code>n == grid[i].length</code></li>\n\t<li><code>1 &lt;= m, n &lt;= 100</code></li>\n\t<li><code>-100 &lt;= grid[i][j] &lt;= 100</code></li>\n</ul>\n\n<p>&nbsp;</p>\n<strong>Follow up:</strong> Could you find an <code>O(n + m)</code> solution?",
        "pointsAwarded": 97,
        "acceptanceRate": 0.77900004,
        "createdAt": "2025-12-27T20:23:31.843196",
        "submittedAt": "2025-12-27T20:22:26",
        "runtime": "0 ms",
        "memory": "13.9 MB",
        "code": "class Solution {\npublic:\n  /**\n  * [\n  *   [4, 3, 2, -1],\n  *   [3, 2, 1, -1],\n  *   [1, 1, -1, -2],\n  *   [-1, -1, -2, -3]\n  * ]\n  *\n  * each given row and given column is sorted\n  * row l->r: desc\n  * col u->d: desc\n  *\n  * the pattern is interesting u can see that it almost splits into a triangle of sorts where all the negatives end up closer to the bottom right \n  * while positives end up closer to the top left.\n  *\n  * [            *\n  *   [4, 3, 2, -1],\n  *   [3, 2, 1, -1],\n  *   [1, 1, -1, -2],\n  *   [-1, -1, -2, -3]\n  * ]\n  *\n  * -1 is negative, which means it and all below it are negative. \n  * the coord for -1 is [0, 3]. if we want to include everything below it,\n  * [1, 3], [2, 3], [3, 3].\n  *\n  * [      *\n  *   [1, -1]\n  *   [-1, -1]\n  * ]\n  *\n  * top right is [0, 1]. All below is negative including it so that means [1, 1] is negative too.\n  * next iter starts at [0, 0] which is 1. \n  */\n  int countNegatives(std::vector<std::vector<int>>& grid) {\n    const int n = grid.size(), m = grid[0].size();\n\n    int count = 0;\n\n    int r = 0, c = m - 1;\n    while (r < n && c >= 0) {\n      // everything below it is negative, including it is negative.\n      if (grid[r][c] < 0) {\n        count += n - r;\n        c--; // move over\n      } else { // keep looking for negative in the given col.\n        r++;\n      }\n    }\n\n    return count;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1867061490",
        "topics": [
          {
            "id": "7b0d992e-ed85-4b8c-8e9c-7a7d2759cecc",
            "questionId": "fa10d4b3-f4b6-429b-bce6-ad5d7b428849",
            "topicSlug": "matrix",
            "topic": "MATRIX",
            "createdAt": "2025-12-27T20:23:31.878182"
          },
          {
            "id": "891e9ed7-f2a6-4915-bd46-e1023c16c4a2",
            "questionId": "fa10d4b3-f4b6-429b-bce6-ad5d7b428849",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-27T20:23:31.871597"
          },
          {
            "id": "a7e8a715-1d34-4b4b-850d-a706ea5968ed",
            "questionId": "fa10d4b3-f4b6-429b-bce6-ad5d7b428849",
            "topicSlug": "binary-search",
            "topic": "BINARY_SEARCH",
            "createdAt": "2025-12-27T20:23:31.875147"
          }
        ]
      },
      {
        "id": "516e3256-2625-485c-9c29-b3ea60e78b41",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "number-of-ways-to-arrive-at-destination",
        "questionTitle": "Number of Ways to Arrive at Destination",
        "questionDifficulty": "Medium",
        "questionNumber": 2090,
        "questionLink": "https://leetcode.com/problems/number-of-ways-to-arrive-at-destination",
        "description": "<p>You are in a city that consists of <code>n</code> intersections numbered from <code>0</code> to <code>n - 1</code> with <strong>bi-directional</strong> roads between some intersections. The inputs are generated such that you can reach any intersection from any other intersection and that there is at most one road between any two intersections.</p>\n\n<p>You are given an integer <code>n</code> and a 2D integer array <code>roads</code> where <code>roads[i] = [u<sub>i</sub>, v<sub>i</sub>, time<sub>i</sub>]</code> means that there is a road between intersections <code>u<sub>i</sub></code> and <code>v<sub>i</sub></code> that takes <code>time<sub>i</sub></code> minutes to travel. You want to know in how many ways you can travel from intersection <code>0</code> to intersection <code>n - 1</code> in the <strong>shortest amount of time</strong>.</p>\n\n<p>Return <em>the <strong>number of ways</strong> you can arrive at your destination in the <strong>shortest amount of time</strong></em>. Since the answer may be large, return it <strong>modulo</strong> <code>10<sup>9</sup> + 7</code>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2025/02/14/1976_corrected.png\" style=\"width: 255px; height: 400px;\" />\n<pre>\n<strong>Input:</strong> n = 7, roads = [[0,6,7],[0,1,2],[1,2,3],[1,3,3],[6,3,3],[3,5,1],[6,5,1],[2,5,1],[0,4,5],[4,6,2]]\n<strong>Output:</strong> 4\n<strong>Explanation:</strong> The shortest amount of time it takes to go from intersection 0 to intersection 6 is 7 minutes.\nThe four ways to get there in 7 minutes are:\n- 0 ➝ 6\n- 0 ➝ 4 ➝ 6\n- 0 ➝ 1 ➝ 2 ➝ 5 ➝ 6\n- 0 ➝ 1 ➝ 3 ➝ 5 ➝ 6\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> n = 2, roads = [[1,0,10]]\n<strong>Output:</strong> 1\n<strong>Explanation:</strong> There is only one way to go from intersection 0 to intersection 1, and it takes 10 minutes.\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= n &lt;= 200</code></li>\n\t<li><code>n - 1 &lt;= roads.length &lt;= n * (n - 1) / 2</code></li>\n\t<li><code>roads[i].length == 3</code></li>\n\t<li><code>0 &lt;= u<sub>i</sub>, v<sub>i</sub> &lt;= n - 1</code></li>\n\t<li><code>1 &lt;= time<sub>i</sub> &lt;= 10<sup>9</sup></code></li>\n\t<li><code>u<sub>i </sub>!= v<sub>i</sub></code></li>\n\t<li>There is at most one road connecting any two intersections.</li>\n\t<li>You can reach any intersection from any other intersection.</li>\n</ul>\n",
        "pointsAwarded": 234,
        "acceptanceRate": 0.372,
        "createdAt": "2025-12-22T23:43:29.929552",
        "submittedAt": "2025-12-22T23:43:16",
        "runtime": "6 ms",
        "memory": "35.2 MB",
        "code": "#define ll long long\n\nclass Solution {\npublic:\n  int countPaths(int N, std::vector<std::vector<int>>& roads) {\n    std::unordered_map<int, std::vector<std::pair<int, int>>> adjList;\n\n    for (auto& road : roads) {\n      int u = road[0], v = road[1], w = road[2];\n      adjList[u].push_back({v, w});\n      adjList[v].push_back({u, w}); // undirected, do both\n    }\n\n    std::vector<ll> dst(N, LLONG_MAX);\n\n    // this is the secret sauce right here. we can calculate the different paths depending on \n    // the distance of what we r currently looking at.\n    std::vector<int> ways(N);\n\n    std::priority_queue<\n      std::pair<ll, int>,\n      std::vector<std::pair<ll, int>>,\n      std::greater<std::pair<ll, int>>\n    > q;\n\n    q.emplace(0, 0);\n    dst[0] = 0;\n    ways[0] = 1; // start, nowhere to go.\n\n    while (!q.empty()) {\n      auto [d, v] = q.top();\n      q.pop();\n\n      // not a shortest path\n      if (d > dst[v]) continue;\n\n      for (auto [n, t] : adjList[v]) {\n        if (dst[v] + t < dst[n]) {\n          dst[n] = dst[v] + t;\n          ways[n] = ways[v];\n          q.emplace(dst[n], n);\n        } else if (dst[v] + t == dst[n]) {\n          ways[n] = (ways[n] + ways[v]) % static_cast<int>(1e9 + 7);\n        }\n      }\n    }\n\n    return ways[N - 1];\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1862952906",
        "topics": [
          {
            "id": "2a419e86-86fb-407e-8efa-ead924f395bd",
            "questionId": "516e3256-2625-485c-9c29-b3ea60e78b41",
            "topicSlug": "shortest-path",
            "topic": "SHORTEST_PATH",
            "createdAt": "2025-12-22T23:43:29.979594"
          },
          {
            "id": "342bff76-7aa4-4788-8fdd-9835f8a42f78",
            "questionId": "516e3256-2625-485c-9c29-b3ea60e78b41",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-22T23:43:29.975101"
          },
          {
            "id": "c832f06e-943c-41e2-8fea-024993474e19",
            "questionId": "516e3256-2625-485c-9c29-b3ea60e78b41",
            "topicSlug": "dynamic-programming",
            "topic": "DYNAMIC_PROGRAMMING",
            "createdAt": "2025-12-22T23:43:29.972072"
          },
          {
            "id": "6d117150-4107-406e-8833-de1645c091c9",
            "questionId": "516e3256-2625-485c-9c29-b3ea60e78b41",
            "topicSlug": "topological-sort",
            "topic": "TOPOLOGICAL_SORT",
            "createdAt": "2025-12-22T23:43:29.977337"
          }
        ]
      },
      {
        "id": "252c86e3-ac63-4e56-82ec-b504e94e7f4c",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "redundant-connection-ii",
        "questionTitle": "Redundant Connection II",
        "questionDifficulty": "Hard",
        "questionNumber": 685,
        "questionLink": "https://leetcode.com/problems/redundant-connection-ii",
        "description": "<p>In this problem, a rooted tree is a <b>directed</b> graph such that, there is exactly one node (the root) for which all other nodes are descendants of this node, plus every node has exactly one parent, except for the root node which has no parents.</p>\n\n<p>The given input is a directed graph that started as a rooted tree with <code>n</code> nodes (with distinct values from <code>1</code> to <code>n</code>), with one additional directed edge added. The added edge has two different vertices chosen from <code>1</code> to <code>n</code>, and was not an edge that already existed.</p>\n\n<p>The resulting graph is given as a 2D-array of <code>edges</code>. Each element of <code>edges</code> is a pair <code>[u<sub>i</sub>, v<sub>i</sub>]</code> that represents a <b>directed</b> edge connecting nodes <code>u<sub>i</sub></code> and <code>v<sub>i</sub></code>, where <code>u<sub>i</sub></code> is a parent of child <code>v<sub>i</sub></code>.</p>\n\n<p>Return <em>an edge that can be removed so that the resulting graph is a rooted tree of</em> <code>n</code> <em>nodes</em>. If there are multiple answers, return the answer that occurs last in the given 2D-array.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/12/20/graph1.jpg\" style=\"width: 222px; height: 222px;\" />\n<pre>\n<strong>Input:</strong> edges = [[1,2],[1,3],[2,3]]\n<strong>Output:</strong> [2,3]\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/12/20/graph2.jpg\" style=\"width: 222px; height: 382px;\" />\n<pre>\n<strong>Input:</strong> edges = [[1,2],[2,3],[3,4],[4,1],[1,5]]\n<strong>Output:</strong> [4,1]\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>n == edges.length</code></li>\n\t<li><code>3 &lt;= n &lt;= 1000</code></li>\n\t<li><code>edges[i].length == 2</code></li>\n\t<li><code>1 &lt;= u<sub>i</sub>, v<sub>i</sub> &lt;= n</code></li>\n\t<li><code>u<sub>i</sub> != v<sub>i</sub></code></li>\n</ul>\n",
        "pointsAwarded": 442,
        "acceptanceRate": 0.35799998,
        "createdAt": "2025-12-22T22:52:07.853628",
        "submittedAt": "2025-12-22T22:51:41",
        "runtime": "1 ms",
        "memory": "13.4 MB",
        "code": "class DisjointSet {\n  std::vector<int> s;\n\npublic:\n  DisjointSet(int n) : s(std::vector<int>(n, -1)) {}\n\n  int find(int i) {\n    if (s[i] < 0) {\n      return i;\n    }\n\n    int par = find(s[i]);\n    s[i] = par;\n    return par;\n  }\n\n  bool join(int u, int v) {\n    int pu = find(u), pv = find(v);\n    int ru = s[pu], rv = s[pv];\n\n    // same component already, return false.\n    if (pu == pv) {\n      return false;\n    }\n\n    // rank u larger\n    if (ru < rv) {\n      s[pu] = pv;\n    // rank v larger\n    } else if (ru > rv) {\n      s[pv] = pu;\n    } else {\n      s[pu] = pv;\n      s[pv]--;\n    }\n\n    return true;\n  }\n\n};\n\n/**\n * disjoint set doesnt work for directed graphs since it has no concept of an indegree or outdegree.\n *\n * BUT, we can get really hacky & use one of the \"side effects\" of disjoint set: it can tell us when \n * we try to add two vertices that are already connected.\n *\n * but this only handles one case, where we have cyclic graph. the other issue is that we might have an extra node \n * which causes u -> v to happen twice. this obviously needs to be the one that's removed. what we can do is maintain an extra \n * array, loop edges and update this array. if we try to update it twice, we know the current parent and next parent.\n */\nclass Solution {\npublic:\n  std::vector<int> findRedundantDirectedConnection(std::vector<std::vector<int>>& edges) {\n    int ov = -1, cp = -1, np = -1;\n    std::vector<int> parents(edges.size() + 1, -1);\n\n    for (auto& edge: edges) {\n      int u = edge[0], v = edge[1];\n      if (parents[v] == -1) {\n        parents[v] = u;\n      } else { // found the repetition\n        ov = v;\n        cp = parents[v];\n        np = u;\n        break;\n      }\n    }\n\n    DisjointSet ds(edges.size() + 1);\n\n    for (auto& edge : edges) {\n      int u = edge[0], v = edge[1];\n\n      if (ov == v && u == np) continue;\n\n      if (!ds.join(u, v)) {\n        return ov == -1 ? edge : std::vector<int>{ cp, ov };\n      }\n    }\n\n    return { np, ov };\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1862918958",
        "topics": [
          {
            "id": "a8ba0f6e-6916-4e86-82a6-5abb4112bc68",
            "questionId": "252c86e3-ac63-4e56-82ec-b504e94e7f4c",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-22T22:52:07.904604"
          },
          {
            "id": "ee1a8850-81bd-428b-8b1d-ba5d8ecf2dbd",
            "questionId": "252c86e3-ac63-4e56-82ec-b504e94e7f4c",
            "topicSlug": "breadth-first-search",
            "topic": "BREADTH_FIRST_SEARCH",
            "createdAt": "2025-12-22T22:52:07.900227"
          },
          {
            "id": "f9481824-4ee2-47ca-87d3-828d1197fb52",
            "questionId": "252c86e3-ac63-4e56-82ec-b504e94e7f4c",
            "topicSlug": "union-find",
            "topic": "UNION_FIND",
            "createdAt": "2025-12-22T22:52:07.902553"
          },
          {
            "id": "ed0d7e8f-d03a-48a6-91d2-c413f1d241b6",
            "questionId": "252c86e3-ac63-4e56-82ec-b504e94e7f4c",
            "topicSlug": "depth-first-search",
            "topic": "DEPTH_FIRST_SEARCH",
            "createdAt": "2025-12-22T22:52:07.897802"
          }
        ]
      },
      {
        "id": "9f3203af-c70e-4f66-93c2-f16bdfe8e885",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "redundant-connection",
        "questionTitle": "Redundant Connection",
        "questionDifficulty": "Medium",
        "questionNumber": 684,
        "questionLink": "https://leetcode.com/problems/redundant-connection",
        "description": "<p>In this problem, a tree is an <strong>undirected graph</strong> that is connected and has no cycles.</p>\n\n<p>You are given a graph that started as a tree with <code>n</code> nodes labeled from <code>1</code> to <code>n</code>, with one additional edge added. The added edge has two <strong>different</strong> vertices chosen from <code>1</code> to <code>n</code>, and was not an edge that already existed. The graph is represented as an array <code>edges</code> of length <code>n</code> where <code>edges[i] = [a<sub>i</sub>, b<sub>i</sub>]</code> indicates that there is an edge between nodes <code>a<sub>i</sub></code> and <code>b<sub>i</sub></code> in the graph.</p>\n\n<p>Return <em>an edge that can be removed so that the resulting graph is a tree of </em><code>n</code><em> nodes</em>. If there are multiple answers, return the answer that occurs last in the input.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2021/05/02/reduntant1-1-graph.jpg\" style=\"width: 222px; height: 222px;\" />\n<pre>\n<strong>Input:</strong> edges = [[1,2],[1,3],[2,3]]\n<strong>Output:</strong> [2,3]\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2021/05/02/reduntant1-2-graph.jpg\" style=\"width: 382px; height: 222px;\" />\n<pre>\n<strong>Input:</strong> edges = [[1,2],[2,3],[3,4],[1,4],[1,5]]\n<strong>Output:</strong> [1,4]\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>n == edges.length</code></li>\n\t<li><code>3 &lt;= n &lt;= 1000</code></li>\n\t<li><code>edges[i].length == 2</code></li>\n\t<li><code>1 &lt;= a<sub>i</sub> &lt; b<sub>i</sub> &lt;= edges.length</code></li>\n\t<li><code>a<sub>i</sub> != b<sub>i</sub></code></li>\n\t<li>There are no repeated edges.</li>\n\t<li>The given graph is connected.</li>\n</ul>\n",
        "pointsAwarded": 205,
        "acceptanceRate": 0.67,
        "createdAt": "2025-12-22T20:06:16.614929",
        "submittedAt": "2025-12-22T20:06:01",
        "runtime": "0 ms",
        "memory": "12.9 MB",
        "code": "class DisjointSet {\n  std::vector<int> s;\n\npublic:\n  DisjointSet(int n) : s(std::vector<int>(n, -1)) {}\n\n  int find(int i) {\n    if (s[i] < 0) {\n      return i;\n    }\n\n    int par = find(s[i]);\n    s[i] = par;\n    return par;\n  }\n\n  bool join(int u, int v) {\n    int pu = find(u), pv = find(v);\n    int ru = s[pu], rv = s[pv];\n\n    // same component already, return false.\n    if (pu == pv) {\n      return false;\n    }\n\n    // rank u larger\n    if (ru < rv) {\n      s[pu] = pv;\n    // rank v larger\n    } else if (ru > rv) {\n      s[pv] = pu;\n    } else {\n      s[pu] = pv;\n      s[pv]--;\n    }\n\n    return true;\n  }\n\n};\n\n/**\n * the main thing to realize is that we can find the redundant connection when \n * we try to connect two things together that are already connected. this also works \n * because the constraint says that if there are \"multiple answers\", remove the last one.\n * this already happens due to how a disjoint set works (if there is PREIVOUSLY a connection, then we \n * know our answer is this). \n */\nclass Solution {\npublic:\n  std::vector<int> findRedundantConnection(std::vector<std::vector<int>>& edges) {\n    DisjointSet ds(edges.size() + 1);\n\n    for (auto& edge : edges) {\n      int src = edge[0], trg = edge[1];\n\n      if (!ds.join(src, trg)) {\n        return { src, trg };\n      }\n    }\n\n    throw std::runtime_error(\"cant happen\");\n  }\n};",
        "language": "cpp",
        "submissionId": "1862854521",
        "topics": [
          {
            "id": "3dcab695-878b-4b91-a886-17f5a9e13e81",
            "questionId": "9f3203af-c70e-4f66-93c2-f16bdfe8e885",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-22T20:06:16.632558"
          },
          {
            "id": "f6c9899d-108c-4713-abd3-4c21cf3822cc",
            "questionId": "9f3203af-c70e-4f66-93c2-f16bdfe8e885",
            "topicSlug": "breadth-first-search",
            "topic": "BREADTH_FIRST_SEARCH",
            "createdAt": "2025-12-22T20:06:16.628337"
          },
          {
            "id": "9de1695a-52b7-4b4f-807d-109e3e3644c8",
            "questionId": "9f3203af-c70e-4f66-93c2-f16bdfe8e885",
            "topicSlug": "union-find",
            "topic": "UNION_FIND",
            "createdAt": "2025-12-22T20:06:16.630354"
          },
          {
            "id": "43745c69-2f5b-491c-b92f-93a7a1ad7738",
            "questionId": "9f3203af-c70e-4f66-93c2-f16bdfe8e885",
            "topicSlug": "depth-first-search",
            "topic": "DEPTH_FIRST_SEARCH",
            "createdAt": "2025-12-22T20:06:16.625692"
          }
        ]
      },
      {
        "id": "d7f69c4b-e8c1-45bd-bb39-c363676d5d05",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "parallel-courses-iii",
        "questionTitle": "Parallel Courses III",
        "questionDifficulty": "Hard",
        "questionNumber": 2176,
        "questionLink": "https://leetcode.com/problems/parallel-courses-iii",
        "description": "<p>You are given an integer <code>n</code>, which indicates that there are <code>n</code> courses labeled from <code>1</code> to <code>n</code>. You are also given a 2D integer array <code>relations</code> where <code>relations[j] = [prevCourse<sub>j</sub>, nextCourse<sub>j</sub>]</code> denotes that course <code>prevCourse<sub>j</sub></code> has to be completed <strong>before</strong> course <code>nextCourse<sub>j</sub></code> (prerequisite relationship). Furthermore, you are given a <strong>0-indexed</strong> integer array <code>time</code> where <code>time[i]</code> denotes how many <strong>months</strong> it takes to complete the <code>(i+1)<sup>th</sup></code> course.</p>\n\n<p>You must find the <strong>minimum</strong> number of months needed to complete all the courses following these rules:</p>\n\n<ul>\n\t<li>You may start taking a course at <strong>any time</strong> if the prerequisites are met.</li>\n\t<li><strong>Any number of courses</strong> can be taken at the <strong>same time</strong>.</li>\n</ul>\n\n<p>Return <em>the <strong>minimum</strong> number of months needed to complete all the courses</em>.</p>\n\n<p><strong>Note:</strong> The test cases are generated such that it is possible to complete every course (i.e., the graph is a directed acyclic graph).</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<strong><img alt=\"\" src=\"https://assets.leetcode.com/uploads/2021/10/07/ex1.png\" style=\"width: 392px; height: 232px;\" /></strong>\n\n<pre>\n<strong>Input:</strong> n = 3, relations = [[1,3],[2,3]], time = [3,2,5]\n<strong>Output:</strong> 8\n<strong>Explanation:</strong> The figure above represents the given graph and the time required to complete each course. \nWe start course 1 and course 2 simultaneously at month 0.\nCourse 1 takes 3 months and course 2 takes 2 months to complete respectively.\nThus, the earliest time we can start course 3 is at month 3, and the total time required is 3 + 5 = 8 months.\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n<strong><img alt=\"\" src=\"https://assets.leetcode.com/uploads/2021/10/07/ex2.png\" style=\"width: 500px; height: 365px;\" /></strong>\n\n<pre>\n<strong>Input:</strong> n = 5, relations = [[1,5],[2,5],[3,5],[3,4],[4,5]], time = [1,2,3,4,5]\n<strong>Output:</strong> 12\n<strong>Explanation:</strong> The figure above represents the given graph and the time required to complete each course.\nYou can start courses 1, 2, and 3 at month 0.\nYou can complete them after 1, 2, and 3 months respectively.\nCourse 4 can be taken only after course 3 is completed, i.e., after 3 months. It is completed after 3 + 4 = 7 months.\nCourse 5 can be taken only after courses 1, 2, 3, and 4 have been completed, i.e., after max(1,2,3,7) = 7 months.\nThus, the minimum time needed to complete all the courses is 7 + 5 = 12 months.\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= n &lt;= 5 * 10<sup>4</sup></code></li>\n\t<li><code>0 &lt;= relations.length &lt;= min(n * (n - 1) / 2, 5 * 10<sup>4</sup>)</code></li>\n\t<li><code>relations[j].length == 2</code></li>\n\t<li><code>1 &lt;= prevCourse<sub>j</sub>, nextCourse<sub>j</sub> &lt;= n</code></li>\n\t<li><code>prevCourse<sub>j</sub> != nextCourse<sub>j</sub></code></li>\n\t<li>All the pairs <code>[prevCourse<sub>j</sub>, nextCourse<sub>j</sub>]</code> are <strong>unique</strong>.</li>\n\t<li><code>time.length == n</code></li>\n\t<li><code>1 &lt;= time[i] &lt;= 10<sup>4</sup></code></li>\n\t<li>The given graph is a directed acyclic graph.</li>\n</ul>\n",
        "pointsAwarded": 419,
        "acceptanceRate": 0.66800004,
        "createdAt": "2025-12-22T00:37:31.724453",
        "submittedAt": "2025-12-22T00:37:06",
        "runtime": "47 ms",
        "memory": "136 MB",
        "code": "class Solution {\npublic:\n  /**\n   * this is obvi going to need kahn's algorithm, but for each jump to the next \"course\"\n   * we need to consider the max(parents) because we have to wait for all requirements before we can move forward.\n   *\n   * im thinking that we could probably combine kahn's with djikstra's where we keep track of the distance as we drag along.\n   * at the end, we just need to find the max elem of that array\n   *\n   * and at the end, we can just sum the times of each.\n   */\n  int minimumTime(int n, std::vector<std::vector<int>>& relations, std::vector<int>& time) {\n    std::vector<std::vector<int>> postreqs(n + 1, std::vector<int>());\n    std::vector<int> prereqs(n + 1, 0);\n    std::vector<int> elapsed(n + 1, -1);\n    int totalElapsed = 0;\n\n    for (auto& relation : relations) {\n      int prev = relation[0], next = relation[1];\n      prereqs[next]++;\n      postreqs[prev].emplace_back(next);\n    }\n\n    std::queue<int> q;\n\n    for (int course = 1; course < prereqs.size(); course++) {\n      int cnt = prereqs[course];\n      if (cnt == 0) {\n        q.emplace(course);\n        elapsed[course] = time[course - 1];\n      }\n    }\n\n    while (!q.empty()) {\n      int course = q.front();\n      q.pop();\n\n      auto& children = postreqs[course];\n\n      for (int child : children) {\n        elapsed[child] = std::max(elapsed[course] + time[child - 1], elapsed[child]);\n        int rem = --prereqs[child];\n        if (rem == 0) {\n          q.emplace(child);\n        }\n      }\n    }\n\n    return *std::max_element(elapsed.begin(), elapsed.end());\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1862092367",
        "topics": [
          {
            "id": "0e365734-cab2-4b3d-a1c4-5ec9ea7f18bd",
            "questionId": "d7f69c4b-e8c1-45bd-bb39-c363676d5d05",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-22T00:37:31.768264"
          },
          {
            "id": "ce89c833-80e4-4064-80b1-02d2879453e2",
            "questionId": "d7f69c4b-e8c1-45bd-bb39-c363676d5d05",
            "topicSlug": "dynamic-programming",
            "topic": "DYNAMIC_PROGRAMMING",
            "createdAt": "2025-12-22T00:37:31.766275"
          },
          {
            "id": "f5f89893-46af-4aa5-8276-e4c42289f8ec",
            "questionId": "d7f69c4b-e8c1-45bd-bb39-c363676d5d05",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-22T00:37:31.764054"
          },
          {
            "id": "7c7db2ff-1958-4069-a1bf-b28a52c2a20b",
            "questionId": "d7f69c4b-e8c1-45bd-bb39-c363676d5d05",
            "topicSlug": "topological-sort",
            "topic": "TOPOLOGICAL_SORT",
            "createdAt": "2025-12-22T00:37:31.770363"
          }
        ]
      },
      {
        "id": "db998efc-c09b-4509-9bea-3c9aa5854130",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "network-delay-time",
        "questionTitle": "Network Delay Time",
        "questionDifficulty": "Medium",
        "questionNumber": 744,
        "questionLink": "https://leetcode.com/problems/network-delay-time",
        "description": "<p>You are given a network of <code>n</code> nodes, labeled from <code>1</code> to <code>n</code>. You are also given <code>times</code>, a list of travel times as directed edges <code>times[i] = (u<sub>i</sub>, v<sub>i</sub>, w<sub>i</sub>)</code>, where <code>u<sub>i</sub></code> is the source node, <code>v<sub>i</sub></code> is the target node, and <code>w<sub>i</sub></code> is the time it takes for a signal to travel from source to target.</p>\n\n<p>We will send a signal from a given node <code>k</code>. Return <em>the <strong>minimum</strong> time it takes for all the</em> <code>n</code> <em>nodes to receive the signal</em>. If it is impossible for all the <code>n</code> nodes to receive the signal, return <code>-1</code>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2019/05/23/931_example_1.png\" style=\"width: 217px; height: 239px;\" />\n<pre>\n<strong>Input:</strong> times = [[2,1,1],[2,3,1],[3,4,1]], n = 4, k = 2\n<strong>Output:</strong> 2\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> times = [[1,2,1]], n = 2, k = 1\n<strong>Output:</strong> 1\n</pre>\n\n<p><strong class=\"example\">Example 3:</strong></p>\n\n<pre>\n<strong>Input:</strong> times = [[1,2,1]], n = 2, k = 2\n<strong>Output:</strong> -1\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= k &lt;= n &lt;= 100</code></li>\n\t<li><code>1 &lt;= times.length &lt;= 6000</code></li>\n\t<li><code>times[i].length == 3</code></li>\n\t<li><code>1 &lt;= u<sub>i</sub>, v<sub>i</sub> &lt;= n</code></li>\n\t<li><code>u<sub>i</sub> != v<sub>i</sub></code></li>\n\t<li><code>0 &lt;= w<sub>i</sub> &lt;= 100</code></li>\n\t<li>All the pairs <code>(u<sub>i</sub>, v<sub>i</sub>)</code> are <strong>unique</strong>. (i.e., no multiple edges.)</li>\n</ul>\n",
        "pointsAwarded": 210,
        "acceptanceRate": 0.592,
        "createdAt": "2025-12-21T22:22:03.39725",
        "submittedAt": "2025-12-21T22:19:17",
        "runtime": "80 ms",
        "memory": "44.2 MB",
        "code": "class Solution {\npublic:\n  int networkDelayTime(vector<vector<int>>& times, int n, int k) {\n    std::unordered_map<int, std::vector<std::pair<int, int>>> adjList;\n\n    for (auto& time : times) {\n      int src = time[0], trg = time[1], w = time[2];\n      adjList[src].push_back({w, trg});\n    }\n\n    std::priority_queue<\n      std::pair<int, int>,\n      std::vector<std::pair<int, int>>,\n      std::greater<std::pair<int, int>>\n    > q;\n\n    // 0 is skipped.\n    std::vector<int> dist(n + 1, INT_MAX);\n\n    int startNode = k;\n    auto first = std::pair<int, int>(0, startNode);\n    dist[startNode] = 0;\n    q.emplace(first);\n\n    while (!q.empty()) {\n      int w = q.top().first;\n      int n = q.top().second;\n      q.pop();\n\n      if (w > dist[n]) continue;\n\n      for (const auto& [cw, cn] : adjList[n]) {\n        if (cw + w < dist[cn]) {\n          dist[cn] = cw + w;\n          q.push({ cw + w, cn });\n        }\n      }\n    }\n\n    int max = -1;\n    for (int i = 1; i < dist.size(); i++) {\n      int d = dist[i];\n      if (d == INT_MAX) {\n        return -1;\n      }\n      max = std::max(max, d);\n    }\n\n    return max;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1862009213",
        "topics": [
          {
            "id": "49680d82-cdb6-4378-81dc-944415800c80",
            "questionId": "db998efc-c09b-4509-9bea-3c9aa5854130",
            "topicSlug": "shortest-path",
            "topic": "SHORTEST_PATH",
            "createdAt": "2025-12-21T22:22:03.452969"
          },
          {
            "id": "e35161d2-8d47-4181-84c5-c989d91c18ff",
            "questionId": "db998efc-c09b-4509-9bea-3c9aa5854130",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-21T22:22:03.446653"
          },
          {
            "id": "9c6f73b5-b455-4f80-be26-7b27f3aec9bf",
            "questionId": "db998efc-c09b-4509-9bea-3c9aa5854130",
            "topicSlug": "breadth-first-search",
            "topic": "BREADTH_FIRST_SEARCH",
            "createdAt": "2025-12-21T22:22:03.443948"
          },
          {
            "id": "4ca50632-fed6-465f-bb1f-e6e932e6d726",
            "questionId": "db998efc-c09b-4509-9bea-3c9aa5854130",
            "topicSlug": "depth-first-search",
            "topic": "DEPTH_FIRST_SEARCH",
            "createdAt": "2025-12-21T22:22:03.432552"
          },
          {
            "id": "5f8999ec-4cc2-407a-be13-1a59baac5f78",
            "questionId": "db998efc-c09b-4509-9bea-3c9aa5854130",
            "topicSlug": "heap-priority-queue",
            "topic": "HEAP_PRIORITY_QUEUE",
            "createdAt": "2025-12-21T22:22:03.449258"
          }
        ]
      },
      {
        "id": "4130c8e6-f673-4d3c-aa2f-c0ffe37c64c4",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "min-cost-to-connect-all-points",
        "questionTitle": "Min Cost to Connect All Points",
        "questionDifficulty": "Medium",
        "questionNumber": 1706,
        "questionLink": "https://leetcode.com/problems/min-cost-to-connect-all-points",
        "description": "<p>You are given an array <code>points</code> representing integer coordinates of some points on a 2D-plane, where <code>points[i] = [x<sub>i</sub>, y<sub>i</sub>]</code>.</p>\n\n<p>The cost of connecting two points <code>[x<sub>i</sub>, y<sub>i</sub>]</code> and <code>[x<sub>j</sub>, y<sub>j</sub>]</code> is the <strong>manhattan distance</strong> between them: <code>|x<sub>i</sub> - x<sub>j</sub>| + |y<sub>i</sub> - y<sub>j</sub>|</code>, where <code>|val|</code> denotes the absolute value of <code>val</code>.</p>\n\n<p>Return <em>the minimum cost to make all points connected.</em> All points are connected if there is <strong>exactly one</strong> simple path between any two points.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/08/26/d.png\" style=\"width: 214px; height: 268px;\" />\n<pre>\n<strong>Input:</strong> points = [[0,0],[2,2],[3,10],[5,2],[7,0]]\n<strong>Output:</strong> 20\n<strong>Explanation:</strong> \n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/08/26/c.png\" style=\"width: 214px; height: 268px;\" />\nWe can connect the points as shown above to get the minimum cost of 20.\nNotice that there is a unique path between every pair of points.\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> points = [[3,12],[-2,5],[-4,1]]\n<strong>Output:</strong> 18\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= points.length &lt;= 1000</code></li>\n\t<li><code>-10<sup>6</sup> &lt;= x<sub>i</sub>, y<sub>i</sub> &lt;= 10<sup>6</sup></code></li>\n\t<li>All pairs <code>(x<sub>i</sub>, y<sub>i</sub>)</code> are distinct.</li>\n</ul>\n",
        "pointsAwarded": 193,
        "acceptanceRate": 0.7,
        "createdAt": "2025-12-21T20:12:29.546783",
        "submittedAt": "2025-12-21T20:07:29",
        "runtime": "599 ms",
        "memory": "133.1 MB",
        "code": "class DisjointSet {\n  std::vector<int> parent;\n  std::vector<int> rank;\n\n  public:\n    DisjointSet(int n) {\n      // 0 is ignored\n      rank.resize(n + 1);\n      parent.resize(n + 1);\n\n      for (int i = 1; i <= n; i++) {\n        parent[i] = i;\n        rank[i] = 0;\n      }\n    }\n\n    int find(int i) {\n      if (parent[i] == i) {\n        return i;\n      }\n\n      int p = find(parent[i]);\n      parent[i] = p;\n      return p;\n    }\n\n    void join(int u, int v) {\n      int pu = find(u);\n      int pv = find(v);\n\n      int ru = rank[pu];\n      int rv = rank[pv];\n\n      if (ru < rv) {\n        parent[pu] = pv;\n      } else if (ru > rv) {\n        parent[pv] = pu;\n      } else {\n        parent[pv] = pu;\n        rank[rv]++;\n      }\n    }\n};\n\nclass Adj {\npublic:\n  int w;\n  int p1;\n  int p2;\n\n  Adj(int _w, int _p1, int _p2) : w(_w), p1(_p1), p2(_p2) {}\n};\n\nclass Solution {\npublic:\n  int minCostConnectPoints(std::vector<std::vector<int>>& points) {\n    std::vector<Adj> adjList;\n\n    for (int i = 0; i < points.size(); i++) {\n      for (int j = i + 1; j < points.size(); j++) {\n        auto& pi = points[i], pj = points[j];\n        int xi = pi[0], yi = pi[1], xj = pj[0], yj = pj[1];\n        int d = std::abs(xi - xj) + std::abs(yi - yj);\n        Adj adj(d, i, j);\n        adjList.emplace_back(adj);\n      }\n    }\n\n    std::sort(adjList.begin(), adjList.end(), [](Adj &larger, Adj &smaller) {\n      return smaller.w > larger.w;\n    });\n\n    DisjointSet ds(points.size());\n\n    int tw = 0;\n    for (auto& adj : adjList) {\n      if (ds.find(adj.p1) != ds.find(adj.p2)) {\n        tw += adj.w;\n        ds.join(adj.p1, adj.p2);\n      }\n    }\n\n    return tw;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1861961318",
        "topics": [
          {
            "id": "c02befc2-5f06-4ce1-84c8-2cd5cc61a0c4",
            "questionId": "4130c8e6-f673-4d3c-aa2f-c0ffe37c64c4",
            "topicSlug": "minimum-spanning-tree",
            "topic": "MINIMUM_SPANNING_TREE",
            "createdAt": "2025-12-21T20:12:29.599454"
          },
          {
            "id": "9983c054-7249-4e07-8e74-7a6ef4454464",
            "questionId": "4130c8e6-f673-4d3c-aa2f-c0ffe37c64c4",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-21T20:12:29.597615"
          },
          {
            "id": "b0a6b5b8-37fc-4587-a6ef-9afaba11d1ef",
            "questionId": "4130c8e6-f673-4d3c-aa2f-c0ffe37c64c4",
            "topicSlug": "union-find",
            "topic": "UNION_FIND",
            "createdAt": "2025-12-21T20:12:29.59559"
          },
          {
            "id": "1246ad5d-4bb7-406c-80dc-a43c5b7d635c",
            "questionId": "4130c8e6-f673-4d3c-aa2f-c0ffe37c64c4",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-21T20:12:29.592359"
          }
        ]
      },
      {
        "id": "a27246a1-68ac-48bc-b387-040e22f4a8b1",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "number-of-provinces",
        "questionTitle": "Number of Provinces",
        "questionDifficulty": "Medium",
        "questionNumber": 547,
        "questionLink": "https://leetcode.com/problems/number-of-provinces",
        "description": "<p>There are <code>n</code> cities. Some of them are connected, while some are not. If city <code>a</code> is connected directly with city <code>b</code>, and city <code>b</code> is connected directly with city <code>c</code>, then city <code>a</code> is connected indirectly with city <code>c</code>.</p>\n\n<p>A <strong>province</strong> is a group of directly or indirectly connected cities and no other cities outside of the group.</p>\n\n<p>You are given an <code>n x n</code> matrix <code>isConnected</code> where <code>isConnected[i][j] = 1</code> if the <code>i<sup>th</sup></code> city and the <code>j<sup>th</sup></code> city are directly connected, and <code>isConnected[i][j] = 0</code> otherwise.</p>\n\n<p>Return <em>the total number of <strong>provinces</strong></em>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/12/24/graph1.jpg\" style=\"width: 222px; height: 142px;\" />\n<pre>\n<strong>Input:</strong> isConnected = [[1,1,0],[1,1,0],[0,0,1]]\n<strong>Output:</strong> 2\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/12/24/graph2.jpg\" style=\"width: 222px; height: 142px;\" />\n<pre>\n<strong>Input:</strong> isConnected = [[1,0,0],[0,1,0],[0,0,1]]\n<strong>Output:</strong> 3\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= n &lt;= 200</code></li>\n\t<li><code>n == isConnected.length</code></li>\n\t<li><code>n == isConnected[i].length</code></li>\n\t<li><code>isConnected[i][j]</code> is <code>1</code> or <code>0</code>.</li>\n\t<li><code>isConnected[i][i] == 1</code></li>\n\t<li><code>isConnected[i][j] == isConnected[j][i]</code></li>\n</ul>\n",
        "pointsAwarded": 207,
        "acceptanceRate": 0.69699997,
        "createdAt": "2025-12-21T17:35:24.612466",
        "submittedAt": "2025-12-21T17:22:17",
        "runtime": "6 ms",
        "memory": "19.6 MB",
        "code": "class DisjointSet {\n  std::vector<int> parent;\n  std::vector<int> rank;\n\n  public:\n    DisjointSet(int n) {\n      // 0 is ignored\n      rank.resize(n + 1);\n      parent.resize(n + 1);\n\n      for (int i = 1; i <= n; i++) {\n        parent[i] = i;\n        rank[i] = 0;\n      }\n    }\n\n    int find(int i) {\n      if (parent[i] == i) {\n        return i;\n      }\n\n      int p = find(parent[i]);\n      parent[i] = p;\n      return p;\n    }\n\n    void join(int u, int v) {\n      int pu = find(u);\n      int pv = find(v);\n\n      int ru = rank[pu];\n      int rv = rank[pv];\n\n      if (ru < rv) {\n        parent[pu] = pv;\n      } else if (ru > rv) {\n        parent[pv] = pu;\n      } else {\n        parent[pv] = pu;\n        rank[rv]++;\n      }\n    }\n};\n\nclass Solution {\n  public:\n    /**\n    * example:\n    *\n    * [\n    *   [1,1,0],\n    *   [1,1,0],\n    *   [0,0,1]\n    * ]\n    *\n    * which means (l to r):\n    * c1 -> c1\n    * c1 -> c2\n    * c2 -> c1\n    * c2 -> c2\n    * c3 -> c3\n    *\n    * first of all, identity connections everywhere (they can be ignored).\n    */\n    int findCircleNum(std::vector<std::vector<int>>& isConnected) {\n      // i == j == n\n      DisjointSet ds(isConnected.size());\n\n      for (int city = 0; city < isConnected.size(); city++) {\n        auto& conns = isConnected[city];\n\n        std::cout << std::endl;\n        for (int connCity = 0; connCity < conns.size(); connCity++) {\n          if (connCity == city) continue;\n\n          auto conn = conns[connCity];\n          if (conn == 1) {\n            ds.join(city + 1, connCity + 1);\n          }\n        }\n      }\n\n      std::unordered_set<int> seen;\n      for (int city = 1; city <= isConnected.size(); city++) {\n        seen.emplace(ds.find(city));\n      }\n\n      return seen.size();\n    }\n};\n",
        "language": "cpp",
        "submissionId": "1861911228",
        "topics": [
          {
            "id": "6c2c3b33-2bfa-415c-af9b-d14a7c6fad4e",
            "questionId": "a27246a1-68ac-48bc-b387-040e22f4a8b1",
            "topicSlug": "graph",
            "topic": "GRAPH",
            "createdAt": "2025-12-21T17:35:24.641419"
          },
          {
            "id": "7c24fd9f-1584-47b6-819f-1fbd7a0ae04d",
            "questionId": "a27246a1-68ac-48bc-b387-040e22f4a8b1",
            "topicSlug": "breadth-first-search",
            "topic": "BREADTH_FIRST_SEARCH",
            "createdAt": "2025-12-21T17:35:24.636605"
          },
          {
            "id": "a39216bd-b4ba-4034-af61-5ff9385106d6",
            "questionId": "a27246a1-68ac-48bc-b387-040e22f4a8b1",
            "topicSlug": "union-find",
            "topic": "UNION_FIND",
            "createdAt": "2025-12-21T17:35:24.638886"
          },
          {
            "id": "55f1f337-20f7-4b44-a26d-33a334d2a632",
            "questionId": "a27246a1-68ac-48bc-b387-040e22f4a8b1",
            "topicSlug": "depth-first-search",
            "topic": "DEPTH_FIRST_SEARCH",
            "createdAt": "2025-12-21T17:35:24.633411"
          }
        ]
      },
      {
        "id": "437a8494-c189-47ec-ad46-fe568e67de50",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "pascals-triangle",
        "questionTitle": "Pascal's Triangle",
        "questionDifficulty": "Easy",
        "questionNumber": 118,
        "questionLink": "https://leetcode.com/problems/pascals-triangle",
        "description": "<p>Given an integer <code>numRows</code>, return the first numRows of <strong>Pascal&#39;s triangle</strong>.</p>\n\n<p>In <strong>Pascal&#39;s triangle</strong>, each number is the sum of the two numbers directly above it as shown:</p>\n<img alt=\"\" src=\"https://upload.wikimedia.org/wikipedia/commons/0/0d/PascalTriangleAnimated2.gif\" style=\"height:240px; width:260px\" />\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<pre><strong>Input:</strong> numRows = 5\n<strong>Output:</strong> [[1],[1,1],[1,2,1],[1,3,3,1],[1,4,6,4,1]]\n</pre><p><strong class=\"example\">Example 2:</strong></p>\n<pre><strong>Input:</strong> numRows = 1\n<strong>Output:</strong> [[1]]\n</pre>\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= numRows &lt;= 30</code></li>\n</ul>\n",
        "pointsAwarded": 83,
        "acceptanceRate": 0.78400004,
        "createdAt": "2025-12-20T18:35:56.719451",
        "submittedAt": "2025-12-20T17:38:33",
        "runtime": "3 ms",
        "memory": "9.7 MB",
        "code": "class Solution {\npublic:\n  std::vector<std::vector<int>> generate(int numRows) {\n    std::vector<std::vector<int>> res;\n    res.push_back({1});\n    if (numRows == 1) return res;\n    res.push_back({1, 1});\n\n    for (int i = 2; i < numRows; i++) {\n      std::vector<int> row;\n      int cells = i + 1;\n\n      for (int c = 0; c < cells; c++) {\n        if (c == 0 || c == cells - 1) {\n          row.emplace_back(1);\n          continue;\n        }\n\n        auto& prev_row = res[i - 1];\n        int prev_l = prev_row[c - 1], prev_r = prev_row[c];\n\n        row.emplace_back(prev_l + prev_r);\n      }\n\n      res.emplace_back(row);\n    }\n\n    return res;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1861032493",
        "topics": [
          {
            "id": "bb8cd08c-f881-4d43-87cf-57394ae1e6d8",
            "questionId": "437a8494-c189-47ec-ad46-fe568e67de50",
            "topicSlug": "dynamic-programming",
            "topic": "DYNAMIC_PROGRAMMING",
            "createdAt": "2025-12-20T18:35:56.835557"
          },
          {
            "id": "26da692e-6529-4a9a-ba88-7083499a2d67",
            "questionId": "437a8494-c189-47ec-ad46-fe568e67de50",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-20T18:35:56.831441"
          }
        ]
      },
      {
        "id": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "repeated-dna-sequences",
        "questionTitle": "Repeated DNA Sequences",
        "questionDifficulty": "Medium",
        "questionNumber": 187,
        "questionLink": "https://leetcode.com/problems/repeated-dna-sequences",
        "description": "<p>The <strong>DNA sequence</strong> is composed of a series of nucleotides abbreviated as <code>&#39;A&#39;</code>, <code>&#39;C&#39;</code>, <code>&#39;G&#39;</code>, and <code>&#39;T&#39;</code>.</p>\n\n<ul>\n\t<li>For example, <code>&quot;ACGAATTCCG&quot;</code> is a <strong>DNA sequence</strong>.</li>\n</ul>\n\n<p>When studying <strong>DNA</strong>, it is useful to identify repeated sequences within the DNA.</p>\n\n<p>Given a string <code>s</code> that represents a <strong>DNA sequence</strong>, return all the <strong><code>10</code>-letter-long</strong> sequences (substrings) that occur more than once in a DNA molecule. You may return the answer in <strong>any order</strong>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<pre><strong>Input:</strong> s = \"AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT\"\n<strong>Output:</strong> [\"AAAAACCCCC\",\"CCCCCAAAAA\"]\n</pre><p><strong class=\"example\">Example 2:</strong></p>\n<pre><strong>Input:</strong> s = \"AAAAAAAAAAAAA\"\n<strong>Output:</strong> [\"AAAAAAAAAA\"]\n</pre>\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= s.length &lt;= 10<sup>5</sup></code></li>\n\t<li><code>s[i]</code> is either <code>&#39;A&#39;</code>, <code>&#39;C&#39;</code>, <code>&#39;G&#39;</code>, or <code>&#39;T&#39;</code>.</li>\n</ul>\n",
        "pointsAwarded": 224,
        "acceptanceRate": 0.525,
        "createdAt": "2025-12-20T16:35:37.506153",
        "submittedAt": "2025-12-20T16:08:22",
        "runtime": "35 ms",
        "memory": "26.1 MB",
        "code": "#define K 10\n\nclass Solution {\npublic:\n  /**\n   * s length of 11 means we should only run 0 and 1.\n   * \n   */\n  std::vector<std::string> findRepeatedDnaSequences(std::string s) {\n    std::vector<std::string> res;\n\n    if (s.length() <= K) return res;\n\n    std::unordered_map<std::string, int> seen;\n\n    for (int l = 0; l <= s.length() - K; l++) {\n      std::string key = s.substr(l, K);\n\n      if (auto found = seen.find(key); found != seen.end()) {\n        if (found->second == 1) {\n          res.emplace_back(found->first);\n        }\n        ++found->second;\n      } else {\n        ++seen[key];\n      }\n    }\n\n    return res;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1861002664",
        "topics": [
          {
            "id": "58e7ef93-d301-42d6-9572-4d712a1e372a",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "rolling-hash",
            "topic": "ROLLING_HASH",
            "createdAt": "2025-12-20T16:35:37.548686"
          },
          {
            "id": "42cb0bd8-72e0-4403-8583-d1045e96a2fd",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "string",
            "topic": "STRING",
            "createdAt": "2025-12-20T16:35:37.540082"
          },
          {
            "id": "502939b1-1c2f-47d0-8b4b-2d88cfda6878",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "sliding-window",
            "topic": "SLIDING_WINDOW",
            "createdAt": "2025-12-20T16:35:37.544978"
          },
          {
            "id": "ba9bd5bc-8bfd-4c53-918e-72f2b23b88ba",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "bit-manipulation",
            "topic": "BIT_MANIPULATION",
            "createdAt": "2025-12-20T16:35:37.542769"
          },
          {
            "id": "0bb7f90e-ec10-4d65-a390-40ec0ea7a78b",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "hash-function",
            "topic": "HASH_FUNCTION",
            "createdAt": "2025-12-20T16:35:37.551251"
          },
          {
            "id": "04cfc6ff-aa8f-459d-8ee8-a034ee04e92e",
            "questionId": "394f5f1b-5609-4eb9-9eb7-a348ce23f1e1",
            "topicSlug": "hash-table",
            "topic": "HASH_TABLE",
            "createdAt": "2025-12-20T16:35:37.536861"
          }
        ]
      },
      {
        "id": "3e21b9b9-c6e2-4951-8e95-11311498add5",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "contains-duplicate-ii",
        "questionTitle": "Contains Duplicate II",
        "questionDifficulty": "Easy",
        "questionNumber": 219,
        "questionLink": "https://leetcode.com/problems/contains-duplicate-ii",
        "description": "<p>Given an integer array <code>nums</code> and an integer <code>k</code>, return <code>true</code> <em>if there are two <strong>distinct indices</strong> </em><code>i</code><em> and </em><code>j</code><em> in the array such that </em><code>nums[i] == nums[j]</code><em> and </em><code>abs(i - j) &lt;= k</code>.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [1,2,3,1], k = 3\n<strong>Output:</strong> true\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [1,0,1,1], k = 1\n<strong>Output:</strong> true\n</pre>\n\n<p><strong class=\"example\">Example 3:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [1,2,3,1,2,3], k = 2\n<strong>Output:</strong> false\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>1 &lt;= nums.length &lt;= 10<sup>5</sup></code></li>\n\t<li><code>-10<sup>9</sup> &lt;= nums[i] &lt;= 10<sup>9</sup></code></li>\n\t<li><code>0 &lt;= k &lt;= 10<sup>5</sup></code></li>\n</ul>\n",
        "pointsAwarded": 0,
        "acceptanceRate": 0.503,
        "createdAt": "2025-12-20T15:05:45.418574",
        "submittedAt": "2025-12-20T14:50:30",
        "runtime": "75 ms",
        "memory": "98.7 MB",
        "code": "using Map = std::unordered_map<int, int>;\n\n/**\n  * [1,2,3,1,4,1]\n  * <>\n  * <  >\n  * <    >\n  * <      >\n  *\n  * if we saw that the number has been seen before,\n  * since its fixed size sliding window , we should check\n  * if we are larger than k and seen reports that nums[r] has been seen, get the \n  * value of the key from the map (which is index) and return.\n  *\n  * else add to map using key of r and value of nums[r]\n  */\nclass Solution {\npublic:\n  bool containsNearbyDuplicate(vector<int>& nums, int k) {\n    Map seen;\n\n    for (int r = 0; r < nums.size(); r++) {\n      Map::iterator found = seen.find(nums[r]);\n      if (found != seen.end() && r - found->second <= k) {\n        return true;\n      }\n\n      seen[nums[r]] = r;\n    }\n\n    return false;\n  }\n};\n",
        "language": "cpp",
        "submissionId": "1860970265",
        "topics": [
          {
            "id": "9c8ebe97-eb4e-4b90-807a-04b7d06fda03",
            "questionId": "3e21b9b9-c6e2-4951-8e95-11311498add5",
            "topicSlug": "sliding-window",
            "topic": "SLIDING_WINDOW",
            "createdAt": "2025-12-20T15:05:45.485929"
          },
          {
            "id": "72934fa2-6d2b-4604-80d6-a9c804ffc71a",
            "questionId": "3e21b9b9-c6e2-4951-8e95-11311498add5",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-20T15:05:45.480169"
          },
          {
            "id": "c6cb15ec-bdcc-4761-929f-5b8c9588b350",
            "questionId": "3e21b9b9-c6e2-4951-8e95-11311498add5",
            "topicSlug": "hash-table",
            "topic": "HASH_TABLE",
            "createdAt": "2025-12-20T15:05:45.483345"
          }
        ]
      },
      {
        "id": "88eef810-ece5-41d3-b076-73036e98834e",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "simple-bank-system",
        "questionTitle": "Simple Bank System",
        "questionDifficulty": "Medium",
        "questionNumber": 2169,
        "questionLink": "https://leetcode.com/problems/simple-bank-system",
        "description": "<p>You have been tasked with writing a program for a popular bank that will automate all its incoming transactions (transfer, deposit, and withdraw). The bank has <code>n</code> accounts numbered from <code>1</code> to <code>n</code>. The initial balance of each account is stored in a <strong>0-indexed</strong> integer array <code>balance</code>, with the <code>(i + 1)<sup>th</sup></code> account having an initial balance of <code>balance[i]</code>.</p>\n\n<p>Execute all the <strong>valid</strong> transactions. A transaction is <strong>valid</strong> if:</p>\n\n<ul>\n\t<li>The given account number(s) are between <code>1</code> and <code>n</code>, and</li>\n\t<li>The amount of money withdrawn or transferred from is <strong>less than or equal</strong> to the balance of the account.</li>\n</ul>\n\n<p>Implement the <code>Bank</code> class:</p>\n\n<ul>\n\t<li><code>Bank(long[] balance)</code> Initializes the object with the <strong>0-indexed</strong> integer array <code>balance</code>.</li>\n\t<li><code>boolean transfer(int account1, int account2, long money)</code> Transfers <code>money</code> dollars from the account numbered <code>account1</code> to the account numbered <code>account2</code>. Return <code>true</code> if the transaction was successful, <code>false</code> otherwise.</li>\n\t<li><code>boolean deposit(int account, long money)</code> Deposit <code>money</code> dollars into the account numbered <code>account</code>. Return <code>true</code> if the transaction was successful, <code>false</code> otherwise.</li>\n\t<li><code>boolean withdraw(int account, long money)</code> Withdraw <code>money</code> dollars from the account numbered <code>account</code>. Return <code>true</code> if the transaction was successful, <code>false</code> otherwise.</li>\n</ul>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n\n<pre>\n<strong>Input</strong>\n[&quot;Bank&quot;, &quot;withdraw&quot;, &quot;transfer&quot;, &quot;deposit&quot;, &quot;transfer&quot;, &quot;withdraw&quot;]\n[[[10, 100, 20, 50, 30]], [3, 10], [5, 1, 20], [5, 20], [3, 4, 15], [10, 50]]\n<strong>Output</strong>\n[null, true, true, true, false, false]\n\n<strong>Explanation</strong>\nBank bank = new Bank([10, 100, 20, 50, 30]);\nbank.withdraw(3, 10);    // return true, account 3 has a balance of $20, so it is valid to withdraw $10.\n                         // Account 3 has $20 - $10 = $10.\nbank.transfer(5, 1, 20); // return true, account 5 has a balance of $30, so it is valid to transfer $20.\n                         // Account 5 has $30 - $20 = $10, and account 1 has $10 + $20 = $30.\nbank.deposit(5, 20);     // return true, it is valid to deposit $20 to account 5.\n                         // Account 5 has $10 + $20 = $30.\nbank.transfer(3, 4, 15); // return false, the current balance of account 3 is $10,\n                         // so it is invalid to transfer $15 from it.\nbank.withdraw(10, 50);   // return false, it is invalid because account 10 does not exist.\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>n == balance.length</code></li>\n\t<li><code>1 &lt;= n, account, account1, account2 &lt;= 10<sup>5</sup></code></li>\n\t<li><code>0 &lt;= balance[i], money &lt;= 10<sup>12</sup></code></li>\n\t<li>At most <code>10<sup>4</sup></code> calls will be made to <strong>each</strong> function <code>transfer</code>, <code>deposit</code>, <code>withdraw</code>.</li>\n</ul>\n",
        "pointsAwarded": 0,
        "acceptanceRate": 0.7,
        "createdAt": "2025-12-06T19:36:14.728267",
        "submittedAt": "2025-12-06T19:11:01",
        "runtime": "37 ms",
        "memory": "48.6 MB",
        "code": "class Bank:\n\n    def __init__(self, balance: List[int]):\n        self.accounts = [-2]\n        for b in balance:\n            self.accounts.append(b)\n   \n    def v(self, acc: int) -> bool:\n        if acc > len(self.accounts):\n            return False\n\n        return True\n\n    def transfer(self, account1: int, account2: int, money: int) -> bool:\n        if not self.v(account1) or not self.v(account2):\n            return False\n\n        m1, m2 = self.accounts[account1], self.accounts[account2]\n\n        # if account 1 current money is less than what we want to withdraw\n        if not self.withdraw(account1, money):\n            return False\n\n        self.deposit(account2, money)\n        return True\n\n    def deposit(self, account: int, money: int) -> bool:\n        if not self.v(account):\n            return False\n\n        self.accounts[account] += money\n        return True\n\n    def withdraw(self, account: int, money: int) -> bool:\n        if not self.v(account):\n            return False\n\n        cm = self.accounts[account]\n\n        # current money is less than what we want to withdraw\n        if cm < money:\n            return False\n\n        self.accounts[account] -= money\n        return True\n\n\n# Your Bank object will be instantiated and called as such:\n# obj = Bank(balance)\n# param_1 = obj.transfer(account1,account2,money)\n# param_2 = obj.deposit(account,money)\n# param_3 = obj.withdraw(account,money)",
        "language": "python3",
        "submissionId": "1848779524",
        "topics": [
          {
            "id": "564e2378-74d0-4f02-bb77-29eccd5de52a",
            "questionId": "88eef810-ece5-41d3-b076-73036e98834e",
            "topicSlug": "design",
            "topic": "DESIGN",
            "createdAt": "2025-12-06T19:36:14.75753"
          },
          {
            "id": "14fb9b3b-f51b-4d3b-91d3-ec7f6c7157c7",
            "questionId": "88eef810-ece5-41d3-b076-73036e98834e",
            "topicSlug": "simulation",
            "topic": "SIMULATION",
            "createdAt": "2025-12-06T19:36:14.760642"
          },
          {
            "id": "8a20048f-5b6d-435c-9c8e-d798f61472ea",
            "questionId": "88eef810-ece5-41d3-b076-73036e98834e",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-06T19:36:14.750184"
          },
          {
            "id": "bd2ddf2e-44a6-4239-ad98-1bd9e623befa",
            "questionId": "88eef810-ece5-41d3-b076-73036e98834e",
            "topicSlug": "hash-table",
            "topic": "HASH_TABLE",
            "createdAt": "2025-12-06T19:36:14.755394"
          }
        ]
      },
      {
        "id": "bf43eaa4-c497-4d13-8d53-965053e0b321",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "two-sum",
        "questionTitle": "Two Sum",
        "questionDifficulty": "Easy",
        "questionNumber": 1,
        "questionLink": "https://leetcode.com/problems/two-sum",
        "description": "<p>Given an array of integers <code>nums</code>&nbsp;and an integer <code>target</code>, return <em>indices of the two numbers such that they add up to <code>target</code></em>.</p>\n\n<p>You may assume that each input would have <strong><em>exactly</em> one solution</strong>, and you may not use the <em>same</em> element twice.</p>\n\n<p>You can return the answer in any order.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [2,7,11,15], target = 9\n<strong>Output:</strong> [0,1]\n<strong>Explanation:</strong> Because nums[0] + nums[1] == 9, we return [0, 1].\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [3,2,4], target = 6\n<strong>Output:</strong> [1,2]\n</pre>\n\n<p><strong class=\"example\">Example 3:</strong></p>\n\n<pre>\n<strong>Input:</strong> nums = [3,3], target = 6\n<strong>Output:</strong> [0,1]\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li><code>2 &lt;= nums.length &lt;= 10<sup>4</sup></code></li>\n\t<li><code>-10<sup>9</sup> &lt;= nums[i] &lt;= 10<sup>9</sup></code></li>\n\t<li><code>-10<sup>9</sup> &lt;= target &lt;= 10<sup>9</sup></code></li>\n\t<li><strong>Only one valid answer exists.</strong></li>\n</ul>\n\n<p>&nbsp;</p>\n<strong>Follow-up:&nbsp;</strong>Can you come up with an algorithm that is less than <code>O(n<sup>2</sup>)</code><font face=\"monospace\">&nbsp;</font>time complexity?",
        "pointsAwarded": 0,
        "acceptanceRate": 0.566,
        "createdAt": "2025-12-01T01:08:37.56932",
        "submittedAt": "2025-12-01T00:41:12",
        "runtime": "0 ms",
        "memory": "19.4 MB",
        "code": "class Solution:\n    def twoSum(self, nums: List[int], target: int) -> List[int]:\n        D = {}\n\n        for i in range(len(nums)):\n            if not(nums[i] in D):\n                D[target - nums[i]] = i\n            else:\n                return [D[nums[i]], i]",
        "language": "python3",
        "submissionId": "1843873839",
        "topics": [
          {
            "id": "afc98e6d-21ec-4910-ade7-1783e2eedd9f",
            "questionId": "bf43eaa4-c497-4d13-8d53-965053e0b321",
            "topicSlug": "array",
            "topic": "ARRAY",
            "createdAt": "2025-12-01T01:08:37.57797"
          },
          {
            "id": "13756b42-9f72-480d-b179-75cf2a9e2b71",
            "questionId": "bf43eaa4-c497-4d13-8d53-965053e0b321",
            "topicSlug": "hash-table",
            "topic": "HASH_TABLE",
            "createdAt": "2025-12-01T01:08:37.580493"
          }
        ]
      },
      {
        "id": "89621f2f-e5a0-45e4-b609-8f2bc520178d",
        "userId": "2f709448-2635-4651-9843-301c2128a011",
        "questionSlug": "add-two-numbers",
        "questionTitle": "Add Two Numbers",
        "questionDifficulty": "Medium",
        "questionNumber": 2,
        "questionLink": "https://leetcode.com/problems/add-two-numbers",
        "description": "<p>You are given two <strong>non-empty</strong> linked lists representing two non-negative integers. The digits are stored in <strong>reverse order</strong>, and each of their nodes contains a single digit. Add the two numbers and return the sum&nbsp;as a linked list.</p>\n\n<p>You may assume the two numbers do not contain any leading zero, except the number 0 itself.</p>\n\n<p>&nbsp;</p>\n<p><strong class=\"example\">Example 1:</strong></p>\n<img alt=\"\" src=\"https://assets.leetcode.com/uploads/2020/10/02/addtwonumber1.jpg\" style=\"width: 483px; height: 342px;\" />\n<pre>\n<strong>Input:</strong> l1 = [2,4,3], l2 = [5,6,4]\n<strong>Output:</strong> [7,0,8]\n<strong>Explanation:</strong> 342 + 465 = 807.\n</pre>\n\n<p><strong class=\"example\">Example 2:</strong></p>\n\n<pre>\n<strong>Input:</strong> l1 = [0], l2 = [0]\n<strong>Output:</strong> [0]\n</pre>\n\n<p><strong class=\"example\">Example 3:</strong></p>\n\n<pre>\n<strong>Input:</strong> l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]\n<strong>Output:</strong> [8,9,9,9,0,0,0,1]\n</pre>\n\n<p>&nbsp;</p>\n<p><strong>Constraints:</strong></p>\n\n<ul>\n\t<li>The number of nodes in each linked list is in the range <code>[1, 100]</code>.</li>\n\t<li><code>0 &lt;= Node.val &lt;= 9</code></li>\n\t<li>It is guaranteed that the list represents a number that does not have leading zeros.</li>\n</ul>\n",
        "pointsAwarded": 0,
        "acceptanceRate": 0.474,
        "createdAt": "2025-11-26T18:35:59.433101",
        "submittedAt": "2025-11-26T18:10:49",
        "runtime": "11 ms",
        "memory": "17.6 MB",
        "code": "# Definition for singly-linked list.\n# class ListNode:\n#     def __init__(self, val=0, next=None):\n#         self.val = val\n#         self.next = next\nclass Solution:\n    def addTwoNumbers(self, l1: ListNode, l2: ListNode) -> ListNode:\n        dummyHead = ListNode(0)\n        tail = dummyHead\n        carry = 0\n\n        while l1 is not None or l2 is not None or carry != 0:\n            digit1 = l1.val if l1 is not None else 0\n            digit2 = l2.val if l2 is not None else 0\n\n            sum = digit1 + digit2 + carry\n            digit = sum % 10\n            carry = sum // 10\n\n            newNode = ListNode(digit)\n            tail.next = newNode\n            tail = tail.next\n\n            l1 = l1.next if l1 is not None else None\n            l2 = l2.next if l2 is not None else None\n\n        result = dummyHead.next\n        dummyHead.next = None\n        return result",
        "language": "python3",
        "submissionId": "1840617892",
        "topics": [
          {
            "id": "2a4e8592-649f-41a6-82d6-b838f8f205e5",
            "questionId": "89621f2f-e5a0-45e4-b609-8f2bc520178d",
            "topicSlug": "linked-list",
            "topic": "LINKED_LIST",
            "createdAt": "2025-11-26T18:35:59.441048"
          },
          {
            "id": "6c71e5d0-72bc-4159-a18b-1d10d27b10c7",
            "questionId": "89621f2f-e5a0-45e4-b609-8f2bc520178d",
            "topicSlug": "math",
            "topic": "MATH",
            "createdAt": "2025-11-26T18:35:59.44363"
          },
          {
            "id": "3b1866e7-014b-4ce6-882f-6ac75dd5e1b8",
            "questionId": "89621f2f-e5a0-45e4-b609-8f2bc520178d",
            "topicSlug": "recursion",
            "topic": "RECURSION",
            "createdAt": "2025-11-26T18:35:59.445823"
          }
        ]
      }
    ],
    "pages": 18,
    "pageSize": 15
  }

  return (
    <Box
      mt={10}
      pos="relative"
      px={isMobile ? "xs" : undefined}
      w={isMobile ? undefined : "100%"}
      maw={isMobile ? undefined : 925}
      p={isMobile ? undefined : "xs"}
    >
      {!isMobile && (
        <Box display="block" style={{ textAlign: "right" }}>
          <FilterDropdown buttonName="Filters">
            <TopicFilterPopover
              value={topics}
              selectedTopicsSet={selectedTopicsSet}
              onChange={setTopics}
              onClear={clearTopics}
            />
            <FilterDropdownItem
              value={pointFilter}
              toggle={togglePointFilter}
              switchMode
              name={
                <Flex gap="0.5rem" align="center">
                  Points Received
                </Flex>
              }
            />
            <DateRangePopover
              startDate={startDate}
              endDate={endDate}
              onStartDateChange={setStartDate}
              onEndDateChange={setEndDate}
            />
          </FilterDropdown>
        </Box>
      )}
      <Group
        justify="space-between"
        align="flex-end"
        mb="sm"
        gap="sm"
        pt={isMobile ? undefined : 10}
      >
        <Box flex={1} miw={0}>
          <SearchBox
            pt={isMobile ? undefined : 0}
            query={searchQuery}
            onChange={(event) => setSearchQuery(event.currentTarget.value)}
            placeholder="Search for submission title"
            w={isMobile ? "100%" : undefined}
          />
        </Box>
        {isMobile && (
          <FilterDropdown buttonName="Filters">
            <TopicFilterPopover
              value={topics}
              selectedTopicsSet={selectedTopicsSet}
              onChange={setTopics}
              onClear={clearTopics}
            />
            <FilterDropdownItem
              value={pointFilter}
              toggle={togglePointFilter}
              switchMode
              name={
                <Flex gap="0.5rem" align="center">
                  Points Received
                </Flex>
              }
            />
            <DateRangePopover
              startDate={startDate}
              endDate={endDate}
              onStartDateChange={setStartDate}
              onEndDateChange={setEndDate}
            />
          </FilterDropdown>
        )}
      </Group>
      <Box pos="relative">
        {isPlaceholderData && (
          <Overlay zIndex={1000} backgroundOpacity={0.35} blur={3} />
        )}
        <Stack gap="sm" my="sm" align={isMobile ? undefined : "center"}>
          {!pageData || pageData.items.length === 0 ?
            <Card
              withBorder
              p="md"
              radius="md"
              mih={80}
              w="100%"
              flex={isMobile ? 1 : undefined}
            >
              <Stack gap="xs" justify="center" align="center" h="100%">
                <Text fw={500} ta="center" c="dimmed">
                  Nothing found.
                </Text>
                <Text size="sm" ta="center" c="dimmed">
                  No submissions has been entered yet.
                </Text>
              </Stack>
            </Card>
          : pageData.items.map((submission) => {
              const badgeDifficultyColor = (() => {
                if (submission.questionDifficulty === "Easy") {
                  return undefined;
                }
                if (submission.questionDifficulty === "Medium") {
                  return "yellow";
                }
                if (submission.questionDifficulty === "Hard") {
                  return "red";
                }
                return undefined;
              })();
              const badgeAcceptedColor = (() => {
                const acceptanceRate = submission.acceptanceRate * 100;
                if (acceptanceRate >= 75) {
                  return undefined;
                }
                if (acceptanceRate >= 50) {
                  return "yellow";
                }
                if (acceptanceRate >= 0) {
                  return "red";
                }
                return undefined;
              })();
              const LanguageIcon =
                langNameToIcon[submission.language as langNameKey] ||
                langNameToIcon["default"];
              return (
                <Card
                  key={submission.id}
                  withBorder
                  p={isMobile ? "sm" : "md"}
                  radius="md"
                  w="100%"
                  component={Link}
                  to={`/submission/${submission.id}`}
                  className="transition-all hover:brightness-110"
                >
                  <Stack gap="xs">
                    <Group justify="space-between" align="flex-start">
                      <Group gap="xs" flex={1} miw={0}>
                        <LanguageIcon
                          size={isMobile ? 20 : 22}
                          width={isMobile ? 20 : 22}
                          height={isMobile ? 20 : 22}
                        />
                        <Text
                          size={isMobile ? "sm" : undefined}
                          fw={500}
                          lh={1.3}
                          flex={1}
                        >
                          {submission.questionTitle}
                        </Text>
                      </Group>
                      <Text size="xs" c="dimmed">
                        {timeDiff(new Date(submission.submittedAt))}
                      </Text>
                    </Group>
                    <Group gap="xs" wrap="wrap">
                      <Badge size="sm" color={badgeDifficultyColor}>
                        {submission.questionDifficulty}
                      </Badge>
                      <Badge size="sm" color={badgeAcceptedColor}>
                        {Math.round(submission.acceptanceRate * 100)}%
                      </Badge>
                    </Group>
                    {submission.topics && submission.topics.length > 0 && (
                      <Group justify="space-between">
                        <Group gap="xs" wrap="wrap">
                          {submission.topics.map((topic) => (
                            <Badge
                              key={topic.id}
                              size="xs"
                              variant={isMobile ? "light" : "filled"}
                              color={isMobile ? "gray" : "gray.4"}
                            >
                              {
                                ApiUtils.getTopicEnumMetadataByTopicEnum(
                                  topic.topic,
                                ).name
                              }
                            </Badge>
                          ))}
                        </Group>
                        <Text size="sm" fw={500}>
                          {submission.pointsAwarded} Pts
                        </Text>
                      </Group>
                    )}
                    {!isMobile &&
                      !(submission.topics && submission.topics.length > 0) && (
                        <Group justify="space-between">
                          <Text size="xs" c="dimmed">
                            -
                          </Text>
                          <Text size="sm" fw={500}>
                            {submission.pointsAwarded} Pts
                          </Text>
                        </Group>
                      )}
                  </Stack>
                </Card>
              );
            })
          }
        </Stack>
      </Box>
      <Paginator
        pages={pageData?.pages ?? 0}
        currentPage={page}
        hasNextPage={pageData?.hasNextPage ?? false}
        goBack={goBack}
        goForward={goForward}
        goTo={goTo}
      />
    </Box>
  );
}
