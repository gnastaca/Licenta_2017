//
// Created by Geo on 5/1/2017.
//
#include "Locatie.h"
#include "Harta.h"
#include "SocketDataTransfer.h"

void Harta::adaugareLocatie(Locatie loc){
    this->harta.push_back(loc);
}

Locatie Harta::getLocatieDupaIndex(int index){
    return this->harta[index];
}

void Harta::afisareHarta() {
    std::vector<Locatie>::iterator it = this->harta.begin();
    while(it != this->harta.end()) {
        printf("[x -> %f, y -> %f]\n", it->getCoordonataX(), it->getCoordonataY());
        it++;
    }
}

int Harta::getNumarOrase() {
    return this->harta.size();
}

double Harta::getDistantaTraseuDupaSolutie(Solutie solutie) {
    double distantaTotala= 0.0;

    Locatie l1 = Harta::getLocatieDupaIndex(solutie.getEncodareLocatie(0));
    for(int i = 0; i < Harta::getNumarOrase()-1; i++){
        Locatie l2 = Harta::getLocatieDupaIndex(solutie.getEncodareLocatie(i+1));
        distantaTotala += l1.distantaIntreDouaLocatii(l2);
        l1 = l2;
    }
    distantaTotala += l1.distantaIntreDouaLocatii(Harta::getLocatieDupaIndex(solutie.getEncodareLocatie(0)));
    return distantaTotala;
}

double Harta::getFitnessFupaSolutie(Solutie solutie) {
    return 1.0 / getDistantaTraseuDupaSolutie(solutie);
}

void Harta::jsonToHarta(Json::Value traseu) {
    int dimensiune = traseu["dimensiune"].asInt();
    printf("[Harta]Dimensiune traseu %d\n", dimensiune);
    for(int i = 0; i < dimensiune; i++){
        Json::Value oras = SocketDataTransfer::stringToJson(traseu[std::to_string(i)].toStyledString());
        Locatie loc(oras["cx"].asDouble(), oras["cy"].asDouble());
        this->adaugareLocatie(loc);
    }
}

Json::Value Harta::solutieToJson(Solutie sol) {
    Json::Value solutieFinala;
    for(int i = 0; i < this->getNumarOrase(); i++){
        Json::Value oras;
        oras["cx"] = this->getLocatieDupaIndex(sol.getEncodareLocatie(i)).getCoordonataX();
        oras["cy"] = this->getLocatieDupaIndex(sol.getEncodareLocatie(i)).getCoordonataY();
        solutieFinala[std::to_string(i)] = oras;
    }
    solutieFinala["dimensiune"] = this->getNumarOrase();
    solutieFinala["distanta"] = (int)this->getDistantaTraseuDupaSolutie(sol);
    return solutieFinala;
}