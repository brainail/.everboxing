cmake_minimum_required(VERSION 3.3)
project(fff)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
include_directories("/usr/local/libtiff-4.0.6/include")
link_directories("/usr/local/libtiff-4.0.6/lib")
link_libraries("tiff")

set(SOURCE_FILES fractal.c)
add_executable(fff ${SOURCE_FILES})