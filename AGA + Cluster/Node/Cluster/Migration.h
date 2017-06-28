//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_MIGRATION_H
#include "vector"
#include "../json/json.h"
#include "../Helpful/SocketDataTransfer.h"

#include <winsock2.h>
#include <windows.h>
#include <ws2tcpip.h>
#include <stdlib.h>
#include <stdio.h>
#include <random>
#include  "../GeneticAlgorithm/Solutie.h"
#define NODE_MIGRATION_H

// Need to link with Ws2_32.lib
#pragma comment (lib, "Ws2_32.lib")
// #pragma comment (lib, "Mswsock.lib")
#define DEFAULT_PORT "9998"

class Migration {
private:
    std::vector<Solutie> migrants;
    int n;
    Json::Value pcOfCluster;
public:
    Migration(Json::Value pcOfCluster);
    void addMigrant(Solutie migrant);
    Solutie getMigrant(int index);
    void setNumberOfMigrants(int nr);
    void setAdressOfPCFromCluste(Json::Value ips);
    Json::Value toJson();
    int startMigration();
    Json::Value stringToJson(std::string str);
};



#endif //NODE_MIGRATION_H
