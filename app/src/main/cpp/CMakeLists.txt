cmake_minimum_required(VERSION 3.22.1)

project("decomp")

add_subdirectory(r2)

add_library(bridge SHARED bridge.cpp)
target_link_libraries(bridge decomp)