#include <jni.h>
#include <string>
#include <android/log.h>
#include "r_core.h"
#include "r_types_base.h"
#include "r_util.h"
#include "r_list.h"
#include "r_bin.h"
#include "r_cons.h"
#include "r_io.h"
#include "r_flag.h"

#define LOG_TAG "Decomp"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT jobjectArray JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeDecompile(
    JNIEnv *env, jobject thiz, jstring so_path) {

    const char *path = env->GetStringUTFChars(so_path, 0);
    LOGI("Advanced Phase 1: %s", path);

    RCore *r = r_core_new();
    RIODesc *desc = r_core_file_open(r, path, R_PERM_R, 0);
    if (!desc) {
        r_core_free(r);
        env->ReleaseStringUTFChars(so_path, path);
        jclass strClass = env->FindClass("java/lang/String");
        jobjectArray tabs = env->NewObjectArray(1, strClass, 0);
        env->SetObjectArrayElement(tabs, 0, env->NewStringUTF("Failed to open .so"));
        return tabs;
    }
    r_io_desc_free(desc);

    r_core_cmd0(r, "aaa");
    
    char *funcs = r_core_cmd_str(r, "aflj");
    char *xrefs = r_core_cmd_str(r, "axtj");
    char *strings = r_core_cmd_str(r, "izzj");
    char *main_disasm = r_core_cmd_str(r, "pdfj @ main");

    jclass strClass = env->FindClass("java/lang/String");
    jobjectArray tabs = env->NewObjectArray(5, strClass, 0);
    
    env->SetObjectArrayElement(tabs, 0, env->NewStringUTF("FUNCTIONS"));
    env->SetObjectArrayElement(tabs, 1, env->NewStringUTF(funcs ? funcs : ""));
    env->SetObjectArrayElement(tabs, 2, env->NewStringUTF("XREFS"));
    env->SetObjectArrayElement(tabs, 3, env->NewStringUTF(xrefs ? xrefs : ""));
    env->SetObjectArrayElement(tabs, 4, env->NewStringUTF("STRINGS & MAIN"));

    free(funcs); free(xrefs); free(strings); free(main_disasm);
    r_core_free(r);
    env->ReleaseStringUTFChars(so_path, path);
    
    return tabs;
}