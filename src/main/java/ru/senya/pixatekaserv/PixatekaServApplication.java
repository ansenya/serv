package ru.senya.pixatekaserv;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class PixatekaServApplication {
    static {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("Version: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {}
    }


    public static void main(String[] args) {
        SpringApplication.run(PixatekaServApplication.class, args);
    }


}
