package com.radar.redis.util;

import com.radar.core.exception.RadarCommonException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import static java.util.zip.Deflater.BEST_SPEED;
import static java.util.zip.Deflater.FILTERED;

@Slf4j
public class ZlibUtils {
    private static final int BUFFER_SIZE = 1024; // 1024가 가장 적합한 듯 함, 너무 크거나 작으면 inflate/deflate 속도가 느려짐

    private static final int DEFAULT_LEVEL = BEST_SPEED; // BEST_SPEED가 속도대비 압축효율이 가장 좋음
    private static final int DEFAULT_STRATEGY = FILTERED; // HUFFMAN_ONLY를 제외하면 strategy는 다 속도가 준수하게 나오지만, 매우 근사한 차이로 FILTERED가 좀 더 좋음

    public static byte[] compress(byte[] bytes) {
        return compress(bytes, DEFAULT_LEVEL, DEFAULT_STRATEGY);
    }

    public static byte[] compress(byte[] bytes, int level, int strategy) {
        if (bytes == null) return null;

        Deflater deflater = new Deflater(level);
        deflater.setStrategy(strategy);
        deflater.setInput(bytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);

        deflater.finish();

        byte[] buffer = new byte[BUFFER_SIZE];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index
            outputStream.write(buffer, 0, count);
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("Exception occurred during close output stream", e);
        }

        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] bytes) {
        if (bytes == null) return null;

        Inflater inflater = new Inflater();
        inflater.setInput(bytes);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(bytes.length);

        byte[] buffer = new byte[BUFFER_SIZE];
        while (!inflater.finished()) {
            try {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            } catch (DataFormatException e) {
                throw new RadarCommonException(e);
            }
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("Exception occurred during close output stream", e);
        }

        return outputStream.toByteArray();
    }
}
