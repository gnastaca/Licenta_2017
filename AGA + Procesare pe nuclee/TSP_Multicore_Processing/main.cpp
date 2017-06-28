#include <iostream>
#include "Locatie.h"
#include "Harta.h"
#include "SpatiuDeSolutii.h"
#include <time.h>
#include <random>
#include "AlgoritmGenetic.h"
#include "Server.h"
#include <winsock2.h>

int main() {
    srand((unsigned int)time(NULL));
    Harta harta;

    Locatie loc1(60,200);
    harta.adaugareLocatie(loc1);
    Locatie loc2(180,200);
    harta.adaugareLocatie(loc2);
    Locatie loc3(80,180);
    harta.adaugareLocatie(loc3);
    Locatie loc4(140,180);
    harta.adaugareLocatie(loc4);
    Locatie loc5(20,160);
    harta.adaugareLocatie(loc5);
    Locatie loc6(100,160);
    harta.adaugareLocatie(loc6);
    Locatie loc7(200,160);
    harta.adaugareLocatie(loc7);
    Locatie loc8(140,140);
    harta.adaugareLocatie(loc8);
    Locatie loc9(40,120);
    harta.adaugareLocatie(loc9);
    Locatie loc10(100,120);
    harta.adaugareLocatie(loc10);
    Locatie loc11(180,100);
    harta.adaugareLocatie(loc11);
    Locatie loc12(60,80);
    harta.adaugareLocatie(loc12);
    Locatie loc13(120,80);
    harta.adaugareLocatie(loc13);
    Locatie loc14(180,60);
    harta.adaugareLocatie(loc14);
    Locatie loc15(20,40);
    harta.adaugareLocatie(loc15);
    Locatie loc16(100,40);
    harta.adaugareLocatie(loc16);
    Locatie loc17(200,40);
    harta.adaugareLocatie(loc17);
    Locatie loc18(20,20);
    harta.adaugareLocatie(loc18);
    Locatie loc19(60,20);
    harta.adaugareLocatie(loc19);
    Locatie loc20(160,20);
    harta.adaugareLocatie(loc20);

//    for(int i = 0; i < 100; i++){
//        harta.adaugareLocatie(Locatie());
//    }

//    SpatiuDeSolutii spatiu(150, harta.getNumarOrase(), harta);
//    spatiu.creareSpatiuDeSolutii();
////    //spatiu.afisareSpatiuDeSolutii();
////    //spatiu.getCeaMaiMicaDistantadescoperita();
    SOCKET s;
//    AlgoritmGenetic AG(&spatiu, &harta, 500, 100,50,3, s);
//
//    AG.rezolvare();
//
    Server * server = new Server();
    server->asteaptaConexiune();

    return 0;
}