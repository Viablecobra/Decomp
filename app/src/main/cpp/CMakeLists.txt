cmake_minimum_required(VERSION 3.22.1)

project("decomp")

find_library(log-lib log)

add_subdirectory(r2)

add_library(bridge SHARED bridge.cpp)
target_include_directories(bridge PRIVATE 
    ${CMAKE_CURRENT_SOURCE_DIR}/include
    /opt/hostedtoolcache/Python/3.10.19/x64/include/python3.10m
)
target_link_libraries(bridge ${log-lib} decomp)