package com.ecommerce.backend.upload;

import com.ecommerce.backend.utils.CharterUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@UtilityClass
public final class FileUpload {
    public static void uploadFile(String uploadPath, String fileName, MultipartFile multipartFile) {
        // originFile을 먼저 저장하고
        try(FileOutputStream fos = new FileOutputStream(makeAbsPath(uploadPath, fileName))){
            fos.write(multipartFile.getBytes());
        } catch (FileNotFoundException e) {
            log.error("[P1][UTIL][FILE][UPAD]: 파일을 찾을 수 없습니다. message=({})", e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("[P1][UTIL][FILE][UPAD]: IOException message=({})", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static File convertMultipartFileToFile(MultipartFile multipartFile, String resizePath) {
        createDirectoryIfNotExists(resizePath);
        final String path = FileUpload.makeAbsPath(resizePath, multipartFile.getOriginalFilename());
        final File file = new File(path);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("[P1][UTL][FILE][CMTF]: MultipartFile를 File로 바꿀 수 없습니다. message=({})", e.getMessage());
            throw new RuntimeException(e);
        }
        return file;
    }

    // 원래 파일명 + "-" + uuid + 확장자를 통해 copyFileName을 생성한다.
    public static String makeFileNameWithUuid(String originalFileName){
        return makeUuid() + CharterUtil.DASH + originalFileName;
    }

    public static String makeAbsPath(String uploadPath, String fileName){
        return uploadPath + CharterUtil.SLASH + fileName;
    }

    public static String makeThumbnailFileName(String originalFileName, int size){
        final String[] split = originalFileName.split("\\.");
        final String fileName = split[0];
        final String extension = split[1];

        return makeUuid() + CharterUtil.DASH + fileName + CharterUtil.UNDER_BAR + size + CharterUtil.DOT + extension;
    }

    public static String makeUuid() {
        return String.valueOf(UUID.randomUUID()).substring(0, 8);
    }

    public static void createDirectoryIfNotExists(String path) {
        try {
            File file = new File(path);
            if (file.exists()) return;
            Files.createDirectory(file.toPath());
            log.info("[P9][UTL][UPLD][CRTE]: ({}) 경로 생성 성공", path);
        } catch (IOException e) {
            log.error("[P1][UTL][UPLD][CRTE]: ({}) 경로 생성 실패", path);
            throw new RuntimeException(e);
        }
    }
    public static void deleteIfExists(File file) {
        final Path path = file.toPath();

        try {
            Files.deleteIfExists(path);
            log.info("[P9][UTL][UPLD][DELE]: ({}) 경로 삭제 성공", path);
        } catch (IOException e) {
            log.error("[P1][UTL][UPLD][DELE]: ({}) 경로 삭제 실패", path);
            throw new RuntimeException(e);
        }
    }
}
