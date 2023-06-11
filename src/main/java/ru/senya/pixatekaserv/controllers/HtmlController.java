package ru.senya.pixatekaserv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.senya.pixatekaserv.models.Image;
import ru.senya.pixatekaserv.repo.ImageRepository;

import java.util.ArrayList;
import java.util.Collections;



@Controller
public class HtmlController {

    @Autowired
    ImageRepository repository;



    @GetMapping("/")
    public String home(Model model) {
        ArrayList<Image> images = (ArrayList<Image>) repository.findAll();
        Collections.shuffle(images);
        model.addAttribute("images", images);
        return "home";
    }

    @GetMapping("/create")
    public String create() {
        return "create";
    }

    @GetMapping("/photos/{path}")
    public ResponseEntity<Resource> getImage(@PathVariable String path) {
        Resource file;
        file = new ClassPathResource("static/images/" + path);
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(file);
        } else {
            return ResponseEntity.status(404).build();
        }
    }

}