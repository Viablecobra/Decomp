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

extern "C" JNIEXPORT jstring JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeDecompile(
    JNIEnv *env, jobject thiz, jstring so_path) {

    const char *path = env->GetStringUTFChars(so_path, 0);
    LOGI("Advanced Phase 1: %s", path);

    RCore *r = r_core_new();
    if (r_core_file_open(r, path, R_PERM_R, 0) < 0) {
        r_core_free(r);
        env->ReleaseStringUTFChars(so_path, path);
        return env->NewStringUTF("Failed to open .so");
    }

    r_core_cmd0(r, "aaa");
    
    char *funcs = r_core_cmd_str(r, "aflj");
    char *xrefs = r_core_cmd_str(r, "axtj");
    char *strings = r_core_cmd_str(r, "izzj");
    char *main_disasm = r_core_cmd_str(r, "pdfj @ main");

    std::string result = "=== FUNCTIONS ===
";
    result += funcs ? funcs : "No functions
";
    result += "
=== XREFS ===
";
    result += xrefs ? xrefs : "No xrefs
";
    result += "
=== STRINGS ===
";
    result += strings ? strings : "No strings
";
    result += "
=== MAIN ===
";
    result += main_disasm ? main_disasm : "No main";

    free(funcs); free(xrefs); free(strings); free(main_disasm);
    r_core_free(r);
    env->ReleaseStringUTFChars(so_path, path);
    
    return env->NewStringUTF(result.c_str());
}