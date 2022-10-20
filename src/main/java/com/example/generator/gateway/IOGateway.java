package com.example.generator.gateway;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface IOGateway {

    List<String[]> readCSV(MultipartFile file);

}
