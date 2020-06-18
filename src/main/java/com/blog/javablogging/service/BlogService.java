package com.blog.javablogging.service;

import com.blog.javablogging.model.Blog;
import org.springframework.data.domain.Page;

public interface BlogService extends GeneralService<Blog> {
    Page<Blog> getBlogByPage(Integer pageNumber, Integer pageSize);
}
