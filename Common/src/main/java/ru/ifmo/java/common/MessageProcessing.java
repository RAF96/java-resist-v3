package ru.ifmo.java.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface MessageProcessing {

    static byte[] packMessage(byte[] array) {
        ByteBuffer length = ByteBuffer.allocate(4).putInt(array.length);
        ByteBuffer message = ByteBuffer.wrap(array);
        return ByteBuffer.allocate(length.capacity() + message.capacity())
                .put(length).put(message).array();
    }

    //FIXME. maybe it's unnecessary
    static byte[] unpackMessage(byte[] array) {
        return null;
    }

    static byte[] readPackedMessage(InputStream inputStream) throws IOException {
        int size = ByteBuffer.wrap(readNBytes(inputStream, 4)).getInt();
        return readNBytes(inputStream, size);
    }

    static private byte[] readNBytes(InputStream inputStream, int n) throws IOException {
        byte[] bytes = new byte[n];
        int numberOfActuallyReadBytes = inputStream.read(bytes);
        assert numberOfActuallyReadBytes == n;
        return bytes;
    }

}
