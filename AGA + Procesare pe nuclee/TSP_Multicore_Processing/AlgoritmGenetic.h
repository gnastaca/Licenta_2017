//
// Created by Geo on 5/1/2017.
//

#ifndef TSPNEWVERSION_ALGORITMGENETIC_H

#include "SpatiuDeSolutii.h"
#include <vector>
#include <set>
#include <winsock2.h>
#define TSPNEWVERSION_ALGORITMGENETIC_H


class AlgoritmGenetic {
private:
    SpatiuDeSolutii * spatiuDeSolutii;
    Harta * harta;
    int epochs;
    int populatieGenerataCrossover;
    int populatieGenerataMutatie;
    int dimensiuneTurneu;
    int stopCond;
    SOCKET socket;
public:
    AlgoritmGenetic(SpatiuDeSolutii * spatiu, Harta * harta, int epoci, int cross, int mutatie, int dimTurn, SOCKET s, int stopCond);
    Solutie rezolvare();
    Solutie rezolvareII();
    Solutie turneu(int dimensiune);
    std::vector<std::set<int>> getVeciniSolutie(Solutie solutie);
    std::vector<std::set<int>> unireDouaVecinatati(std::vector<std::set<int>> v1, std::vector<std::set<int>> v2);
    void afisareVecinatate(std::vector<std::set<int>> vecinatati);
    Solutie recombinareMuchiiCrossOver(Solutie mama, Solutie tata);
    void stergeVecin(int locatie, std::vector<std::set<int>> *unire);
    Solutie mutatie(Solutie sol);
    Solutie inversionMutation(Solutie sol);


};


#endif //TSPNEWVERSION_ALGORITMGENETIC_H
