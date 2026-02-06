package org.patinanetwork.codebloom.common.db.repos;

import org.patinanetwork.codebloom.common.email.client.codebloom.OfficialCodebloomEmailClient;
import org.patinanetwork.codebloom.common.email.client.github.GithubOAuthEmailClient;
import org.patinanetwork.codebloom.jda.JDAInitializer;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Some clients are slow and are not required to be loaded for database integration tests.
 *
 * <p>All database tests must extend this class.
 */
public class BaseRepositoryTest {

    @MockitoBean
    private JDAInitializer jdaInitializer;

    @MockitoBean
    private OfficialCodebloomEmailClient codebloomEmail;

    @MockitoBean
    private GithubOAuthEmailClient githubOAuthEmail;
}
