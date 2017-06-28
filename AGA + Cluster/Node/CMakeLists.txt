cmake_minimum_required(VERSION 3.6)
project(Node)

set(CMAKE_CXX_STANDARD 11)

set(SOURCE_FILES jsoncpp.cpp  json/json.h GeneticAlgorithm/AlgoritmGenetic.cpp GeneticAlgorithm/AlgoritmGenetic.h Helpful/SocketDataTransfer.h GeneticAlgorithm/SpatiuDeSolutii.cpp GeneticAlgorithm/SpatiuDeSolutii.h GeneticAlgorithm/Harta.cpp GeneticAlgorithm/Harta.h GeneticAlgorithm/Locatie.cpp GeneticAlgorithm/Locatie.h GeneticAlgorithm/Solutie.cpp GeneticAlgorithm/Solutie.h Cluster/Node.cpp Cluster/Node.h Cluster/Migration.cpp Cluster/Migration.h main.cpp Cluster/Granita.cpp Cluster/Granita.h Helpful/ResetConnection.cpp Helpful/ResetConnection.h)
add_executable(Node ${SOURCE_FILES})

if(WIN32)
    target_link_libraries(Node wsock32 ws2_32)
endif()