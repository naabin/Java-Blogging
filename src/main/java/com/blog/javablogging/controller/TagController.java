package com.blog.javablogging.controller;

import com.blog.javablogging.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/unique-tag")
    public ResponseEntity<?> checkUniqueTag(@RequestParam(name = "tag", required = true) String tag) {
        Boolean byTagName = this.tagService.findByTagName(tag);
        HashMap<String, Boolean> available = new HashMap<>();
        if (byTagName){
            available.put("available", true);
            return ResponseEntity.ok().body(available);
        }
        available.put("available", false);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(available);
    }
}
