//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_NODE_H

#include <stdio.h>
#include <string.h>
#include "../GeneticAlgorithm/AlgoritmGenetic.h"
#include "../GeneticAlgorithm/Harta.h"
#include <iostream>
#include "../Helpful/SocketDataTransfer.h"
#define NODE_NODE_H

/**
 * Clasa SubPopulation reprezinta o parte din intreaga populatie(oras)
 */
class Node{
private:
    int port;
    char * ip;
    SOCKET sock;
    std::string name;

public:
    static bool status;
    static bool restoreConnection;
    Node(int port, char * ip){
        this->port = port;
        this->ip = ip;
    }
    int InitConnection();
    void Comunication();
    void PrintRecived(Json::Value json);
    Json::Value stringToJson(std::string str);
    void Test();
    void connectionBetweenNode();
    Json::Value pcOfCluster;
    void setNonBlock();
    void closeSocketNode();
};


#endif //NODE_NODE_H
