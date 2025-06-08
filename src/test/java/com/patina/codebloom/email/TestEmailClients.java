package com.patina.codebloom.email;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.email.client.codebloom.OfficialCodebloomEmail;
import com.patina.codebloom.common.email.client.github.GithubOAuthEmail;
import com.patina.codebloom.common.email.error.EmailException;

@SpringBootTest
public class TestEmailClients {
    private final GithubOAuthEmail githubOAuthEmail;
    private final OfficialCodebloomEmail officialCodebloomEmail;

    @Autowired
    public TestEmailClients(final GithubOAuthEmail githubOAuthEmail, final OfficialCodebloomEmail officialCodebloomEmail) {
        this.githubOAuthEmail = githubOAuthEmail;
        this.officialCodebloomEmail = officialCodebloomEmail;
    }

    @Test
    void testConnections() throws EmailException {
        githubOAuthEmail.testConnection();
        officialCodebloomEmail.testConnection();
    }
}
