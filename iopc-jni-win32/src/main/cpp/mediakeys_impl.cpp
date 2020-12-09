#include <jni.h>
#include <stdio.h>
#include <string>

#define WINVER 0x0500
#include <windows.h>

#include "mediakeys.h"

// https://stackoverflow.com/a/2969148/11039508
void MediaKeys_pressKey(WORD key) {
    KEYBDINPUT kbi;

    //specific keycode
    kbi.wVk = key;

    kbi.wScan = 0;
    kbi.dwFlags = 0;
    kbi.time = 0;
    kbi.dwExtraInfo = (ULONG_PTR) c();

    INPUT input;
    input.type = INPUT_KEYBOARD;
    input.ki   = kbi;

    SendInput(1, &input, sizeof(INPUT));

    return;
}