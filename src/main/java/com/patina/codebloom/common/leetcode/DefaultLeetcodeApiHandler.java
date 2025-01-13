package com.patina.codebloom.common.leetcode;

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

import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;

@Component
public class DefaultLeetcodeApiHandler implements LeetcodeApiHandler {

    @Override
    public LeetcodeQuestion findQuestionBySlug(String slug) {
        String endpoint = "https://alfa-leetcode-api.onrender.com/select?titleSlug=" + slug;
        try {
            RequestSpecification reqSpec = RestAssured.given();
            Response response = reqSpec.get(endpoint);

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
            System.out.println("Response status code: " + statusCode);
            System.out.println("Response body: " + body);

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

                System.out.println("Timestamp string = " + timestampString);

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
