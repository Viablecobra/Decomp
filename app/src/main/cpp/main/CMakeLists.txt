add_library(decomp SHARED decomp.cpp)

find_library(log-lib log)

target_link_libraries(decomp ${log-lib})