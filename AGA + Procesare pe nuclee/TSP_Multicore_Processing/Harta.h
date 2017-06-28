//
// Created by Geo on 5/1/2017.
//

#ifndef TSPNEWVERSION_HARTA_H
    #include <vector>
    #include "Locatie.h"
    #include <stdio.h>
#include "json/json.h"
#include "Solutie.h"
#define TSPNEWVERSION_HARTA_H

class Harta {
public:
    std::vector<Locatie> harta;
    void adaugareLocatie(Locatie loc);
    Locatie getLocatieDupaIndex(int index);
    void afisareHarta();
    int getNumarOrase();
    double getDistantaTraseuDupaSolutie(Solutie solutie);
    double getFitnessFupaSolutie(Solutie solutie);
    void jsonToHarta(Json::Value traseu);
    Json::Value solutieToJson(Solutie sol);
};

//definire
//std::vector<Locatie> Harta::harta;

#endif //TSPNEWVERSION_HARTA_H
