#include <jni.h>
#include <string>
#include <android/log.h>
#include <r_core.h>

#define LOG_TAG "Decomp"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeDecompile(
    JNIEnv *env, jobject thiz, jstring so_path) {

    const char *path = env->GetStringUTFChars(so_path, 0);
    LOGI("Opening: %s", path);

    RCore *r = r_core_new();
    if (r_core_file_open(r, path, R_PERM_R, 0) < 0) {
        LOGI("Failed to open file");
        r_core_free(r);
        env->ReleaseStringUTFChars(so_path, path);
        return env->NewStringUTF("Failed to open .so");
    }

    r_core_cmd0(r, "aaa");
    char *json = r_core_cmd_str(r, "aflj");

    std::string result = json ? json : "No functions";
    free(json);
    r_core_free(r);
    
    env->ReleaseStringUTFChars(so_path, path);
    return env->NewStringUTF(result.c_str());
}