package fr.esgi.archi.codec;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

import java.io.File;

public class FileCodec implements MessageCodec<File, File> {

    @Override
    public void encodeToWire(Buffer buffer, File file) {

    }

    @Override
    public File decodeFromWire(int i, Buffer buffer) {
        System.out.println(buffer.toString());
        return new File("");
    }

    @Override
    public File transform(File file) {
        return file;
    }

    @Override
    public String name() {
        return "FileCodec";
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
