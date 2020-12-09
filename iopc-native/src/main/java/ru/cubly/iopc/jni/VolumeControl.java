package ru.cubly.iopc.jni;

import org.scijava.nativelib.NativeLoader;

import java.io.IOException;

public class VolumeControl {
    static {
        try {
            NativeLoader.loadLibrary("iopc-native");
        } catch (IOException e) {
            System.err.println("Failed to init iopc-native library");
            e.printStackTrace();
        }
    }

    public static native int getMasterVolume();
    public static native void setMasterVolume(int value);
}
