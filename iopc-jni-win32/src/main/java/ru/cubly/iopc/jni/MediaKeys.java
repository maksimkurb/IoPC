package ru.cubly.iopc.jni;

import org.scijava.nativelib.NativeLoader;

import java.io.IOException;

public class MediaKeys {

    static {
        try {
            NativeLoader.loadLibrary("iopc-jni-win32");
        } catch (IOException e) {
            System.err.println("Failed to init iopc-jni-win32 library");
            e.printStackTrace();
        }
    }

    public static native void volumeMute();

    public static native void volumeDown();

    public static native void volumeUp();

    public static native void songPrevious();

    public static native void songNext();

    public static native void songPlayPause();

    public static native void mediaStop();

}