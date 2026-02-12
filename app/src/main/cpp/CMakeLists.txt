cmake_minimum_required(VERSION 3.22.1)

project("decomp")

find_library(log-lib log)

add_subdirectory(r2)

add_library(bridge SHARED bridge.cpp)
target_include_directories(bridge PRIVATE 
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    ${CMAKE_SOURCE_DIR}/build/python/env/debug/include/python3.11
)
target_link_libraries(bridge 
    ${log-lib} 
    decomp
    ${CMAKE_SOURCE_DIR}/build/python/env/debug/lib/libpython3.11.so
)