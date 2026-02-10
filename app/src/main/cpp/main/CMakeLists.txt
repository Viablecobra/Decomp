cmake_minimum_required(VERSION 3.22.1)

add_library(decomp SHARED decomp.cpp)

target_include_directories(decomp PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/include)

find_library(log-lib log)

target_link_libraries(decomp
    log
    r_core
    r_util
    r_bin
    r_asm
    r_anal
    r_cons
    r_io
    r_flag
)