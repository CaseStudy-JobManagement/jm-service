package com.optile.cs.job.service;

import com.optile.cs.error.AppErrorCode;
import com.optile.cs.error.AppException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

    @Value("${app.jobStore}")
    private String jobStore;

    @PostConstruct
    private void setupStore() throws IOException {
        if (!Files.isDirectory(Paths.get(jobStore)))
            Files.createDirectories(Paths.get(jobStore));
    }

    public String saveFile(MultipartFile file, String jobId) {
        if (file.isEmpty())
            throw new AppException(AppErrorCode.ERROR_EMPTY_JOB_FILE);

        String fileLocation = this.getJobStorePath(jobId, FilenameUtils.getExtension(file.getOriginalFilename()));
        try {
            Files.copy(file.getInputStream(), Paths.get(fileLocation),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new AppException(AppErrorCode.ERROR_SAVE_JOB_FILE);
        }

        return fileLocation;
    }

    private String getJobStorePath(String jobId, String fileExtension) {
        return jobStore + "/" + jobId + "." + fileExtension;
    }
}