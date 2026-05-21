cmake_minimum_required(VERSION 3.22.1)
project("llamajni")

add_library(llama-jni SHARED src/main/cpp/llama_placeholder.cpp)

find_library(log-lib log)

target_link_libraries(llama-jni ${log-lib})

target_include_directories(llama-jni PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
