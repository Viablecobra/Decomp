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

extern "C" JNIEXPORT jstringArray JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeDecompile(
    JNIEnv *env, jobject thiz, jstring so_path) {

    const char *path = env->GetStringUTFChars(so_path, 0);
    LOGI("Decomp: %s", path);

    RCore *r = r_core_new();
    if (!r) {
        jclass strClass = env->FindClass("java/lang/String");
        jstringArray tabs = env->NewStringArray(16);
        env->SetStringArrayElement(tabs, 0, env->NewStringUTF("r_core_new failed"));
        env->ReleaseStringUTFChars(so_path, path);
        return tabs;
    }

    RIODesc *desc = r_core_file_open(r, path, R_PERM_R, 0);
    if (!desc) {
        r_core_free(r);
        jclass strClass = env->FindClass("java/lang/String");
        jstringArray tabs = env->NewStringArray(16);
        env->SetStringArrayElement(tabs, 0, env->NewStringUTF("Failed to open"));
        env->ReleaseStringUTFChars(so_path, path);
        return tabs;
    }

    r_core_cmd0(r, "aaa");
    
    char *overview = r_core_cmd_str(r, "iI");
    char *funcs = r_core_cmd_str(r, "aflj");
    char *xrefs = r_core_cmd_str(r, "axtj");
    char *strings = r_core_cmd_str(r, "izzj");
    char *names = r_core_cmd_str(r, "flj");
    char *imports = r_core_cmd_str(r, "ii");
    char *exports = r_core_cmd_str(r, "iE");
    char *pseudo = r_core_cmd_str(r, "pdf @ main");

    jclass strClass = env->FindClass("java/lang/String");
    jstringArray tabs = env->NewStringArray(16);
    
    env->SetStringArrayElement(tabs,  0, env->NewStringUTF("Overview"));
    env->SetStringArrayElement(tabs,  1, env->NewStringUTF(overview ? overview : "{}"));
    env->SetStringArrayElement(tabs,  2, env->NewStringUTF("Functions"));
    env->SetStringArrayElement(tabs,  3, env->NewStringUTF(funcs ? funcs : "[]"));
    env->SetStringArrayElement(tabs,  4, env->NewStringUTF("XRefs"));
    env->SetStringArrayElement(tabs,  5, env->NewStringUTF(xrefs ? xrefs : "[]"));
    env->SetStringArrayElement(tabs,  6, env->NewStringUTF("Strings"));
    env->SetStringArrayElement(tabs,  7, env->NewStringUTF(strings ? strings : "[]"));
    env->SetStringArrayElement(tabs,  8, env->NewStringUTF("Names"));
    env->SetStringArrayElement(tabs,  9, env->NewStringUTF(names ? names : "[]"));
    env->SetStringArrayElement(tabs, 10, env->NewStringUTF("Pseudo C"));
    env->SetStringArrayElement(tabs, 11, env->NewStringUTF(pseudo ? pseudo : "no main"));
    env->SetStringArrayElement(tabs, 12, env->NewStringUTF("Imports"));
    env->SetStringArrayElement(tabs, 13, env->NewStringUTF(imports ? imports : "[]"));
    env->SetStringArrayElement(tabs, 14, env->NewStringUTF("Exports"));
    env->SetStringArrayElement(tabs, 15, env->NewStringUTF(exports ? exports : "[]"));

    free(overview); free(funcs); free(xrefs); free(strings); free(names);
    free(imports); free(exports); free(pseudo);
    r_core_free(r);
    env->ReleaseStringUTFChars(so_path, path);
    
    return tabs;
}