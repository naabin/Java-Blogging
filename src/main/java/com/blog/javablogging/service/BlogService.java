package com.blog.javablogging.service;

import com.blog.javablogging.model.Blog;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface BlogService extends GeneralService<Blog> {
    Page<Blog> getBlogByPage(Integer pageNumber, Integer pageSize);
    Page<Blog> getPublishedBlogByPage(Integer pageNumber, Integer pageSize);
    Optional<Blog> getBlogByTitle(String title);
}
