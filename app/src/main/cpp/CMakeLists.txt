cmake_minimum_required(VERSION 3.22.1)

project("decomp")

find_library(log-lib log)

add_subdirectory(r2)

add_library(bridge SHARED bridge.cpp)
target_include_directories(bridge PRIVATE 
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    $ENV{GITHUB_WORKSPACE}/python/include/python3.14
)
target_link_libraries(bridge 
    ${log-lib} 
    decomp
    $ENV{GITHUB_WORKSPACE}/python/lib/libpython3.14.so
)