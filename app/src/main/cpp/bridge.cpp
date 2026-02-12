#include <jni.h>
#include <Python.h>
#include <android/log.h>

#define LOG_TAG "DecompBridge"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

extern "C" JNIEXPORT void JNICALL
Java_com_vcx_decomp_DecompiledActivity_nativeStartAnalysis(JNIEnv *env, jobject thiz, jstring so_path) {
    const char* path = env->GetStringUTFChars(so_path, 0);
    
    if (Py_Initialize()) {
        LOGI("Python init failed");
        env->ReleaseStringUTFChars(so_path, path);
        return;
    }
    
    PyRun_SimpleString("import sys; sys.path.append('/data/user/0/com.vcx.decomp/files/python')");
    
    PyObject *pModule = PyImport_ImportModule("analyzer");
    if (pModule) {
        PyObject *pFunc = PyObject_GetAttrString(pModule, "ida_analyze");
        if (pFunc && PyCallable_Check(pFunc)) {
            PyObject *pArgs = PyTuple_New(1);
            PyTuple_SetItem(pArgs, 0, PyUnicode_FromString(path));
            PyObject_CallObject(pFunc, pArgs);
            Py_DECREF(pArgs);
        }
        Py_DECREF(pFunc);
        Py_DECREF(pModule);
    }
    
    Py_Finalize();
    env->ReleaseStringUTFChars(so_path, path);
}