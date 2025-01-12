package com.patina.codebloom.common.db.repos.metadata;

import com.patina.codebloom.common.db.models.Metadata;

/**
 * 
 */
public interface MetadataRepository {
    Metadata getMetadataById(String id);
}