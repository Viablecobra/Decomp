#include <jni.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "Decomp"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jstring JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeDecompile(
    JNIEnv *env, jobject thiz, jstring so_path) {

    const char *path = env->GetStringUTFChars(so_path, 0);
    LOGI("Got .so path: %s", path);

    std::string result = "int main() {
"
                        "    printf("Decompiled via JNI stub\
");
"
                        "    return 0;
}";
    
    env->ReleaseStringUTFChars(so_path, path);
    return env->NewStringUTF(result.c_str());
}