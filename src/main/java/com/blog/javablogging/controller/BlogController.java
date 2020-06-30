package com.blog.javablogging.controller;

import com.blog.javablogging.exceptions.ResourceNotFoundException;
import com.blog.javablogging.model.Blog;
import com.blog.javablogging.model.User;
import com.blog.javablogging.service.BlogService;
import com.blog.javablogging.service.UserService;
import com.sun.mail.iap.Response;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService blogService;
    private final UserService userService;
    public BlogController(BlogService blogService, UserService userService) {
        this.blogService = blogService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> getBlogByPage(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0")Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        Page<Blog> blogByPage = this.blogService.getBlogByPage(pageNumber, pageSize);
        return ResponseEntity.ok().body(blogByPage);
    }

    @GetMapping("/published")
    public ResponseEntity<Page<Blog>> getPublishedBlog(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize
    ) {
        Page<Blog> blogPage = this.blogService.getPublishedBlogByPage(pageNumber, pageSize);
        return ResponseEntity.ok().body(blogPage);
    }

    @PostMapping
    public ResponseEntity<?> createBlog(
            @RequestParam(value = "userId", required = true)Integer userId,
            @RequestBody @Valid Blog blog) throws ResourceNotFoundException {
        Optional<User> user = this.userService.getById(userId);
        user.ifPresent(blog::setUser);
        Blog createdBlog = this.blogService.create(blog);
        if (createdBlog != null){
            return ResponseEntity.ok().body(createdBlog);
        } else {
            throw new ResourceNotFoundException("Something went wrong while creating blog post");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlogById(@PathVariable("id") Integer blogId) throws ResourceNotFoundException {
        Blog blog = this.blogService.getById(blogId).orElseThrow(() -> new ResourceNotFoundException("Could not find the resource" +
                "with an id" + blogId));
        return ResponseEntity.ok().body(blog);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBlog(@PathVariable("id") Integer id, @RequestBody Blog blog) throws ResourceNotFoundException {
        Blog updatingBlog = this.blogService.getById(id).orElseThrow(() -> new ResourceNotFoundException("Could not locate the resource."));
        updatingBlog.setContent(blog.getContent());
        updatingBlog.setTitle(blog.getTitle());
        updatingBlog.setTags(blog.getTags());
        updatingBlog.setPublish(blog.isPublished());
        Blog updatedBlog = this.blogService.update(updatingBlog);
        return ResponseEntity.ok().body(updatedBlog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable("id") Integer id){
        this.blogService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
