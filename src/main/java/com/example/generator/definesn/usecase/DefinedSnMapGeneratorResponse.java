package com.example.generator.definesn.usecase;

import java.io.ByteArrayOutputStream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "valueOf")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DefinedSnMapGeneratorResponse {

    ByteArrayOutputStream zip;

    String filename;
}
