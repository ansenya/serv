package ru.senya.pixatekaserv.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.senya.pixatekaserv.models.Image;
import ru.senya.pixatekaserv.repo.ImageRepository;
import ru.senya.pixatekaserv.utils.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.*;


import static ru.senya.pixatekaserv.utils.Utils.*;


@RestController
public class ImagesController {

    Logger logger = LoggerFactory.getLogger(ImagesController.class);

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    public ImagesController(ServerProperties serverProperties) {
        SERVER_PORT = String.valueOf(serverProperties.getPort());
//        SERVER_IP = new BufferedReader(new InputStreamReader(new URL("https://api.ipify.org").openStream())).readLine();
//        SERVER_IP = InetAddress.getLoopbackAddress().getHostAddress();
//        SERVER_IP = "192.168.50.20";
//        SERVER_HOST = SERVER_IP + ":" + SERVER_PORT;
        SERVER_HOST="localhost:8080";
        logger.info("Running on: " + SERVER_HOST);

    }

    @GetMapping("/images")
    public Iterable<Image> images() {
        return imageRepository.findAll();
    }

    @GetMapping("/images/info/{id}")
    public ResponseEntity<Image> getPhoto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok().body(imageRepository.findById(id).orElseThrow());
        } catch (NoSuchElementException exception) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/images/create")
    public ResponseEntity<List<Image>> createPhotos(@RequestParam(value = "name", defaultValue = "") String imageName,
                                                    @RequestParam(value = "description", defaultValue = "") String description,
                                                    @RequestParam(value = "images") List<MultipartFile> files) {
        List<Image> images = new ArrayList<>();

        if (files.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            List<Thread> threads = new ArrayList<>();
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                if (file.isEmpty()) {
                    continue;
                }
                String uniqueFilename = UUID.randomUUID() + "." + file.getContentType().split("/")[1];
                String path = PATH_FOLDER + uniqueFilename;
                Thread thread = new Thread(() -> {
                    try {
                        Files.copy(file.getInputStream(), Paths.get(path), StandardCopyOption.REPLACE_EXISTING);

                        Image image =
                                Image.builder()
                                        .color(Utils.getColor(path))
                                        .tags(Utils.getTags(path))
                                        .description(description)
                                        .name(imageName)
                                        .path("http://" + SERVER_HOST + "/images/get/" + uniqueFilename)
                                        .build();

                        imageRepository.save(image);
                        images.add(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }
            return ResponseEntity.ok().body(images);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("images/get/{path}")
    public ResponseEntity<Resource> getByPath(@PathVariable String path) {
        Resource file = new FileSystemResource(PATH_FOLDER + path);
        if (file.exists()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE)
                    .body(file);
        }
        return ResponseEntity.noContent().build();
    }


}
