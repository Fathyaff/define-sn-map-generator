package com.example.generator.definesn.usecase;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DefinedSnMapGeneratorRequest {

    String proId;

    String productCode;

    String batchId;

    String locationCode;

    String locationNo;

    int totalPallet;

    int quantityBox;

    int quantityBottle;

    MultipartFile file;

    String timeStamp;

    public static DefinedSnMapGeneratorRequest valueOf(
        MultipartFile file, String proId, String productCode, String batchId, String locationCode,
        String locationNo, int totalPallet, int quantityBox,
        int quantityBottle
    ) {
        return builder()
            .proId(proId)
            .productCode(productCode)
            .batchId(batchId)
            .locationCode(locationCode)
            .locationNo(locationNo)
            .totalPallet(totalPallet)
            .quantityBox(quantityBox)
            .quantityBottle(quantityBottle)
            .file(file)
            .timeStamp(constructTimeStamp())
            .build();
    }

    private static String constructTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        return sdfDate.format(new Date());
    }
}
