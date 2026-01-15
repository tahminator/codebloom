package org.patinanetwork.codebloom.common.db.repos.potd;

import java.util.ArrayList;
import org.patinanetwork.codebloom.common.db.models.potd.POTD;

public interface POTDRepository {
    POTD createPOTD(POTD potd);

    POTD getPOTDById(String id);

    POTD getCurrentPOTD();

    ArrayList<POTD> getAllPOTDS();

    void updatePOTD(POTD potd);

    void deletePOTD(String id);
}
