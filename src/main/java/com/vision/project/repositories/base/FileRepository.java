package com.vision.project.repositories.base;

import com.vision.project.models.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("from File where resource_type LIKE :resourceType AND owner LIKE :owner")
    File findByName(String resourceType, long owner);
}
