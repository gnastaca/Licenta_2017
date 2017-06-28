//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_HARTA_H
#include <vector>
#include "Locatie.h"
#include <stdio.h>
#include "../json/json.h"
#include "Solutie.h"
#define NODE_HARTA_H



    class Harta {
    public:
        std::vector<Locatie> harta;
        void adaugareLocatie(Locatie loc);
        Locatie getLocatieDupaIndex(int index);
        void afisareHarta();
        int getNumarOrase();
        double getDistantaTraseuDupaSolutie(Solutie solutie);
        double getFitnessFupaSolutie(Solutie solutie);
        bool jsonToHarta(Json::Value traseu);
        Json::Value solutieToJson(Solutie sol);
        void curataHarta();
    };



#endif //NODE_HARTA_H
