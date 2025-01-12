package com.patina.codebloom.submissions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patina.codebloom.common.db.models.Submission;
import com.patina.codebloom.common.db.repos.submissions.SubmissionsRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class SubmissionsService {

    private final SubmissionsRepository submissionsRepository;

    public SubmissionsService(SubmissionsRepository submissionsRepository) {
        this.submissionsRepository = submissionsRepository;
    }

    public List<Submission> fetchAndSaveSubmissions(String leetUser) {
        // use test api here?
        String apiUrl = "https://alfa-leetcode-api.onrender.com/" + leetUser + "/submission";

        String responseBody = makeHttpGetRequest(apiUrl);

        List<Submission> externalSubmissions = parseSubmissionsJson(responseBody, leetUser);

        for (Submission submission : externalSubmissions) {
            submissionsRepository.insertSubmission(submission);
        }

        return externalSubmissions;
    }

    private String makeHttpGetRequest(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed, HTTP code " + response.statusCode() + ": " + response.body());
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error fetching submissions from " + url, e);
        }
    }

    private List<Submission> parseSubmissionsJson(String json, String leetUser) {
        List<Submission> result = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            JsonNode submissionsArray = root.get("Submissions");
            if (submissionsArray != null && submissionsArray.isArray()) {
                for (JsonNode subNode : submissionsArray) {
                    Submission s = new Submission();

                    s.setLeetcodeUsername(leetUser);

                    if (subNode.hasNonNull("titleSlug")) {
                        s.setQuestionSlug(subNode.get("titleSlug").asText());
                    }

                    if (subNode.hasNonNull("timestamp")) {
                        s.setTimestamp(subNode.get("timestamp").asText());
                    }

                    if (subNode.hasNonNull("statusDisplay")) {
                        s.setStatusDisplay(subNode.get("statusDisplay").asText());
                    }

                    if (subNode.hasNonNull("lang")) {
                        s.setLang(subNode.get("lang").asText());
                    }

                    result.add(s);
                }
            }

            return result;
        } catch (IOException e) {
            throw new RuntimeException("Error parsing JSON from API", e);
        }
    }
}