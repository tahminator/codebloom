package com.patina.codebloom.db;

import com.patina.codebloom.common.email.client.codebloom.OfficialCodebloomEmail;
import com.patina.codebloom.common.email.client.github.GithubOAuthEmail;
import com.patina.codebloom.jda.JDAInitializer;
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
    private OfficialCodebloomEmail codebloomEmail;

    @MockitoBean
    private GithubOAuthEmail githubOAuthEmail;
}
