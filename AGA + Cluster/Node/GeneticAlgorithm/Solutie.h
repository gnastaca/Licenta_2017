//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_SOLUTIE_H
#include <vector>
#include "Locatie.h"
#include "../json/json.h"
#define NODE_SOLUTIE_H


class Solutie {
private:
    std::vector<int> solutie;
    int dimensine;
    double fitness = 0.0;
public:
    Solutie(int dimensiune);
    /**
     * creaza o solutie encodata
     */
    void initializareSolutie();
    void afisareSolutie();
    int getEncodareLocatie(int index);
    /**
     * Aceasta functie are rolul de ajuta la formarea unei noi solutii
     * @param encode
     */
    void addLocation(int encode);
    /**
     * In caz ca atunci cand se construieste un copil trebuie sa cautam o valoare
     * care nu se gaseste deja in solutie
     */
    bool verificareLocatie(int l);

    void setInfo(int index, int info);

    void setFitness(double dist);

    double getFitness();

    double getDistantaSolutie();

    void copyTraseu(Solutie sol);

    int getNumarOrase();

    Json::Value solutieToJson();

    void jsonToSolutie(Json::Value json);

    void setNumarOrase(int n);
};


#endif //NODE_SOLUTIE_H
