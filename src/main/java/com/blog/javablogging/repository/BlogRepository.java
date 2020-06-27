package com.blog.javablogging.repository;

import com.blog.javablogging.model.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {

    @Query(value="select * from blog where published=?1 order by created_date", nativeQuery=true)
    Page<Blog> getAllByPublished(Boolean published,  Pageable pageable);
}
