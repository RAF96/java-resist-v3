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

    static private byte[] readNBytes(InputStream inputStream, int expectedNumberOfReadBytes) throws IOException {
        byte[] bytes = new byte[expectedNumberOfReadBytes];
        int sumNumberOfActuallyReadBytes = 0;
        while (sumNumberOfActuallyReadBytes < expectedNumberOfReadBytes) {
            int numberOfActuallyReadBytes = inputStream.read(bytes, sumNumberOfActuallyReadBytes, expectedNumberOfReadBytes - sumNumberOfActuallyReadBytes);
            sumNumberOfActuallyReadBytes += numberOfActuallyReadBytes;
            if (numberOfActuallyReadBytes == -1) {
                throw new ClosedSocket();
            }
        }
        assert (sumNumberOfActuallyReadBytes == expectedNumberOfReadBytes) :
                String.format("Number of actually read bytes is %d, then expected %d",
                        sumNumberOfActuallyReadBytes,
                        expectedNumberOfReadBytes);
        return bytes;
    }

    class ClosedSocket extends IOException{}

}
