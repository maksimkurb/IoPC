#include <windows.h>
#include <commctrl.h>
#include <mmdeviceapi.h>
#include <endpointvolume.h>
#include <stdio.h>
#include <math.h>       /* log */

#ifndef _VolumeControl_h
#define _VolumeControl_h

    void VolumeControlSetMasterVolume(int value, long* err);
    int VolumeControlGetMasterVolume(long* err);

#endif