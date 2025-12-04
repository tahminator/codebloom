package com.patina.codebloom.db.potd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class POTDRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private POTDRepository potdRepository;

    private POTD testPOTD;

    @BeforeEach
    void setup() {
        POTD potd = POTD.builder()
                .title("Test Title")
                .slug("test-title")
                .multiplier(2.0f)
                .createdAt(StandardizedLocalDateTime.now())
                .build();
        testPOTD = potdRepository.createPOTD(potd);
    }

    @AfterEach
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
        assertTrue(testPOTD.equals(fetched));
    }

    @Test
    @Order(3)
    void testGetCurrentPOTD() {
        POTD current = potdRepository.getCurrentPOTD();
        assertNotNull(current);
        assertTrue(testPOTD.equals(current));
    }

    @Test
    @Order(4)
    void testGetAllPOTDS() {
        List<POTD> all = potdRepository.getAllPOTDS();
        assertNotNull(all);
        assertTrue(all.contains(testPOTD));
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
