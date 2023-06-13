package ru.senya.pixatekaserv.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.pixatekaserv.models.Image;
import ru.senya.pixatekaserv.repo.ImageRepository;
import ru.senya.pixatekaserv.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static ru.senya.pixatekaserv.utils.Utils.*;


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

    @PostMapping("/upload")
    public String getUpload(@RequestParam(value = "file") MultipartFile file, Model model) {
        String uniqueFilename = UUID.randomUUID() + "." + file.getContentType().split("/")[1];
        String path = PATH_FOLDER + uniqueFilename;
        try {
            Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
            model.addAttribute("img", "http://"+SERVER_HOST+"/images/get/"+uniqueFilename);
            model.addAttribute("txt", Utils.getTags(path));
            return "upload";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}