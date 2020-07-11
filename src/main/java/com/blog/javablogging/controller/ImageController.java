package com.blog.javablogging.controller;

import com.blog.javablogging.exceptions.ResourceNotFoundException;
import com.blog.javablogging.model.Blog;
import com.blog.javablogging.model.Image;
import com.blog.javablogging.service.BlogService;
import com.blog.javablogging.service.ImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;
    private final BlogService blogService;

    public ImageController(ImageService imageService, BlogService blogService) {
        this.imageService = imageService;
        this.blogService = blogService;
    }

    @PostMapping
    public ResponseEntity<Image> uploadImage(
            @RequestPart(value = "file", required = true)MultipartFile file,
            @RequestParam(name = "blogId", required = true) Integer id
            ) throws Exception {
        Blog blog = this.blogService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Resource could not be located"));
        Image image = this.imageService.uploadToS3Bucket(file, id);
        image.setBlog(blog);
        blog.setImage(image);
        this.blogService.update(blog);
        Image updatedImage = this.imageService.update(image);
        return ResponseEntity.ok().body(updatedImage);
    }

    @GetMapping("/{blogId}")
    public ResponseEntity<Image> getImageById(@PathVariable("blogId") Integer id) throws ResourceNotFoundException {
        Image image = this.imageService.findImageByBlogId(id).orElseThrow(() -> new ResourceNotFoundException("Resource could not be located"));
        return ResponseEntity.ok().body(image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(
            @PathVariable("id") Integer id,
            @RequestParam(name = "imageURL", required = true) String imageUrl
            )
    {
        this.imageService.deleteFileFromS3Bucket(imageUrl, id);
        return ResponseEntity.ok().build();
    }


}
