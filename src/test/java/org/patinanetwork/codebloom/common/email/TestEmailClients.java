package org.patinanetwork.codebloom.common.email;

import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.email.client.codebloom.OfficialCodebloomEmailClient;
import org.patinanetwork.codebloom.common.email.client.github.GithubOAuthEmailClient;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.config.NoJdaRequired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestEmailClients extends NoJdaRequired {

    private final GithubOAuthEmailClient githubOAuthEmail;
    private final OfficialCodebloomEmailClient officialCodebloomEmail;

    @Autowired
    public TestEmailClients(
            final GithubOAuthEmailClient githubOAuthEmail, final OfficialCodebloomEmailClient officialCodebloomEmail) {
        this.githubOAuthEmail = githubOAuthEmail;
        this.officialCodebloomEmail = officialCodebloomEmail;
    }

    @Test
    void testConnections() throws EmailException {
        githubOAuthEmail.testConnection();
        officialCodebloomEmail.testConnection();
    }
}
