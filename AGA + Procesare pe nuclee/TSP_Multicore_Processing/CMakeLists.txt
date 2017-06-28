cmake_minimum_required(VERSION 3.6)
project(TspNewversion)

set(CMAKE_CXX_STANDARD 11)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -fopenmp")

set(SOURCE_FILES main.cpp Locatie.cpp Locatie.h Harta.cpp Harta.h SpatiuDeSolutii.cpp SpatiuDeSolutii.h Solutie.cpp Solutie.h AlgoritmGenetic.cpp AlgoritmGenetic.h Server.cpp Server.h jsoncpp.cpp)
FILE(GLOB INCLUDE_FILES "json/*.h")
add_executable(TspNewversion ${SOURCE_FILES})
if(WIN32)
    target_link_libraries(TspNewversion wsock32 ws2_32)
endif()