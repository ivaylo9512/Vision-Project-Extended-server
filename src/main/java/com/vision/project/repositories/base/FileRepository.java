package com.vision.project.repositories.base;

import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    @Query("from File where resource_type LIKE :resourceType AND owner = :owner")
    Optional<File> findByType(String resourceType, @Param("owner") UserModel owner);
}
