#pragma once

#ifdef _WIN32
#define EXPORT_FUNC __declspec(dllexport)
#else
#define EXPORT_FUNC
#endif

#include <string>

void EXPORT_FUNC MediaKeys_pressKey (WORD key);