package com.example.generator.definesn.controller;

import javax.validation.constraints.NotNull;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.generator.definesn.usecase.DefinedSnMapGenerator;
import com.example.generator.definesn.usecase.DefinedSnMapGeneratorRequest;
import com.example.generator.definesn.usecase.DefinedSnMapGeneratorResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DefinedSNMapGeneratorController {

    private final DefinedSnMapGenerator definedSnMapGenerator;

    @PostMapping(value = "/map-generator/define-sn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> generateMap (
        @RequestParam("proId") String proId,
        @RequestParam("productCode") String productCode,
        @RequestParam("batchId") String batchId,
        @RequestParam("locationCode") String locationCode,
        @RequestParam("locationNo") String locationNo,
        @RequestParam("totalPallet") int totalPallet,
        @RequestParam("quantityBox") int quantityBox,
        @RequestParam("quantityBottle") int quantityBottle,
        @NotNull @RequestPart("file") MultipartFile file
    ) {
        DefinedSnMapGeneratorRequest cmd = DefinedSnMapGeneratorRequest.valueOf(file, proId,
            productCode, batchId, locationCode, locationNo, totalPallet, quantityBox,
            quantityBottle
        );

        DefinedSnMapGeneratorResponse dto = definedSnMapGenerator.generate(cmd);
        ByteArrayResource resource = new ByteArrayResource(dto.getZip().toByteArray());

        return ResponseEntity.ok()
            .headers(setHeaders(dto.getFilename()))
            .body(resource);
    }

    public static HttpHeaders setHeaders(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            String.format("attachment; filename=\"%s.zip\"", filename)
        );

        return headers;
    }
}
