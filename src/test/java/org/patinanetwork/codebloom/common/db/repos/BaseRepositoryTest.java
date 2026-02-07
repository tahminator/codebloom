package org.patinanetwork.codebloom.common.db.repos;

import org.patinanetwork.codebloom.common.email.client.codebloom.OfficialCodebloomEmail;
import org.patinanetwork.codebloom.common.email.client.github.GithubOAuthEmail;
import org.patinanetwork.codebloom.jda.JDAClientManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Some clients are slow and are not required to be loaded for database integration tests.
 *
 * <p>All database tests must extend this class.
 */
public class BaseRepositoryTest {

    @MockitoBean
    private JDAClientManager jdaInitializer;

    @MockitoBean
    private OfficialCodebloomEmail codebloomEmail;

    @MockitoBean
    private GithubOAuthEmail githubOAuthEmail;
}
