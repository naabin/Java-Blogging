package com.blog.javablogging.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.blog.javablogging.model.Image;
import com.blog.javablogging.repository.ImageRepository;
import com.blog.javablogging.service.ImageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService {

    @Value("${endpoint}")
    private String endPoint;

    @Value("${bucket}")
    private String bucket;

    @Value("${credential.access-key}")
    private String accessKey;

    @Value("${credential.secret-key}")
    private String secretKey;

    private AmazonS3 amazonS3;

    private final ImageRepository imageRepository;

    @PostConstruct
    private void amazonCredentials(){
        BasicAWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.amazonS3 = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTHEAST_2)
                .build();
    }

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Async
    @Override
    public Image uploadToS3Bucket(MultipartFile file, Integer blogId) throws Exception {
        String fileUrl = "";
        File convertedFile = convertMultiPartFile(file);
        String fileName = generateFileName(file);
        Optional<Image> imageByBlogId = this.findImageByBlogId(blogId);
        if (imageByBlogId.isPresent()){
            Image image = imageByBlogId.get();
            this.deleteFileFromS3Bucket(image.getImageURL(), image.getId());
        }
        fileUrl = this.endPoint + "/" + this.bucket + "/" + fileName;
        uploadFile(fileName, convertedFile);
        convertedFile.delete();
        Image uploadedImage = new Image();
        uploadedImage.setImageURL(fileUrl);
        return this.imageRepository.save(uploadedImage);
    }

    @Async
    @Override
    public void deleteFileFromS3Bucket(String fileName, Integer id) {
        String file = fileName.substring(fileName.lastIndexOf("/") + 1);
        this.amazonS3.deleteObject(new DeleteObjectRequest(this.bucket, file));
        this.deleteById(id);
    }

    @Override
    public Optional<Image> findImageByBlogId(Integer id) {
        return this.imageRepository.findByBlogId(id);
    }

    @Override
    public Image create(Image image) {
        return null;
    }

    @Override
    public List<Image> getAll() {
        return null;
    }

    @Override
    public Optional<Image> getById(Integer id) {
        return this.imageRepository.findById(id);
    }

    @Override
    public Image update(Image image) {
        return this.imageRepository.saveAndFlush(image);
    }

    @Override
    public void deleteById(Integer id) {
        this.imageRepository.deleteById(id);
    }

    private File convertMultiPartFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return file;
    }
    private String generateFileName(MultipartFile file){
        return new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFile(String fileName, File file){
        this.amazonS3.putObject(new PutObjectRequest(this.bucket, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
