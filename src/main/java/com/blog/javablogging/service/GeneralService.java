package com.blog.javablogging.service;

import java.util.List;
import java.util.Optional;

public interface GeneralService<T> {

    T create(T t);
    List<T> getAll();
    Optional<T> getById(Integer id);
    T update(T t);
    void deleteById(Integer id);
}
