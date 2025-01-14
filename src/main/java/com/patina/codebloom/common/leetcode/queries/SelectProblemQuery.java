package com.patina.codebloom.common.leetcode.queries;

public class SelectProblemQuery {
    public static String query = "#graphql\n" +
            "    query selectProblem($titleSlug: String!) {\n" +
            "        question(titleSlug: $titleSlug) {\n" +
            "            questionId\n" +
            "            questionFrontendId\n" +
            "            boundTopicId\n" +
            "            title\n" +
            "            titleSlug\n" +
            "            content\n" +
            "            translatedTitle\n" +
            "            translatedContent\n" +
            "            isPaidOnly\n" +
            "            difficulty\n" +
            "            likes\n" +
            "            dislikes\n" +
            "            isLiked\n" +
            "            similarQuestions\n" +
            "            exampleTestcases\n" +
            "            contributors {\n" +
            "                username\n" +
            "                profileUrl\n" +
            "                avatarUrl\n" +
            "            }\n" +
            "            topicTags {\n" +
            "                name\n" +
            "                slug\n" +
            "                translatedName\n" +
            "            }\n" +
            "            companyTagStats\n" +
            "            codeSnippets {\n" +
            "                lang\n" +
            "                langSlug\n" +
            "                code\n" +
            "            }\n" +
            "            stats\n" +
            "            hints\n" +
            "            solution {\n" +
            "                id\n" +
            "                canSeeDetail\n" +
            "                paidOnly\n" +
            "                hasVideoSolution\n" +
            "                paidOnlyVideo\n" +
            "            }\n" +
            "            status\n" +
            "            sampleTestCase\n" +
            "            metaData\n" +
            "            judgerAvailable\n" +
            "            judgeType\n" +
            "            mysqlSchemas\n" +
            "            enableRunCode\n" +
            "            enableTestMode\n" +
            "            enableDebugger\n" +
            "            envInfo\n" +
            "            libraryUrl\n" +
            "            adminUrl\n" +
            "            challengeQuestion {\n" +
            "                id\n" +
            "                date\n" +
            "                incompleteChallengeCount\n" +
            "                streakCount\n" +
            "                type\n" +
            "            }\n" +
            "            note\n" +
            "        }\n" +
            "    }";
}
