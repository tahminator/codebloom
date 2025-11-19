package com.patina.codebloom.common.leetcode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.leetcode.models.Lang;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.queries.GetAllProblems;
import com.patina.codebloom.common.leetcode.queries.GetPotd;
import com.patina.codebloom.common.leetcode.queries.GetSubmissionDetails;
import com.patina.codebloom.common.leetcode.queries.GetTopics;
import com.patina.codebloom.common.leetcode.queries.GetUserProfile;
import com.patina.codebloom.common.leetcode.queries.SelectAcceptedSubmisisonsQuery;
import com.patina.codebloom.common.leetcode.queries.SelectProblemQuery;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

/**
 * TODO: Add an input to determine whether the request must be processed quickly
 * or not.
 */
@Component
@Primary
public class LeetcodeClientImpl implements LeetcodeClient {
    private static final String GRAPHQL_ENDPOINT = "https://leetcode.com/graphql";

    private final ObjectMapper mapper;

    private final HttpClient client;
    private final LeetcodeAuthStealer leetcodeAuthStealer;
    private final Reporter reporter;

    public LeetcodeClientImpl(final LeetcodeAuthStealer leetcodeAuthStealer, final Reporter reporter) {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();

        this.leetcodeAuthStealer = leetcodeAuthStealer;
        this.reporter = reporter;
    }

    private String buildCookieHeader() {
        List<String> cookies = new ArrayList<>();

        String session = leetcodeAuthStealer.getCookie();
        cookies.add("LEETCODE_SESSION=" + session);

        String csrf = leetcodeAuthStealer.getCsrf();
        if (csrf != null) {
            cookies.add("csrftoken=" + csrf);
        }

        return String.join("; ", cookies);
    }

    /**
     * leetcode.com does not always throw correct errors, so this is our best guess
     * for what a throttled response status could be.
     *
     */
    private boolean isThrottled(final int statusCode) {
        // 5xx errors
        if (statusCode >= 500) {
            return true;
        }

        // not 100% sure, but thrown consistently from cf
        if (statusCode == 302) {
            return true;
        }

        // cf error
        if (statusCode == 403) {
            return true;
        }

        return false;
    }

    /**
     *
     * Returns {@link HttpRequest.Builder} with some defaults required to interface
     * with leetcode.com
     * 
     */
    private HttpRequest.Builder getGraphQLRequestBuilder() {
        return HttpRequest.newBuilder()
                        .uri(URI.create(GRAPHQL_ENDPOINT))
                        .header("Content-Type", "application/json")
                        .header("Referer", "https://leetcode.com")
                        .header("Cookie", buildCookieHeader());
    }

    @Override
    public LeetcodeQuestion findQuestionBySlug(final String slug) {

        String requestBody;
        try {
            requestBody = SelectProblemQuery.body(slug);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(requestBody))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to find question by slug due to status code of %d

                                                    Slug: %s

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, slug, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonNode node = mapper.readTree(body);

            int questionId = node.path("data").path("question").path("questionId").asInt();
            String questionTitle = node.path("data").path("question").path("title").asText();
            String titleSlug = node.path("data").path("question").path("titleSlug").asText();
            String link = "https://leetcode.com/problems/" + titleSlug;
            String difficulty = node.path("data").path("question").path("difficulty").asText();
            String question = node.path("data").path("question").path("content").asText();

            String statsJson = node.path("data").path("question").path("stats").asText();
            JsonNode stats = mapper.readTree(statsJson);
            String acRateString = stats.get("acRate").asText();
            float acRate = Float.parseFloat(acRateString.replace("%", "")) / 100f;

            JsonNode topicTagsNode = node.path("data").path("question").path("topicTags");

            List<LeetcodeTopicTag> tags = new ArrayList<>();

            for (JsonNode el : topicTagsNode) {
                tags.add(LeetcodeTopicTag.builder()
                                .name(el.get("name").asText())
                                .slug(el.get("slug").asText())
                                .build());
            }

            return LeetcodeQuestion.builder()
                            .link(link)
                            .questionId(questionId)
                            .questionTitle(questionTitle)
                            .titleSlug(titleSlug)
                            .difficulty(difficulty)
                            .question(question)
                            .acceptanceRate(acRate)
                            .topics(tags)
                            .build();

        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(final String username) {
        ArrayList<LeetcodeSubmission> submissions = new ArrayList<>();

        String requestBody;
        try {
            requestBody = SelectAcceptedSubmisisonsQuery.body(username);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(requestBody))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to find submission by username due to status code of %d

                                                    Username: %s

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, username, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonNode node = mapper.readTree(body);
            JsonNode submissionsNode = node.path("data").path("recentAcSubmissionList");

            if (submissionsNode.isArray()) {

                if (submissionsNode.isEmpty() || submissionsNode == null) {
                    return submissions;
                }

                for (JsonNode submission : submissionsNode) {
                    int id = submission.path("id").asInt();
                    String title = submission.path("title").asText();
                    String titleSlug = submission.path("titleSlug").asText();
                    String timestampString = submission.path("timestamp").asText();
                    long epochSeconds = Long.parseLong(timestampString);
                    Instant instant = Instant.ofEpochSecond(epochSeconds);

                    LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    String statusDisplay = submission.path("statusDisplay").asText();
                    submissions.add(new LeetcodeSubmission(id, title, titleSlug, timestamp, statusDisplay));

                }
            }

            return submissions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(final int submissionId) {
        String requestBody;
        try {
            requestBody = GetSubmissionDetails.body(submissionId);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(requestBody))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to find submission detail by submission ID due to status code of %d

                                                    Submission ID: %s

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, submissionId, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonNode node = mapper.readTree(body);
            JsonNode baseNode = node.path("data").path("submissionDetails");

            int runtime = baseNode.path("runtime").asInt();
            String runtimeDisplay = baseNode.path("runtimeDisplay").asText();
            float runtimePercentile = (float) baseNode.path("runtimePercentile").asDouble();
            int memory = baseNode.path("memory").asInt();
            String memoryDisplay = baseNode.path("memoryDisplay").asText();
            float memoryPercentile = (float) baseNode.path("memoryPercentile").asDouble();
            String code = baseNode.path("code").asText();
            String langName = baseNode.path("lang").path("name").asText();
            String langVerboseName = baseNode.path("lang").path("verboseName").asText();
            Lang lang = (langName != null && langVerboseName != null) ? new Lang(langName, langVerboseName) : null;

            // if any of these are empty, then extremely likely that we're throttled.
            if (Strings.isNullOrEmpty(runtimeDisplay)
                            || Strings.isNullOrEmpty(memoryDisplay)) {
                leetcodeAuthStealer.reloadCookie();
            }

            LeetcodeDetailedQuestion question = new LeetcodeDetailedQuestion(runtime, runtimeDisplay, runtimePercentile, memory, memoryDisplay, memoryPercentile, code, lang);

            return question;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }

    }

    public POTD getPotd() {
        String requestBody;
        try {
            requestBody = GetPotd.body();
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(requestBody))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to get POTD due to status code of %d

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonNode node = mapper.readTree(body);
            JsonNode baseNode = node.path("data").path("activeDailyCodingChallengeQuestion").path("question");

            String titleSlug = baseNode.path("titleSlug").asText();
            String title = baseNode.path("title").asText();
            var difficulty = QuestionDifficulty.valueOf(baseNode.path("difficulty").asText());

            return new POTD(title, titleSlug, difficulty);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public UserProfile getUserProfile(final String username) {
        String requestBody;
        try {
            requestBody = GetUserProfile.body(username);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body", e);
        }

        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(requestBody))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to get user profile by username due to status code of %d

                                                    Username: %s

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, username, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonNode node = mapper.readTree(body);
            JsonNode baseNode = node.path("data").path("matchedUser");

            var returnedUsername = baseNode.path("username").asText();
            var ranking = baseNode.path("profile").path("ranking").asText();
            var userAvatar = baseNode.path("profile").path("userAvatar").asText();
            var realName = baseNode.path("profile").path("realName").asText();
            var aboutMe = baseNode.path("profile").path("aboutMe").asText().trim();

            return new UserProfile(returnedUsername, ranking, userAvatar, realName, aboutMe);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public Set<LeetcodeTopicTag> getAllTopicTags() {
        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(GetTopics.body()))
                            .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to get all topic tags due to status code of %d

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("Non-successful response getting topics from Leetcode API. Status code: " + statusCode);
            }

            JsonNode json = mapper.readTree(body);
            JsonNode edges = json.path("data").path("questionTopicTags").path("edges");

            if (!edges.isArray()) {
                throw new RuntimeException("The expected shape of getting topics did not match the received body");
            }

            Set<LeetcodeTopicTag> result = new HashSet<>();

            for (JsonNode edge : edges) {
                JsonNode node = edge.path("node");
                result.add(LeetcodeTopicTag.builder()
                                .name(node.get("name").asText())
                                .slug(node.get("slug").asText())
                                .build());
            }

            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error getting topics from Leetcode API", e);
        }
    }

    public List<LeetcodeQuestion> getAllProblems() {
        try {
            HttpRequest request = getGraphQLRequestBuilder()
                            .POST(BodyPublishers.ofString(GetAllProblems.body()))
                            .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();
            if (statusCode != 200) {
                if (isThrottled(statusCode)) {
                    leetcodeAuthStealer.reloadCookie();
                }
                reporter.log(Report.builder()
                                .data(String.format("""
                                                    Leetcode client failed to get all leetcode questions due to status code of %d

                                                    Body: %s

                                                    Header(s): %s
                                                """, statusCode, body, response.headers().toString()))
                                .build());
                throw new RuntimeException("Non-successful response getting all questions from Leetcode API. Status code: " + statusCode);
            }

            JsonNode json = mapper.readTree(body);
            JsonNode allQuestions = json.path("data").path("problemsetQuestionListV2").path("questions");

            if (!allQuestions.isArray()) {
                throw new RuntimeException("The expected shape of getting topics did not match the received body");
            }

            List<LeetcodeQuestion> result = new ArrayList<>();
            for (JsonNode question : allQuestions) {
                JsonNode topicTags = question.get("topicTags");

                List<LeetcodeTopicTag> tags = new ArrayList<>();
                for (JsonNode tag : topicTags) {
                    tags.add(LeetcodeTopicTag.builder()
                    .name(tag.get("name").asText())
                    .slug(tag.get("slug").asText())
                    .build());
                }

                result.add(LeetcodeQuestion.builder()
                    .link("https://leetcode.com/problems/" + question.get("titleSlug").asText())
                    .questionId(question.get("questionFrontendId").asInt())
                    .questionTitle(question.get("title").asText())
                    .titleSlug(question.get("titleSlug").asText())
                    .difficulty(question.get("difficulty").asText())
                    .acceptanceRate((float) question.get("acRate").asDouble())
                    .topics(tags)
                    .build());
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error getting all problems from Leetcode API", e);
        }
    }
}
