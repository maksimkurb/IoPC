#include <jni.h>
#include <stdio.h>

#include "VolumeControlJni.h"
#include "VolumeControl.h"

jint throwIllegalAccessError(JNIEnv* env, char* message)
{
    jclass exClass;
    char* className = "java/lang/IllegalAccessException";

    exClass = env->FindClass(className);
    return env->ThrowNew(exClass, message);
}


#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     ru_cubly_iopc_jni_VolumeControl
 * Method:    getMasterVolume
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ru_cubly_iopc_jni_VolumeControl_getMasterVolume (JNIEnv * env, jclass clazz) {
    long err = 0;
    int volume = VolumeControlGetMasterVolume(&err);
    if (FAILED(err)) {
        char buf[64];
        snprintf(buf, 64, "Failed to get master volume: error %d", err);
        throwIllegalAccessError(env, buf);
    }
    return volume;
}

/*
 * Class:     ru_cubly_iopc_jni_VolumeControl
 * Method:    setMasterVolume
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_ru_cubly_iopc_jni_VolumeControl_setMasterVolume (JNIEnv * env, jclass clazz, jint volume) {
    long err = 0;
    VolumeControlSetMasterVolume(volume, &err);
    if (FAILED(err)) {
        char buf[64];
        snprintf(buf, 64, "Failed to set master volume: error %d", err);
        throwIllegalAccessError(env, buf);
    }
}

#ifdef __cplusplus
}
#endif
