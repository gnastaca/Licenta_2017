//
// Created by Geo on 5/7/2017.
//

#ifndef NODE_LOCATIE_H
#define NODE_LOCATIE_H

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


#endif //NODE_LOCATIE_H
