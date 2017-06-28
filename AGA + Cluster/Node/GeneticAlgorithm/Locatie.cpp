//
// Created by Geo on 5/1/2017.
//

#include "Locatie.h"
#include <math.h>
#include <stdio.h>
#include <random>

Locatie::Locatie(double x, double y){
    this->coordonataX = x;
    this->coordonataY = y;
}

Locatie::Locatie(){
    this->coordonataX = rand() % 300;
    this->coordonataY = rand() % 300;
}

double Locatie::getCoordonataX() {
    return this->coordonataX;
}

double Locatie::getCoordonataY() {
    return this->coordonataY;
}

double Locatie::distantaIntreDouaLocatii(Locatie locatie) {
    double distance;
    distance =  sqrt(pow((this->coordonataX - locatie.coordonataX),2) + pow((this->coordonataY - locatie.coordonataY),2));
    return distance;
}

void Locatie::afisareCoordonate() {
    printf("coordonata x = %f\nCoordonata y = %f\n", this->coordonataX,this->coordonataY);
}
