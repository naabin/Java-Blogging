package com.blog.javablogging.service.impl;

import com.blog.javablogging.model.Blog;
import com.blog.javablogging.model.Tag;
import com.blog.javablogging.repository.BlogRepository;
import com.blog.javablogging.repository.TagRepository;
import com.blog.javablogging.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogServiceImpl implements BlogService {

    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;

    @Autowired
    public BlogServiceImpl(BlogRepository blogRepository, TagRepository tagRepository) {
        this.blogRepository = blogRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Page<Blog> getBlogByPage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        return this.blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> getPublishedBlogByPage(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return this.blogRepository.getAllByPublished(true, pageable);
    }

    @Override
    public Blog create(Blog blog) {
        for (Tag tag: blog.getTags()){
            this.tagRepository.save(tag);
        }
        return this.blogRepository.save(blog);
    }

    @Override
    public List<Blog> getAll() {
        return this.blogRepository.findAll();
    }

    @Override
    public Optional<Blog> getById(Integer id) {
        return this.blogRepository.findById(id);
    }

    @Override
    public Blog update(Blog blog) {
        return this.blogRepository.saveAndFlush(blog);
    }

    @Override
    public void deleteById(Integer id) {
        this.blogRepository.deleteById(id);
    }
}
