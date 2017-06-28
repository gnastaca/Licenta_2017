//
// Created by Geo on 5/1/2017.
//

#ifndef TSPNEWVERSION_LOCATIE_H
#define TSPNEWVERSION_LOCATIE_H


class Locatie {
private:
    double coordonataX;
    double coordonataY;
public:
    Locatie(double x, double y);
    Locatie();

    double getCoordonataX();
    double getCoordonataY();

    /**
     * Aceasta functie are rolul de a calcula distanta dintre doua puncte
     * Distanta utilizata fiind cea euclidiana
     * @param locatie
     * @return
     */
    double distantaIntreDouaLocatii(Locatie locatie);

    void afisareCoordonate();
};


#endif //TSPNEWVERSION_LOCATIE_H
