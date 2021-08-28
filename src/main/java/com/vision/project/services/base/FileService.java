package com.vision.project.services.base;

import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    boolean delete(String fileName, UserModel loggedUser);

    Resource getAsResource(String fileName);

    File update(MultipartFile file, String name, long id, String type);

    File create(MultipartFile receivedFile, String name, String type, UserModel owner);

    File findByName(String fileName);
}