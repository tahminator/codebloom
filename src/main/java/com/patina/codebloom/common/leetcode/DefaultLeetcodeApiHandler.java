package com.patina.codebloom.common.leetcode;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.queries.SelectProblemQuery;

import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;

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

        System.out.println(requestBody);

        try {
            RequestSpecification reqSpec = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Referer", "https://leetcode.com")
                    .body(requestBody);
            Response response = reqSpec.post(endpoint);
            try {

                System.out.println(response.getBody().asPrettyString());
            } catch (Exception e) {
                System.out.println("Error printing response body");
            }

            JsonPath jsonPath = response.jsonPath();

            String link = jsonPath.getString("link");
            int questionId = jsonPath.getInt("questionId");
            String questionTitle = jsonPath.getString("questionTitle");
            String titleSlug = jsonPath.getString("titleSlug");
            String difficulty = jsonPath.getString("difficulty");
            String question = jsonPath.getString("question");

            return new LeetcodeQuestion(link, questionId, questionTitle, titleSlug, difficulty, question);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API");
        }
    }

    @Override
    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(String username) {
        String endpoint = "https://alfa-leetcode-api.onrender.com/" + username + "/submission";
        try {
            RequestSpecification reqSpec = RestAssured.given();
            Response response = reqSpec.get(endpoint);

            int statusCode = response.getStatusCode();
            String body = response.getBody().asString();

            if (statusCode != 200) {
                throw new RuntimeException("API Returned stauts " + statusCode + ": " + body);
            }

            JsonPath jsonPath = response.jsonPath();

            List<Map<String, Object>> submissionsList = jsonPath.getList("submission");
            if (submissionsList == null || submissionsList.isEmpty()) {
                System.out.println("No submissions found.");
                return new ArrayList<>();
            }

            ArrayList<LeetcodeSubmission> submissions = new ArrayList<>();
            for (int i = 0; i < submissionsList.size(); i++) {
                String title = jsonPath.getString("submission[" + i + "].title");
                String titleSlug = jsonPath.getString("submission[" + i + "].titleSlug");
                String timestampString = jsonPath.getString("submission[" + i + "].timestamp");
                long epochSeconds = Long.parseLong(timestampString);
                Instant instant = Instant.ofEpochSecond(epochSeconds);

                LocalDateTime timestamp = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                String statusDisplay = jsonPath.getString("submission[" + i + "].statusDisplay");
                submissions.add(new LeetcodeSubmission(title, titleSlug, timestamp, statusDisplay));
            }

            return submissions;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching the API", e);
        }
    }

}
