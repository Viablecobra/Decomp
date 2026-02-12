cmake_minimum_required(VERSION 3.22.1)

project("decomp")

find_library(log-lib log)

add_subdirectory(r2)

add_library(bridge SHARED bridge.cpp)
target_include_directories(bridge PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_CURRENT_SOURCE_DIR}/python3.14
)
target_link_libraries(bridge
    ${log-lib}
    decomp
    ${CMAKE_CURRENT_SOURCE_DIR}/../../main/jniLibs/arm64-v8a/libpython3.14.so
    ${CMAKE_CURRENT_SOURCE_DIR}/../../main/jniLibs/arm64-v8a/libcrypto.so
    ${CMAKE_CURRENT_SOURCE_DIR}/../../main/jniLibs/arm64-v8a/libssl.so
)