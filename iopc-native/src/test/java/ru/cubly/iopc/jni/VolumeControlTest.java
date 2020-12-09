package ru.cubly.iopc.jni;

import org.junit.Test;

import static org.junit.Assert.*;

public class VolumeControlTest {
    @Test
    public void getMasterVolume() {
        System.out.println(VolumeControl.getMasterVolume());
    }
}