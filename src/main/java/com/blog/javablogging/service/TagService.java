package com.blog.javablogging.service;

import com.blog.javablogging.model.Tag;

public interface TagService extends GeneralService<Tag> {

    Boolean findByTagName(String name);
}
