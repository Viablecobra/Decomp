#ifndef R2_CONFIGURE_H
#define R2_CONFIGURE_H

#ifdef __cplusplus
extern "C" {
#endif

#include "r_version.h"

#define R_CHECKS_LEVEL 0
#define WANT_DEBUGSTUFF 0
#define R_CRITICAL_ENABLED 0
#define DEBUGGER 0
#define WANT_ZIP 1
#define HAVE_DECL_ADDR_NO_RANDOMIZE 0
#define HAVE_ARC4RANDOM_UNIFORM 0
#define HAVE_EXPLICIT_BZERO 0
#define HAVE_EXPLICIT_MEMSET 0
#define HAVE_CLOCK_NANOSLEEP 0
#define HAVE_SIGACTION 1
#define WANT_THREADS 1
#define WANT_QJS 0
#define WANT_V35 0
#define WANT_CAPSTONE 1
#define HAVE_LINUX_CAN_H 0
#define R2_USE_BUNDLE_PREFIX 1
#define R2_USE_SQSH 0
#define R2_WASM_BROWSER 0

#define R_BUILDSYSTEM "android"
#define R2_CSVERSION 5
#define WITH_STATIC_THEMES 0
#define HAVE_GPERF 0

#define R2_PREFIX "/data/data/com.vcx.decomp"
#define R2_DATDIR_R2 ""
#define R2_SDB ""
#define R2_PLATFORM "platform"
#define R2_ZIGNS "zigns"
#define R2_THEMES "cons"
#define R2_FLAGS "flag"
#define R2_FORTUNES "doc"
#define R2_HUD "hud"
#define R2_SDB_FCNSIGN "fcnsign"
#define R2_SDB_OPCODES "opcodes"
#define R2_SDB_MAGIC "magic"
#define R2_SDB_FORMAT "format"
#define R2_GLOBAL_RC "radare2rc"

#define HAVE_LIB_MAGIC 0
#define USE_LIB_MAGIC 0
#define HAVE_LIB_XXHASH 0
#define USE_LIB_XXHASH 0
#define HAVE_LIB_SSL 0
#define WANT_SSL_CRYPTO 0
#define HAVE_LIBUV 0
#define HAVE_PTRACE 0
#define USE_PTRACE_WRAP 0
#define HAVE_FORK 0
#define WANT_DYLINK 1
#define WITH_GPL 1
#define HAVE_JEMALLOC 0

#ifdef __cplusplus
}
#endif

#endif