cmake_minimum_required(VERSION 3.22.1)

project("decomp")

set(R2_LIB_DIR ${CMAKE_SOURCE_DIR}/main/jniLibs/${ANDROID_ABI})

add_subdirectory(main)