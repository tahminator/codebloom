package com.patina.codebloom.scheduled.questionBank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;
import com.patina.codebloom.common.db.repos.question.questionbank.QuestionBankRepository;
import com.patina.codebloom.common.db.repos.task.BackgroundTaskRepository;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.leetcode.LeetcodeClient;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.scheduled.leetcode.FetchAllLeetcodeQuestions;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class FetchAllLeetcodeQuestionsTest {

    private BackgroundTaskRepository backgroundTaskRepository;
    private LeetcodeClient leetcodeClient;
    private QuestionBankRepository questionBankRepository;
    private Env env;

    private FetchAllLeetcodeQuestions job;

    @BeforeEach
    void setup() {
        backgroundTaskRepository = mock(BackgroundTaskRepository.class);
        leetcodeClient = mock(LeetcodeClient.class);
        questionBankRepository = mock(QuestionBankRepository.class);
        env = mock(Env.class);

        job = new FetchAllLeetcodeQuestions(
            backgroundTaskRepository,
            leetcodeClient,
            questionBankRepository,
            env
        );

        BackgroundTask lastSync = BackgroundTask.builder()
            .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
            .completedAt(OffsetDateTime.now().minusHours(20))
            .build();

        when(
            backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(
                BackgroundTaskEnum.LEETCODE_QUESTION_BANK
            )
        ).thenReturn(lastSync);
    }

    @Test
    void testSyncCreatesAndDeletesQuestions() {
        LeetcodeQuestion q1 = LeetcodeQuestion.builder()
            .titleSlug("two-sum")
            .questionTitle("Two Sum")
            .questionId(1)
            .difficulty("Easy")
            .link("l1")
            .acceptanceRate(50.0f)
            .build();

        LeetcodeQuestion q2 = LeetcodeQuestion.builder()
            .titleSlug("add-two-numbers")
            .questionTitle("Add Two Numbers")
            .questionId(2)
            .difficulty("Medium")
            .link("l2")
            .acceptanceRate(33.0f)
            .build();

        when(leetcodeClient.getAllProblems()).thenReturn(List.of(q1, q2));

        QuestionBank existing = QuestionBank.builder()
            .id("id-existing")
            .questionSlug("two-sum")
            .build();

        QuestionBank outdated = QuestionBank.builder()
            .id("id-outdated")
            .questionSlug("old-question")
            .build();

        when(questionBankRepository.getAllQuestions()).thenReturn(
            List.of(existing, outdated)
        );

        job.updateQuestionBank();

        ArgumentCaptor<QuestionBank> createCaptor = ArgumentCaptor.forClass(
            QuestionBank.class
        );
        verify(questionBankRepository, times(1)).createQuestion(
            createCaptor.capture()
        );
        assertEquals(
            "add-two-numbers",
            createCaptor.getValue().getQuestionSlug()
        );

        verify(questionBankRepository, times(1)).deleteQuestionById(
            "id-outdated"
        );

        ArgumentCaptor<BackgroundTask> taskCaptor = ArgumentCaptor.forClass(
            BackgroundTask.class
        );
        verify(backgroundTaskRepository, times(1)).createBackgroundTask(
            taskCaptor.capture()
        );
        assertEquals(
            BackgroundTaskEnum.LEETCODE_QUESTION_BANK,
            taskCaptor.getValue().getTask()
        );
    }
}
