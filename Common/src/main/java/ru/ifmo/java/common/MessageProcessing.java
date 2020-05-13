package ru.ifmo.java.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface MessageProcessing {

    static byte[] packMessage(byte[] array) {
        byte[] length = ByteBuffer.allocate(4).putInt(array.length).array();
        return ByteBuffer.allocate(4 + array.length)
                .put(length).put(array).array();
    }

    static byte[] readPackedMessage(InputStream inputStream) throws IOException {
        byte[] bytes = readNBytes(inputStream, 4);
        int size = ByteBuffer.wrap(bytes).getInt();
        return readNBytes(inputStream, size);
    }

    static private byte[] readNBytes(InputStream inputStream, int n) throws IOException {
        byte[] bytes = new byte[n];
        int numberOfActuallyReadBytes = inputStream.read(bytes);
        if (numberOfActuallyReadBytes == -1) {
            throw new ClosedSocket();
        }
        assert numberOfActuallyReadBytes == n;
        return bytes;
    }

    class ClosedSocket extends IOException{}

}
