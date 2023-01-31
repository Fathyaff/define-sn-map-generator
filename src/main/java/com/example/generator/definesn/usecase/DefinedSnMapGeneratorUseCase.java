package com.example.generator.definesn.usecase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.example.generator.gateway.IOGateway;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefinedSnMapGeneratorUseCase implements DefinedSnMapGenerator {

    private final IOGateway ioGateway;

    @Override
    public DefinedSnMapGeneratorResponse generate(DefinedSnMapGeneratorRequest cmd) {
        Deque<String> serialNumbers = getSerialNumbers(cmd);

        String zipFileName = String.format("generated-map-%s", cmd.getTimeStamp());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ZipOutputStream zos = new ZipOutputStream(outStream);
            writePalletFiles(cmd, serialNumbers, zos);
            writeReminingSnFile(serialNumbers, zos);
            zos.close();

            return DefinedSnMapGeneratorResponse.valueOf(outStream, zipFileName);
        } catch (IOException e) {
            log.error("Error on zip file", e);
            return DefinedSnMapGeneratorResponse.valueOf(outStream, zipFileName);
        }
    }

    private void writeReminingSnFile(
        Deque<String> serialNumbers, ZipOutputStream zos
    ) throws IOException {
        String remainingSnFilename = String.format("remaining-sn-%s.csv", serialNumbers.size());
        ZipEntry secondFileEntry = new ZipEntry(remainingSnFilename);
        zos.putNextEntry(secondFileEntry);
        for (String remainingSn : serialNumbers) {
            String data = String.format("%s\n", remainingSn);
            zos.write(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
        }
        zos.closeEntry();
    }

    private Deque<String> getSerialNumbers(DefinedSnMapGeneratorRequest cmd) {
        List<String[]> lines = ioGateway.readCSV(cmd.getFile());
        return lines.stream()
            .map(sn -> {
                return String.join("", sn);
            })
            .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private void writePalletFiles(
        DefinedSnMapGeneratorRequest cmd, Deque<String> serialNumbers, ZipOutputStream zos
    ) {
        for (int i = 1; i <= cmd.getTotalPallet(); i++) {
            String highestSerialNumber = serialNumbers.pop();
            String boxFileName = String.format("[%s]-[%s]-[%s_%s]-[%s]-[3]-[%s].csv",
                cmd.getProId(), highestSerialNumber, cmd.getLocationCode(), cmd.getLocationNo(),
                cmd.getTimeStamp(), cmd.getBatchId()
            );

            List<String> boxSerialNumbers = writeBoxFiles(cmd, serialNumbers, zos);
            addPalletChild(cmd, zos, boxFileName, boxSerialNumbers);
        }
    }

    @SneakyThrows
    private void addPalletChild(
        DefinedSnMapGeneratorRequest cmd,
        ZipOutputStream zos,
        String boxFileName,
        List<String> boxSerialNumbers
    ) {
        ZipEntry secondFileEntry = new ZipEntry(boxFileName);
        zos.putNextEntry(secondFileEntry);
        for (int index = 0; index < boxSerialNumbers.size(); index++) {
            String data = getDataLine(cmd, index, boxSerialNumbers);
            zos.write(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
        }

        zos.closeEntry();
    }

    private String getDataLine(
        DefinedSnMapGeneratorRequest cmd, int index, List<String> boxSerialNumbers
    ) {
        if (index == boxSerialNumbers.size() - 1) {
            return String.format("%s;%s;%s",
                boxSerialNumbers.get(index), cmd.getTimeStamp(), cmd.getProductCode()
            );
        }

        return String.format("%s;%s;%s\n",
            boxSerialNumbers.get(index), cmd.getTimeStamp(), cmd.getProductCode()
        );
    }

    @SneakyThrows
    private List<String> writeBoxFiles(
        DefinedSnMapGeneratorRequest cmd, Deque<String> serialNumbers, ZipOutputStream zos
    ) {
        List<String> boxSerialNumbers = new ArrayList<>();
        for (int i = 1; i <= cmd.getQuantityBox(); i++) {
            String containerSerialNumber = serialNumbers.pop();
            boxSerialNumbers.add(containerSerialNumber);

            String bottleFilename = String.format("[%s]-[%s]-[%s_%s]-[%s]-[2]-[%s].csv",
                cmd.getProId(), containerSerialNumber, cmd.getLocationCode(),
                cmd.getLocationNo(), cmd.getTimeStamp(), cmd.getBatchId()
            );

            ZipEntry secondFileEntry = new ZipEntry(bottleFilename);
            zos.putNextEntry(secondFileEntry);
            writeChild(cmd, serialNumbers, zos);
            zos.closeEntry();
        }

        return boxSerialNumbers;
    }

    @SneakyThrows
    private void writeChild(
        DefinedSnMapGeneratorRequest cmd,
        Deque<String> serialNumbers,
        ZipOutputStream zos
    ) {
        for (int index = 1; index <= cmd.getQuantityBottle(); index++) {
            String child = serialNumbers.pop();
            String childData = getChildLine(cmd, index, child);

            zos.write(childData.getBytes(StandardCharsets.UTF_8), 0,
                childData.length()
            );
        }
    }

    private String getChildLine(DefinedSnMapGeneratorRequest cmd, int index, String child) {
        if (index == cmd.getQuantityBottle()) {
            return String.format("%s;%s;%s", child, cmd.getTimeStamp(),
                cmd.getProductCode()
            );
        }

        return String.format("%s;%s;%s\n", child, cmd.getTimeStamp(),
            cmd.getProductCode()
        );
    }

}
