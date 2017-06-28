//
// Created by Geo on 5/1/2017.
//

#include "SpatiuDeSolutii.h"
#include <omp.h>
#include <stdio.h>
#include <math.h>

SpatiuDeSolutii::SpatiuDeSolutii() {}

SpatiuDeSolutii::SpatiuDeSolutii(int dimensiune, int numarOrase, Harta harta) {
    this->numarTotalDeOrase = numarOrase;
    this->dimensiuneSpatiuDeSolutii = dimensiune;
    this->harta = harta;
}

void SpatiuDeSolutii::creareSpatiuDeSolutii() {
    for(int i = 0; i < this->dimensiuneSpatiuDeSolutii; i++){
        Solutie sol(this->numarTotalDeOrase);
        sol.initializareSolutie();
        //sol.afisareSolutie();
        this->spatiuDeSolutii.push_back(sol);
    }
}

void SpatiuDeSolutii::afisareSpatiuDeSolutii() {
    for(int i = 0; i < this->dimensiuneSpatiuDeSolutii; i ++){
        this->spatiuDeSolutii[i].afisareSolutie();
    }
}

void SpatiuDeSolutii::getCeaMaiMicaDistantadescoperita() {
    for(int i = 0; i < this->dimensiuneSpatiuDeSolutii; i ++){
       printf("Solutia %d: %f\n",i,harta.getDistantaTraseuDupaSolutie(this->spatiuDeSolutii[i]));
    }
}

int SpatiuDeSolutii::getDimensiuneSpatiu() {
    return this->dimensiuneSpatiuDeSolutii;
}

Solutie SpatiuDeSolutii::getSolutieDupaIndex(int index) {
    return this->spatiuDeSolutii[index];
}
void SpatiuDeSolutii::adaugareSolutie(Solutie sol) {
    this->spatiuDeSolutii.push_back(sol);
}

Solutie SpatiuDeSolutii::getCelMaiTare() {
    Solutie sol = this->spatiuDeSolutii[0];
    double fit = this->spatiuDeSolutii[0].getFitness();
    for(int i = 1; i < this->dimensiuneSpatiuDeSolutii; i ++){
        if(fit < this->spatiuDeSolutii[i].getFitness()){
            fit = this->spatiuDeSolutii[i].getFitness();
            sol = this->spatiuDeSolutii[i];
        }
    }
   // printf("Dimensiunea traseului este:%f.\n",dim);
    return sol;
    //sol.afisareSolutie();
}

Solutie SpatiuDeSolutii::getCelMaiSlab() {
    Solutie sol = this->spatiuDeSolutii[0];
    double fit = this->spatiuDeSolutii[0].getFitness();
    for(int i = 1; i < this->dimensiuneSpatiuDeSolutii; i ++){
        if(fit > this->spatiuDeSolutii[i].getFitness()){
            fit = this->spatiuDeSolutii[i].getFitness();
            sol = this->spatiuDeSolutii[i];
        }
    }
    // printf("Dimensiunea traseului este:%f.\n",dim);
    return sol;
    //sol.afisareSolutie();
}

int SpatiuDeSolutii::getIndexCeaMaiSlabaSolutie() {
    int index;
    index  = 0;
    double fit = this->spatiuDeSolutii[0].getFitness();
    for(int i = 1; i < this->dimensiuneSpatiuDeSolutii; i ++){
        if(fit > this->spatiuDeSolutii[i].getFitness()){
            fit = this->spatiuDeSolutii[i].getFitness();
            index  = i;
        }
    }
    // printf("Dimensiunea traseului este:%f.\n",dim);
    return index;
    //sol.afisareSolutie();
}

void SpatiuDeSolutii::setFitnessPentruToateSolutiile() {
#pragma omp parallel for
    for(int i = 0; i < this->dimensiuneSpatiuDeSolutii; i ++){
        //printf("nr_threads:%d\n",omp_get_num_threads());
        this->spatiuDeSolutii[i].setFitness(this->harta.getDistantaTraseuDupaSolutie(this->spatiuDeSolutii[i]));
       // printf("%f\n",this->spatiuDeSolutii[i].getFitness());
    }
    //printf("Am terminat!!!\n");
}

void SpatiuDeSolutii::setSolutie(int index, Solutie sol) {
        this->spatiuDeSolutii[index] = sol;
//    for(int i = 0; i < this->spatiuDeSolutii[index].getNumarOrase(); i++){
//        this->spatiuDeSolutii[index].setInfo(i, sol.getEncodareLocatie(i));
//    }
//    this->spatiuDeSolutii[index].setFitness(harta.getDistantaTraseuDupaSolutie(this->spatiuDeSolutii[index]));
}

void SpatiuDeSolutii::initializareSpatiuLiber() {
    for(int i = 0; i < this->dimensiuneSpatiuDeSolutii; i++){
        Solutie sol(this->numarTotalDeOrase);
        this->spatiuDeSolutii.push_back(sol);
    }
}