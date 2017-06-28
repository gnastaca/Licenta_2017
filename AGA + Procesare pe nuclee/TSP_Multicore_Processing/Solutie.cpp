//
// Created by Geo on 5/1/2017.
//

#include "Solutie.h"
#include <random>

Solutie::Solutie(int dimensiune) {
    this->dimensine = dimensiune;
}

/**
 * Solutia contine numere de la 0 la dimensiun-1
 */
void Solutie::initializareSolutie() {
    for(int i = 0; i < this->dimensine; i++)
        this->solutie.push_back(i);

    int sursa, destinatie, aux;
    for(int i = 0; i < this->dimensine; i++){
        sursa = rand() % this->dimensine;
        destinatie = rand() % this->dimensine;

        aux = this->solutie[sursa];
        this->solutie[sursa] = this->solutie[destinatie];
        this->solutie[destinatie] = aux;
    }
}

void Solutie::afisareSolutie() {
    for(int i = 0; i < this->solutie.size(); i++)
        printf("%d-", this->solutie[i]);
    printf("\n");
}

int Solutie::getEncodareLocatie(int index) {
    return this->solutie[index];
}

void Solutie::addLocation(int encode) {
    this->solutie.push_back(encode);
}

bool Solutie::verificareLocatie(int l) {
    for(int i = 0; i < this->solutie.size(); i++) {
        if (this->solutie[i] == l)
            return true;
    }
    return false;
}

void Solutie::setInfo(int index, int info) {
    this->solutie[index] = info;
}

void Solutie::setFitness(double dist) {
    this->fitness = 1.0 / dist;
}

double Solutie::getFitness() {
    return this->fitness;
}

//double Solutie::getDistantaSolutie() {
//    double distantaTotala= 0.0;
//    Locatie l1 = this->traseu[this->solutie[0]];
//    for(int i = 0; i < this->dimensine-1; i++){
//        Locatie l2 = this->traseu[this->solutie[i+1]];
//        distantaTotala += l1.distantaIntreDouaLocatii(l2);
//        l1 = l2;
//    }
//    distantaTotala += l1.distantaIntreDouaLocatii(this->traseu[this->solutie[0]]);
//    return distantaTotala;
//}

//void Solutie::copyTraseu(std::vector<Locatie> traseu) {
//    for(int i = 0; i < this->dimensine; i++){
//        this->traseu = traseu;
////        Locatie *loc = new Locatie(traseu[i].getCoordonataX(), traseu[i].getCoordonataX());
////        this->traseu.push_back(*loc);
////        Locatie loc(1,2);
////        this->traseu.push_back(loc);
//    }
//}