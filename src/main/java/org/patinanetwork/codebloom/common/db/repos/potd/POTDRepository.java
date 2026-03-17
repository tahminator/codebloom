package org.patinanetwork.codebloom.common.db.repos.potd;

import java.util.ArrayList;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.potd.POTD;

public interface POTDRepository {
    POTD createPOTD(POTD potd);

    Optional<POTD> getPOTDById(String id);

    Optional<POTD> getCurrentPOTD();

    ArrayList<POTD> getAllPOTDS();

    void updatePOTD(POTD potd);

    void deletePOTD(String id);
}
