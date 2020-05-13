package ru.ifmo.java.common;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class MessageProcessingTest {

    @Test
    public void test() throws IOException {
        String test = "test";
        InputStream inputStream = new ByteArrayInputStream(MessageProcessing.packMessage(test.getBytes()));
        byte[] bytes = MessageProcessing.readPackedMessage(inputStream);
        String res = new String(bytes);
        assertEquals(res, test);

    }
}