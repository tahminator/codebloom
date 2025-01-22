package com.patina.codebloom.common.leetcode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.queries.SelectProblemQuery;
import com.patina.codebloom.common.leetcode.queries.SelectSubmisisonsQuery;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

@Component
public class DefaultLeetcodeApiHandler implements LeetcodeApiHandler {

    public static String buildQuestionRequestBody(String query, String slug) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> variables = new HashMap<>();
        variables.put("titleSlug", slug);

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("query", query);
        requestBodyMap.put("variables", variables);

        return objectMapper.writeValueAsString(requestBodyMap);
    }

    public static String buildRecentSubmissionsRequestBody(String query, String username) throws Exception {
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
        String query = SelectSubmisisonsQuery.query;

        String requestBody;
        try {
            requestBody = buildRecentSubmissionsRequestBody(query, username);
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

            List<Map<String, Object>> submissionsList = jsonPath.getList("data.recentSubmissionList");
            if (submissionsList == null || submissionsList.isEmpty()) {
                return submissions;
            }

            for (int i = 0; i < submissionsList.size(); i++) {
                String title = jsonPath.getString("data.recentSubmissionList[" + i + "].title");
                String titleSlug = jsonPath.getString("data.recentSubmissionList[" + i + "].titleSlug");
                String timestampString = jsonPath.getString("data.recentSubmissionList[" + i + "].timestamp");
                long epochSeconds = Long.parseLong(timestampString);
                Instant instant = Instant.ofEpochSecond(epochSeconds);

                LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String statusDisplay = jsonPath.getString("data.recentSubmissionList[" + i + "].statusDisplay");
                submissions.add(new LeetcodeSubmission(title, titleSlug, timestamp, statusDisplay));
            }

            return submissions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

}
