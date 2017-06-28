//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_GRANITA_H
#include <winsock2.h>
#include <stdio.h>
#include "../json/json.h"
#include "../Helpful/SocketDataTransfer.h"
#include "../GeneticAlgorithm/Solutie.h"
#define NODE_GRANITA_H


class Granita {
public:
    Granita();
    static Solutie migrant;
    static bool flag;
    static int port;
    void connectionBetweenNode();
};

#endif //NODE_GRANITA_H
