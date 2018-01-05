package com.example.edwin.photoarchive.AzureClasses;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Edwin on 4/18/2017.
 */
public class CustomInputStream extends InputStream {

    private final InputStream wraped;
    private final ReadListener listener;
    private long minimumBytesPerCall;
    private long bytesRead;
    private long totalRead;
    private long size;
    private long remainder;

    public CustomInputStream(InputStream wraped, ReadListener listener, long minimumBytesPerCall, long size) {
        this.wraped = wraped;
        this.listener = listener;
        this.minimumBytesPerCall = minimumBytesPerCall;
        this.size = size;
        this.remainder = size % minimumBytesPerCall;

    }


    @Override
    public int read() throws IOException {
        int read = wraped.read();
        if (read >= 0) {
            bytesRead++;
        }
        if (bytesRead == minimumBytesPerCall) {
            totalRead+=bytesRead;
            listener.onRead(bytesRead);
            bytesRead = 0;
        }

        if((size -totalRead) == remainder){
            minimumBytesPerCall = remainder;

        }

        return read;
    }

    @Override
    public int available() throws IOException {
        return wraped.available();
    }

    @Override
    public void close() throws IOException {
        wraped.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        wraped.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        wraped.reset();
    }

    @Override
    public boolean markSupported() {
        return wraped.markSupported();
    }

    interface ReadListener {
        void onRead(long bytes);
    }
}