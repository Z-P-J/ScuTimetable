//
// android Log格式在c++的日志工具类
//

#ifndef NDKPRACTICE_LOGUTILS_H
#define NDKPRACTICE_LOGUTILS_H
#include <android/log.h>
#define LOG_TAG "xiyou"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#endif //NDKPRACTICE_LOGUTILS_H
