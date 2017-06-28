//
// Created by Geo on 5/1/2017.
//

#ifndef TSPNEWVERSION_SPATIUDESOLUTII_H
#include <vector>
#include "Solutie.h"
#include "Harta.h"
#define TSPNEWVERSION_SPATIUDESOLUTII_H


class SpatiuDeSolutii {
private:
    int dimensiuneSpatiuDeSolutii;
    int numarTotalDeOrase;
    Harta harta;
    std::vector<Solutie> spatiuDeSolutii;
public:
    SpatiuDeSolutii();
    SpatiuDeSolutii(int dimensiune, int numarOrase, Harta harta);
    /**
     * In functie de dimensiunea spatiului se vor crea solutiile
     * Ruta va fi encodata cu valori de la 1 la dimensiunea hartii
     */
    void creareSpatiuDeSolutii();
    void afisareSpatiuDeSolutii();
    void getCeaMaiMicaDistantadescoperita();
    int getDimensiuneSpatiu();
    void adaugareSolutie(Solutie sol);
    Solutie getSolutieDupaIndex(int index);
    Solutie getCelMaiTare();
    Solutie getCelMaiSlab();
    void setFitnessPentruToateSolutiile();
    int getIndexCeaMaiSlabaSolutie();
    void setSolutie(int index, Solutie sol);
    void initializareSpatiuLiber();
};


#endif //TSPNEWVERSION_SPATIUDESOLUTII_H
