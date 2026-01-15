package org.patinanetwork.codebloom.scheduled.potd;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.patinanetwork.codebloom.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codebloom.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codebloom.common.leetcode.models.POTD;
import org.patinanetwork.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import org.patinanetwork.codebloom.common.time.StandardizedLocalDateTime;

public class PotdSetterTest {
    private final ThrottledLeetcodeClient leetcodeClient = mock(ThrottledLeetcodeClient.class);
    private final POTDRepository potdRepository = mock(POTDRepository.class);

    private final PotdSetter potdSetter = new PotdSetter(leetcodeClient, potdRepository);

    @Test
    void testPotdSetterSetPotdWherePotdIsNull() {
        when(leetcodeClient.getPotd()).thenReturn(null);

        potdSetter.setPotd();

        verify(potdRepository, never()).getCurrentPOTD();
        verify(potdRepository, never()).createPOTD(any());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundButStillCurrentPotd() {
        POTD potd = new POTD("Example title", "Example slug", QuestionDifficulty.Easy);
        org.patinanetwork.codebloom.common.db.models.potd.POTD dbPotd = org.patinanetwork
                .codebloom
                .common
                .db
                .models
                .potd
                .POTD
                .builder()
                .createdAt(StandardizedLocalDateTime.now())
                .id(UUID.randomUUID().toString())
                .multiplier(1.3f)
                .slug(potd.getTitleSlug())
                .title(potd.getTitle())
                .build();

        when(leetcodeClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(dbPotd);

        potdSetter.setPotd();

        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, never()).createPOTD(any());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundButNoCurrentPotdYet() {
        POTD potd = new POTD("Example title", "Example slug", QuestionDifficulty.Easy);
        when(leetcodeClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(null);

        potdSetter.setPotd();

        ArgumentCaptor<org.patinanetwork.codebloom.common.db.models.potd.POTD> potdCaptor = ArgumentCaptor.captor();
        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, times(1)).createPOTD(potdCaptor.capture());

        var dbPotd = potdCaptor.getValue();
        assertNotNull(dbPotd);
        assertEquals(potd.getTitle(), dbPotd.getTitle());
        assertEquals(potd.getTitleSlug(), dbPotd.getSlug());
        assertEquals(potd.getDifficulty(), potd.getDifficulty());
    }

    @Test
    void testPotdSetterSetPotdWherePotdIsFoundAndDoesntMatchOldPotd() {
        POTD potd = new POTD("Example title", "Example slug", QuestionDifficulty.Easy);
        org.patinanetwork.codebloom.common.db.models.potd.POTD oldDbPotd = org.patinanetwork
                .codebloom
                .common
                .db
                .models
                .potd
                .POTD
                .builder()
                .createdAt(StandardizedLocalDateTime.now())
                .id(UUID.randomUUID().toString())
                .multiplier(1.3f)
                .slug("old slug")
                .title("old title")
                .build();

        when(leetcodeClient.getPotd()).thenReturn(potd);
        when(potdRepository.getCurrentPOTD()).thenReturn(oldDbPotd);

        potdSetter.setPotd();

        ArgumentCaptor<org.patinanetwork.codebloom.common.db.models.potd.POTD> potdCaptor = ArgumentCaptor.captor();
        verify(potdRepository, times(1)).getCurrentPOTD();
        verify(potdRepository, times(1)).createPOTD(potdCaptor.capture());

        var dbPotd = potdCaptor.getValue();
        assertNotNull(dbPotd);
        assertEquals(potd.getTitle(), dbPotd.getTitle());
        assertEquals(potd.getTitleSlug(), dbPotd.getSlug());
        assertEquals(potd.getDifficulty(), potd.getDifficulty());
    }
}
