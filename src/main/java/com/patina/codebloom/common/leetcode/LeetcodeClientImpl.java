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

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.leetcode.models.Lang;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.common.leetcode.queries.GetPotd;
import com.patina.codebloom.common.leetcode.queries.GetSubmissionDetails;
import com.patina.codebloom.common.leetcode.queries.GetUserProfile;
import com.patina.codebloom.common.leetcode.queries.SelectAcceptedSubmisisonsQuery;
import com.patina.codebloom.common.leetcode.queries.SelectProblemQuery;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

@Component
@Primary
public class LeetcodeClientImpl implements LeetcodeClient {
    private static final String GRAPHQL_ENDPOINT = "https://leetcode.com/graphql";

    private final HttpClient client;
    private final LeetcodeAuthStealer leetcodeAuthStealer;

    public LeetcodeClientImpl(final LeetcodeAuthStealer leetcodeAuthStealer) {
        this.client = HttpClient.newHttpClient();
        this.leetcodeAuthStealer = leetcodeAuthStealer;
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
                        .header("Cookie", "LEETCODE_SESSION=" + leetcodeAuthStealer.getCookie());
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
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(body);

            int questionId = node.path("data").path("question").path("questionId").asInt();
            String questionTitle = node.path("data").path("question").path("title").asText();
            String titleSlug = node.path("data").path("question").path("titleSlug").asText();
            String link = "https://leetcode.com/problems/" + titleSlug;
            String difficulty = node.path("data").path("question").path("difficulty").asText();
            String question = node.path("data").path("question").path("content").asText();
            String statsJson = node.path("data").path("question").path("stats").asText();
            JsonObject stats = JsonParser.parseString(statsJson).getAsJsonObject();
            String acRateString = stats.get("acRate").getAsString();
            float acRate = Float.parseFloat(acRateString.replace("%", "")) / 100f;

            return new LeetcodeQuestion(link, questionId, questionTitle, titleSlug, difficulty, question, acRate);
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
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            ObjectMapper mapper = new ObjectMapper();
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

            System.out.println(response.headers().allValues("Set-Cookie"));

            if (statusCode != 200) {
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            ObjectMapper mapper = new ObjectMapper();
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
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            ObjectMapper mapper = new ObjectMapper();
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
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            ObjectMapper mapper = new ObjectMapper();
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

}
