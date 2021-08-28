package com.vision.project.services.base;

import com.vision.project.models.File;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    Resource loadFileAsResource(String fileName);

    File update(MultipartFile file, String name, long id);

    File create(MultipartFile receivedFile, String name);
}