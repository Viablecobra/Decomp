cmake_minimum_required(VERSION 3.22.1)

set(R2_LIB_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../jniLibs/arm64-v8a")

add_library(decomp SHARED decomp.cpp)

target_include_directories(decomp PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)

find_library(log-lib log)

add_library(r_core SHARED IMPORTED)
set_target_properties(r_core PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_core.so")

add_library(r_util SHARED IMPORTED)
set_target_properties(r_util PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_util.so")

add_library(r_bin SHARED IMPORTED)
set_target_properties(r_bin PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_bin.so")

add_library(r_asm SHARED IMPORTED)
set_target_properties(r_asm PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_asm.so")

add_library(r_anal SHARED IMPORTED)
set_target_properties(r_anal PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_anal.so")

add_library(r_cons SHARED IMPORTED)
set_target_properties(r_cons PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_cons.so")

add_library(r_io SHARED IMPORTED)
set_target_properties(r_io PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_io.so")

add_library(r_flag SHARED IMPORTED)
set_target_properties(r_flag PROPERTIES IMPORTED_LOCATION "${R2_LIB_DIR}/libr_flag.so")

target_link_libraries(decomp
    ${log-lib}
    r_core
    r_util
    r_bin
    r_asm
    r_anal
    r_cons
    r_io
    r_flag
)

set_target_properties(decomp PROPERTIES
    CXX_STANDARD 17
    CXX_STANDARD_REQUIRED ON
)