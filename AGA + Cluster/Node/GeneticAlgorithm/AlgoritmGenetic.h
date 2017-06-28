//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_ALGORITMGENETIC_H
#include <winsock2.h>
#include "SpatiuDeSolutii.h"
#include <vector>
#include <set>
#include <thread>
#define NODE_ALGORITMGENETIC_H


class AlgoritmGenetic {
private:
    SpatiuDeSolutii * spatiuDeSolutii;
    Harta * harta;
    int populatieGenerataCrossover;
    int populatieGenerataMutatie;
    int dimensiuneTurneu;
    int stopCond;
    Json::Value pcOfCluster;
    SOCKET socket;
public:
    static int epochs;
    AlgoritmGenetic();
    AlgoritmGenetic(SpatiuDeSolutii * spatiu, Harta * harta, int epoci, int cross, int mutatie, int dimTurn, SOCKET s, Json::Value pcOfCluster, int stopCond);
    void rezolvare();
    void rezolvareII();
    Solutie turneu(int dimensiune);
    std::vector<std::set<int>> getVeciniSolutie(Solutie solutie);
    std::vector<std::set<int>> unireDouaVecinatati(std::vector<std::set<int>> v1, std::vector<std::set<int>> v2);
    void afisareVecinatate(std::vector<std::set<int>> vecinatati);
    Solutie recombinareMuchiiCrossOver(Solutie mama, Solutie tata);
    void stergeVecin(int locatie, std::vector<std::set<int>> *unire);
    Solutie mutatie(Solutie sol);
    Solutie inversionMutation(Solutie sol);
    void sendSolution(Solutie sol);
};


#endif //NODE_ALGORITMGENETIC_H
