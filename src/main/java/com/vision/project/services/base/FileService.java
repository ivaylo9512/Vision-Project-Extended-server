package com.vision.project.services.base;

import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileNotFoundException;

public interface FileService {
    boolean delete(String resourceType, UserModel ownerId, UserModel loggedUser);

    Resource getAsResource(String fileName) throws FileNotFoundException;

    File findByName(String resourceType, UserModel ownerId);

    void save(String name, MultipartFile receivedFile);

    File generate(MultipartFile receivedFile, String resourceType, String fileType);
}