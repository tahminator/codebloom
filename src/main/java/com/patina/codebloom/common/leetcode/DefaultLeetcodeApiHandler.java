package com.patina.codebloom.common.leetcode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.leetcode.models.Lang;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.queries.GetPotd;
import com.patina.codebloom.common.leetcode.queries.GetSubmissionDetails;
import com.patina.codebloom.common.leetcode.queries.SelectAcceptedSubmisisonsQuery;
import com.patina.codebloom.common.leetcode.queries.SelectProblemQuery;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Component
public class DefaultLeetcodeApiHandler implements LeetcodeApiHandler {

    public static String buildQuestionRequestBody(String query, String slug) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> variables = new HashMap<>();
        variables.put("titleSlug", slug);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", query);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }

    public static String buildAcceptedSubmissionsRequestBody(String query, String username)
            throws JsonProcessingException {
        // API doesn't let you get more than this amount.
        int limit = 20;

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> variables = new HashMap<>();
        variables.put("username", username);
        variables.put("limit", limit);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", query);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }

    public static String buildGetSubmissionDetailRequestBody(String query, int submissionId)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Integer> variables = new HashMap<>();
        variables.put("submissionId", submissionId);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", query);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);

    }

    public static String buildPotdRequestBody(String query) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", query);

        return objectMapper.writeValueAsString(requestBodyMap);
    }

    @Override
    public LeetcodeQuestion findQuestionBySlug(String slug) {
        String endpoint = "https://leetcode.com/graphql";
        String query = SelectProblemQuery.query;

        String requestBody;
        try {
            requestBody = buildQuestionRequestBody(query, slug);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            RequestSpecification reqSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://leetcode.com")
                    .body(requestBody);

            Response response = reqSpec.post(endpoint);

            int statusCode = response.getStatusCode();
            String body = response.getBody().asString();

            if (statusCode != 200) {
                throw new RuntimeException("API Returned status " + statusCode + ": " + body);
            }

            JsonPath jsonPath = response.jsonPath();

            int questionId = jsonPath.getInt("data.question.questionId");
            String questionTitle = jsonPath.getString("data.question.title");
            String titleSlug = jsonPath.getString("data.question.titleSlug");
            String link = "https://leetcode.com/problems/" + titleSlug;
            String difficulty = jsonPath.getString("data.question.difficulty");
            String question = jsonPath.getString("data.question.content");

            String statsJson = jsonPath.getString("data.question.stats");
            JsonObject stats = JsonParser.parseString(statsJson).getAsJsonObject();
            String acRateString = stats.get("acRate").getAsString();
            float acRate = Float.parseFloat(acRateString.replace("%", "")) / 100f;

            return new LeetcodeQuestion(link, questionId, questionTitle, titleSlug, difficulty, question, acRate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(String username) {
        ArrayList<LeetcodeSubmission> submissions = new ArrayList<>();

        String endpoint = "https://leetcode.com/graphql";
        String query = SelectAcceptedSubmisisonsQuery.query;

        String requestBody;
        try {
            requestBody = buildAcceptedSubmissionsRequestBody(query, username);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            RequestSpecification reqSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://leetcode.com")
                    .body(requestBody);
            Response response = reqSpec.post(endpoint);

            JsonPath jsonPath = response.jsonPath();

            List<Map<String, Object>> submissionsList = jsonPath.getList("data.recentAcSubmissionList");
            if (submissionsList == null || submissionsList.isEmpty()) {
                return submissions;
            }

            for (int i = 0; i < submissionsList.size(); i++) {
                int id = jsonPath.getInt("data.recentAcSubmissionList[" + i + "].id");
                String title = jsonPath.getString("data.recentAcSubmissionList[" + i + "].title");
                String titleSlug = jsonPath.getString("data.recentAcSubmissionList[" + i + "].titleSlug");
                String timestampString = jsonPath.getString("data.recentAcSubmissionList[" + i + "].timestamp");
                long epochSeconds = Long.parseLong(timestampString);
                Instant instant = Instant.ofEpochSecond(epochSeconds);

                LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String statusDisplay = jsonPath.getString("data.recentAcSubmissionList[" + i + "].statusDisplay");
                submissions.add(new LeetcodeSubmission(id, title, titleSlug, timestamp, statusDisplay));
            }

            return submissions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

    @Override
    public LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId) {
        String endpoint = "https://leetcode.com/graphql";
        String query = GetSubmissionDetails.query;

        String requestBody;
        try {
            requestBody = buildGetSubmissionDetailRequestBody(query, submissionId);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            RequestSpecification reqSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://leetcode.com")
                    .cookie("LEETCODE_SESSION=" + LeetcodeAuthStealer.getCookie() + ";")
                    .body(requestBody);
            Response response = reqSpec.post(endpoint);

            JsonPath jsonPath = response.jsonPath();

            System.out.println(response.asPrettyString());

            int runtime = (jsonPath.get("data.submissionDetails.runtime") instanceof Integer)
                    ? jsonPath.getInt("data.submissionDetails.runtime")
                    : 0;

            String runtimeDisplay = (jsonPath.get("data.submissionDetails.runtimeDisplay") instanceof String)
                    ? jsonPath.getString("data.submissionDetails.runtimeDisplay")
                    : null;

            float runtimePercentile = (jsonPath.get("data.submissionDetails.runtimePercentile") instanceof Float)
                    ? jsonPath.getFloat("data.submissionDetails.runtimePercentile")
                    : 0.0f;

            int memory = (jsonPath.get("data.submissionDetails.memory") instanceof Integer)
                    ? jsonPath.getInt("data.submissionDetails.memory")
                    : 0;

            String memoryDisplay = (jsonPath.get("data.submissionDetails.memoryDisplay") instanceof String)
                    ? jsonPath.getString("data.submissionDetails.memoryDisplay")
                    : null;

            float memoryPercentile = (jsonPath.get("data.submissionDetails.memoryPercentile") instanceof Float)
                    ? jsonPath.getFloat("data.submissionDetails.memoryPercentile")
                    : 0.0f;

            String code = (jsonPath.get("data.submissionDetails.code") instanceof String)
                    ? jsonPath.getString("data.submissionDetails.code")
                    : null;

            String langName = (jsonPath.get("data.submissionDetails.lang.name") instanceof String)
                    ? jsonPath.getString("data.submissionDetails.lang.name")
                    : null;

            String langVerboseName = (jsonPath.get("data.submissionDetails.lang.verboseName") instanceof String)
                    ? jsonPath.getString("data.submissionDetails.lang.verboseName")
                    : null;

            Lang lang = (langName != null && langVerboseName != null)
                    ? new Lang(langName, langVerboseName)
                    : null;

            LeetcodeDetailedQuestion question = new LeetcodeDetailedQuestion(
                    runtime,
                    runtimeDisplay,
                    runtimePercentile,
                    memory,
                    memoryDisplay,
                    memoryPercentile,
                    code,
                    lang);

            return question;

        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }

    }

    public POTD getPotd() {
        String endpoint = "https://leetcode.com/graphql";
        String query = GetPotd.query;

        String requestBody;
        try {
            requestBody = buildPotdRequestBody(query);
        } catch (Exception e) {
            throw new RuntimeException("Error building the request body");
        }

        try {
            RequestSpecification reqSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://leetcode.com")
                    .body(requestBody);
            Response response = reqSpec.post(endpoint);

            JsonPath jsonPath = response.jsonPath();

            System.out.println(response.asPrettyString());

            var titleSlug = jsonPath.getString("data.activeDailyCodingChallengeQuestion.question.titleSlug");
            var title = jsonPath.getString("data.activeDailyCodingChallengeQuestion.question.title");
            var difficulty = QuestionDifficulty
                    .valueOf(jsonPath.getString("data.activeDailyCodingChallengeQuestion.question.difficulty"));

            return new POTD(title, titleSlug, difficulty);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

}
