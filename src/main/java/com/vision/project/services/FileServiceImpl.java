package com.vision.project.services;

import com.vision.project.exceptions.FileNotFoundUncheckedException;
import com.vision.project.exceptions.FileStorageException;
import com.vision.project.models.File;
import com.vision.project.services.base.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService {
    private final Path fileLocation;

    public FileServiceImpl() {
        this.fileLocation = Paths.get("./uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception e) {
            throw new FileStorageException("Couldn't create directory");
        }
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundUncheckedException("File not found");
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundUncheckedException("File not found " + e);
        }
    }

    @Override
    public File create(MultipartFile receivedFile, String name) {

        File file = generate(receivedFile, name);

        try {
            if (!file.getType().startsWith("image/")) {
                throw new FileFormatException("File should be of type IMAGE.");
            }

            save(file, receivedFile);

            return file;

        } catch (IOException e) {
            throw new FileStorageException("Couldn't store the image.");
        }
    }
}
