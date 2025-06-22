package com.patina.codebloom.potd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class POTDRepositoryTest {

    @Autowired
    private POTDRepository potdRepository;

    private POTD testPOTD;

    @BeforeAll
    void setup() {
        POTD potd = POTD.builder()
                        .title("Test Title")
                        .slug("test-title")
                        .multiplier(2.0f)
                        .createdAt(LocalDateTime.now())
                        .build();
        testPOTD = potdRepository.createPOTD(potd);
    }

    @AfterAll
    void deleteTestPOTD() {
        if (testPOTD != null && testPOTD.getId() != null) {
            potdRepository.deletePOTD(testPOTD.getId());
        }
    }

    @Test
    @Order(1)
    void testCreatePOTD() {
        assertNotNull(testPOTD);
        assertNotNull(testPOTD.getId());
        assertEquals("Test Title", testPOTD.getTitle());
        assertEquals("test-title", testPOTD.getSlug());
    }

    @Test
    @Order(2)
    void testGetPOTDById() {
        POTD fetched = potdRepository.getPOTDById(testPOTD.getId());
        assertNotNull(fetched);
        assertEquals(testPOTD.getId(), fetched.getId());
    }

    @Test
    @Order(3)
    void testGetCurrentPOTD() {
        POTD current = potdRepository.getCurrentPOTD();
        assertNotNull(current);
        assertEquals(testPOTD.getId(), current.getId());
    }

    @Test
    @Order(4)
    void testGetAllPOTDS() {
        ArrayList<POTD> all = potdRepository.getAllPOTDS();
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(p -> p.getId().equals(testPOTD.getId())));
    }

    @Test
    @Order(5)
    void testUpdatePOTD() {
        testPOTD.setTitle("Updated Title");
        potdRepository.updatePOTD(testPOTD);
        POTD updated = potdRepository.getPOTDById(testPOTD.getId());
        assertEquals("Updated Title", updated.getTitle());
    }
}
