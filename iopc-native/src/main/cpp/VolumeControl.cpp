#include "VolumeControl.h"

#define EXIT_ON_ERROR(hr)  \
              if (FAILED(hr)) { *err=hr; printf("VolumeControl error %d occurred\n", -hr); goto Exit; }


#define SAFE_RELEASE(punk)  \
              if ((punk) != NULL)  \
                { (punk)->Release(); (punk) = NULL; }

int VolumeControlGetMasterVolume(long* err) {
    IAudioEndpointVolume *g_pEndptVol = NULL;
    bool WindowsLH;
    HRESULT hr = S_OK;
    IMMDeviceEnumerator *pEnumerator = NULL;
    IMMDevice *pDevice = NULL;
    OSVERSIONINFO VersionInfo;

    ZeroMemory(&VersionInfo, sizeof(OSVERSIONINFO));
    VersionInfo.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
    GetVersionEx(&VersionInfo);
    if (VersionInfo.dwMajorVersion > 5)
      WindowsLH = true; // vista+
    else
      WindowsLH = false;


    float currentVal = 1;
    if (WindowsLH)
    {
      CoInitialize(NULL);

      // Get enumerator for audio endpoint devices.
      hr = CoCreateInstance(__uuidof(MMDeviceEnumerator),
                            NULL, CLSCTX_INPROC_SERVER,
                            __uuidof(IMMDeviceEnumerator),
                            (void**)&pEnumerator);
      EXIT_ON_ERROR(hr)

      // Get default audio-rendering device.
      hr = pEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &pDevice);
      EXIT_ON_ERROR(hr)

      hr = pDevice->Activate(__uuidof(IAudioEndpointVolume),
                             CLSCTX_ALL, NULL, (void**)&g_pEndptVol);
      EXIT_ON_ERROR(hr)

      hr = g_pEndptVol->GetMasterVolumeLevelScalar(&currentVal);
      EXIT_ON_ERROR(hr)
    }

Exit:
    SAFE_RELEASE(pEnumerator)
    SAFE_RELEASE(pDevice)
    SAFE_RELEASE(g_pEndptVol)
    CoUninitialize();

    return (int)round(100 * currentVal);
}

void VolumeControlSetMasterVolume(int value, long* err) {
    IAudioEndpointVolume* g_pEndptVol = NULL;
    bool WindowsLH;
    HRESULT hr = S_OK;
    IMMDeviceEnumerator* pEnumerator = NULL;
    IMMDevice* pDevice = NULL;
    OSVERSIONINFO VersionInfo;

    ZeroMemory(&VersionInfo, sizeof(OSVERSIONINFO));
    VersionInfo.dwOSVersionInfoSize = sizeof(OSVERSIONINFO);
    GetVersionEx(&VersionInfo);
    if (VersionInfo.dwMajorVersion > 5)
        WindowsLH = true; // vista+
    else
        WindowsLH = false;

    if (WindowsLH)
    {
        CoInitialize(NULL);

        // Get enumerator for audio endpoint devices.
        hr = CoCreateInstance(__uuidof(MMDeviceEnumerator),
            NULL, CLSCTX_INPROC_SERVER,
            __uuidof(IMMDeviceEnumerator),
            (void**)&pEnumerator);
        EXIT_ON_ERROR(hr)

            // Get default audio-rendering device.
            hr = pEnumerator->GetDefaultAudioEndpoint(eRender, eConsole, &pDevice);
        EXIT_ON_ERROR(hr)

            hr = pDevice->Activate(__uuidof(IAudioEndpointVolume),
                CLSCTX_ALL, NULL, (void**)&g_pEndptVol);
        EXIT_ON_ERROR(hr)
            float currentVal;

        float got = (float)value / 100.0; // needs to be within 1.0 to 0.0
        hr = g_pEndptVol->SetMasterVolumeLevelScalar(got, NULL);
        EXIT_ON_ERROR(hr)
    }

Exit:
    SAFE_RELEASE(pEnumerator)
        SAFE_RELEASE(pDevice)
        SAFE_RELEASE(g_pEndptVol)
        CoUninitialize();
}