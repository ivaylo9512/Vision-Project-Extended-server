package com.vision.project.services.base;

import org.springframework.core.io.Resource;

public interface FileService {
    Resource loadFileAsResource(String fileName);
}