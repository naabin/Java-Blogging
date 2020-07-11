package com.blog.javablogging.service;

import com.blog.javablogging.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface ImageService extends GeneralService<Image> {

    public Image uploadToS3Bucket(MultipartFile file, Integer blogId) throws Exception;
    public void deleteFileFromS3Bucket(String fileName, Integer id);
    Optional<Image> findImageByBlogId(Integer id);

}
