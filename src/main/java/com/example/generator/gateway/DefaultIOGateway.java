package com.example.generator.gateway;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultIOGateway implements IOGateway {

    @Override
    public List<String[]> readCSV(MultipartFile file) {
        List<String[]> result = new ArrayList<>();
        try {
            String line;
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
                String[] lines = line.split(";");
                result.add(lines);
            }

        } catch (IOException e) {
            log.info("Error when read CSV");
        }

        return result;
    }

}
