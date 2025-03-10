package com.patina.codebloom.website.leetcode.repo;

import java.util.ArrayList;

import com.patina.codebloom.website.leetcode.models.POTD;

public interface POTDRepository {
    POTD createPOTD(POTD potd);

    POTD getPOTDById(String id);

    POTD getCurrentPOTD();

    ArrayList<POTD> getAllPOTDS();

    void updatePOTD(POTD potd);

    void deletePOTD(String id);
}
