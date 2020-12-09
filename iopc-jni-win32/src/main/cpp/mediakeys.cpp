#include <jni.h>
#include <stdio.h>

#define WINVER 0x0500
#include <windows.h>

#include "mediakeys_jni.h"
#include "mediakeys.h"

#ifdef __cplusplus
extern "C" {
#endif

//hits the volume mute/unmute key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_volumeMute (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_VOLUME_MUTE);
    return;
}

//hits the volume down key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_volumeDown (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_VOLUME_DOWN);
    return;
}

//hits the volume up key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_volumeUp (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_VOLUME_UP);
    return;
}

//hits the previous track key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_songPrevious (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_MEDIA_PREV_TRACK);
    return;
}

//hits the next track key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_songNext (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_MEDIA_NEXT_TRACK);
    return;
}

//hits the play/pause key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_songPlayPause (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_MEDIA_PLAY_PAUSE);
    return;
}

//hits the media stop key
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_MediaKeys_mediaStop (JNIEnv *env, jclass thisClass) {
    MediaKeys_pressKey(VK_MEDIA_STOP);
    return;
}

#ifdef __cplusplus
}
#endif