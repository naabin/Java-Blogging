package com.blog.javablogging.service.impl;

import com.blog.javablogging.model.Tag;
import com.blog.javablogging.repository.TagRepository;
import com.blog.javablogging.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Boolean findByTagName(String name) {
        Tag tag = this.tagRepository.findByName(name);
        return !(tag != null && tag.getName().equalsIgnoreCase(name));
    }

    @Override
    public Tag create(Tag tag) {
        return null;
    }

    @Override
    public List<Tag> getAll() {
        return null;
    }

    @Override
    public Optional<Tag> getById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Tag update(Tag tag) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {

    }
}
