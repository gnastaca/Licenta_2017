cmake_minimum_required(VERSION 3.6)
project(TSP_GA_SingleThread)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES main.cpp Map.h Individual.h Population.h SolvProblem.h)
add_executable(TSP_GA_SingleThread ${SOURCE_FILES})