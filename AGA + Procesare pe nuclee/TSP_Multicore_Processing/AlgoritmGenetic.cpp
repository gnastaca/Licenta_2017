//
// Created by Geo on 5/1/2017.
//

#include "AlgoritmGenetic.h"
#include "random"
#include <vector>
#include <iostream>
#include "SocketDataTransfer.h"
#include <omp.h>
#include <time.h>
AlgoritmGenetic::AlgoritmGenetic(SpatiuDeSolutii * spatiu, Harta * harta, int epoci, int cross, int mutatie, int dimTurn, SOCKET s, int stopCond) {
    this->spatiuDeSolutii = spatiu;
    this->harta = harta;
    this->epochs = epoci;
    this->populatieGenerataCrossover = cross;
    this->populatieGenerataMutatie = mutatie;
    this->dimensiuneTurneu = dimTurn;
    this->socket = s;
    this->stopCond = stopCond;
}

Solutie AlgoritmGenetic::turneu(int dimensiune) {
    std::vector<Solutie> candidati;
    int indexSolutie;
    for(int i = 0; i < dimensiune; i++){
        indexSolutie = rand() % this->spatiuDeSolutii->getDimensiuneSpatiu();
        //printf("Index:%d\n", indexSolutie);
        candidati.push_back(this->spatiuDeSolutii->getSolutieDupaIndex(indexSolutie));
    }

    Solutie sol = candidati[0];
    for(int i = 1; i < candidati.size(); i++){
        if(harta->getDistantaTraseuDupaSolutie(sol) > harta->getDistantaTraseuDupaSolutie(candidati[i]))
            sol = candidati[i];
    }
    return sol;
}

Solutie AlgoritmGenetic::rezolvare() {
    int contor = 0;
    double okk = 0.0;
    int iteratia_finala;
    //this->spatiuDeSolutii->setFitnessPentruToateSolutiile();
    int  i = 0;
    printf("Dimensiune traseu initial:%f\n",harta->getDistantaTraseuDupaSolutie(this->spatiuDeSolutii->getCelMaiTare()));
    for(i = 0; i < this->epochs; i++){
        SpatiuDeSolutii * sp = new SpatiuDeSolutii(this->spatiuDeSolutii->getDimensiuneSpatiu(),this->harta->getNumarOrase(),*this->harta);
        sp->initializareSpatiuLiber();
        sp->setSolutie(0,this->spatiuDeSolutii->getCelMaiTare());
#pragma omp parallel for
        for(int i = 1; i < this->populatieGenerataCrossover; i++){

            //printf("Index:%d\n",i);
            Solutie sol1 = this->turneu(this->dimensiuneTurneu);
            Solutie sol2 = this->turneu(this->dimensiuneTurneu);
            sp->setSolutie(i, recombinareMuchiiCrossOver(sol1,sol2));
        }
#pragma omp parallel for
        for(int i = this->populatieGenerataCrossover; i < this->populatieGenerataCrossover + this->populatieGenerataMutatie; i++){
            //printf("nr_threads:%d\n",omp_get_num_threads());
            //printf("Index:%d\n",i);
            Solutie sol1 = this->turneu(this->dimensiuneTurneu);
            sp->setSolutie(i, this->inversionMutation(sol1));
        }
        //exit(0);
        this->spatiuDeSolutii = sp;
        //delete sp;
        this->spatiuDeSolutii->setFitnessPentruToateSolutiile();
       // printf("%f %d\n",this->harta->getDistantaTraseuDupaSolutie(spatiuDeSolutii->getCelMaiTare()), i);

        if(okk != harta->getDistantaTraseuDupaSolutie(this->spatiuDeSolutii->getCelMaiTare())){
            okk = harta->getDistantaTraseuDupaSolutie(this->spatiuDeSolutii->getCelMaiTare());
            contor = 0;
            iteratia_finala = i;
        }else{
            contor ++;
        }
        if(contor == stopCond){
            break;
        }
        if( i > 0 and i % 50 == 0 and i != this->epochs){
            Json::Value rezultat = this->harta->solutieToJson(this->spatiuDeSolutii->getCelMaiTare());
            rezultat["code"] = 200;
            rezultat["ep"] = this->epochs;
            rezultat["aep"] = i;
            SocketDataTransfer::writing(this->socket, rezultat.toStyledString().c_str());
        }
        //sp->afisareSpatiuDeSolutii();
        //exit(0);
    }
    printf("Dimensiune traseu final:%f\n",harta->getDistantaTraseuDupaSolutie(this->spatiuDeSolutii->getCelMaiTare()));
    printf("Total numar iteratii:%d\n",i);
    printf("Numar iteratii solutie optima:%d\n",iteratia_finala);
    return this->spatiuDeSolutii->getCelMaiTare();
}

Solutie AlgoritmGenetic::rezolvareII() {

    for(int k = 0; k < this->epochs; k ++) {
        this->spatiuDeSolutii->setFitnessPentruToateSolutiile();
        for (int i = 0; i < this->spatiuDeSolutii->getDimensiuneSpatiu(); i++) {
            Solutie sol1 = this->turneu(this->dimensiuneTurneu);
            Solutie sol2 = this->turneu(this->dimensiuneTurneu);
            Solutie solutie = recombinareMuchiiCrossOver(sol1,sol2);

            if(this->spatiuDeSolutii->getCelMaiSlab().getFitness() < this->harta->getFitnessFupaSolutie(solutie)){
                solutie.setFitness(this->harta->getDistantaTraseuDupaSolutie(solutie));
                if(rand() % 100 < this->populatieGenerataMutatie){
                    solutie = this->inversionMutation(solutie);
                }
                int index = this->spatiuDeSolutii->getIndexCeaMaiSlabaSolutie();
                this->spatiuDeSolutii->setSolutie(index, solutie);
            }else{
                Solutie dupaMutatie = this->inversionMutation(solutie);
                dupaMutatie.setFitness(this->harta->getDistantaTraseuDupaSolutie(dupaMutatie));
                if(this->spatiuDeSolutii->getCelMaiSlab().getFitness() < this->harta->getFitnessFupaSolutie(dupaMutatie)){
                    int index = this->spatiuDeSolutii->getIndexCeaMaiSlabaSolutie();
                    this->spatiuDeSolutii->setSolutie(index, dupaMutatie);
                }
            }
        }
        if(k < this->epochs - 1 && k % 10 == 0) {
            Solutie boss = this->spatiuDeSolutii->getCelMaiTare();
            printf("Cel mai bun: %f\n", this->harta->getDistantaTraseuDupaSolutie(boss));
            Json::Value rezultat = this->harta->solutieToJson(this->spatiuDeSolutii->getCelMaiTare());
            rezultat["code"] = 200;
            SocketDataTransfer::writing(this->socket, rezultat.toStyledString().c_str());
        }
    }
    return this->spatiuDeSolutii->getCelMaiTare();
}

Solutie AlgoritmGenetic::recombinareMuchiiCrossOver(Solutie mama, Solutie tata) {
    //mama.afisareSolutie();
    //tata.afisareSolutie();
    std::vector<std::set<int>> vecini1 = this->getVeciniSolutie(mama);
    std::vector<std::set<int>> vecini2 = this->getVeciniSolutie(tata);
    std::vector<std::set<int>> rez = unireDouaVecinatati(vecini1,vecini2);

    std::set<int> codariRamase;
    for(int i = 0; i < this->harta->getNumarOrase(); i++)
        codariRamase.insert(i);

    Solutie copil(this->harta->getNumarOrase());
    int start = rand() % 2;
    if(start == 0){
        start = mama.getEncodareLocatie(0);
    }else
        start = tata.getEncodareLocatie(0);

    for(int i = 0; i < this->harta->getNumarOrase(); i++){
        copil.addLocation(start);
        codariRamase.erase(start);
        this->stergeVecin(start, &rez);

        std::set<int> data = rez[start];
        if(codariRamase.size() == 0 )
            break;
        if(data.size() == 0){
            int index = rand() % codariRamase.size();
            int j = 0;
            //printf("index = %d\n",index);
            for(auto data: codariRamase){
                if(index == j){
                    start = data;
                    break;
                }
                j++;
            }
        }else{
            int min = 7;
            for(auto l : data) {
                min = rez[l].size();
                break;
            }
            int ok = 0;
            for(auto locEnc: data){
                if(rez[locEnc].size() < min){
                    start = locEnc;
                    min = rez[locEnc].size();
                    ok ++;
                }
            }

            if(ok == 0){
                int k = rand() % rez[start].size();
                int j = 0;
                for(auto l : data){
                    if(k == j){
                        start = l;
                        break;
                    }
                    j++;
                }
            }
        }
    }
    //copil.afisareSolutie();
   return copil;
}

std::vector<std::set<int>> AlgoritmGenetic::getVeciniSolutie(Solutie solutie) {
    std::vector<std::set<int>> vecini(this->harta->getNumarOrase());

    for(int i = 1; i < this->harta->getNumarOrase()-1; i++){
        std::set<int> veciniLocatie;
        veciniLocatie.insert(solutie.getEncodareLocatie(i-1));
        veciniLocatie.insert(solutie.getEncodareLocatie(i+1));
        vecini[solutie.getEncodareLocatie(i)] = veciniLocatie;
    }
    std::set<int> veciniLocatieS;
    veciniLocatieS.insert(solutie.getEncodareLocatie(1));
    veciniLocatieS.insert(solutie.getEncodareLocatie(harta->getNumarOrase()-1));
    vecini[solutie.getEncodareLocatie(0)] = veciniLocatieS;

    std::set<int> veciniLocatieF;
    veciniLocatieF.insert(solutie.getEncodareLocatie(0));
    veciniLocatieF.insert(solutie.getEncodareLocatie(harta->getNumarOrase()-2));
    vecini[solutie.getEncodareLocatie(harta->getNumarOrase()-1)] = veciniLocatieF;
    return vecini;
}

std::vector<std::set<int>> AlgoritmGenetic::unireDouaVecinatati(std::vector<std::set<int>> v1,
                                                                std::vector<std::set<int>> v2) {
    for(int i = 0; i < v1.size(); i++){
        for(auto rez : v2[i]){
            v1[i].insert(rez);
        }
    }
    return v1;
}

void AlgoritmGenetic::afisareVecinatate(std::vector<std::set<int>> vecinatati) {
    for(int i = 0; i < this->harta->getNumarOrase(); i++){
        printf("%d:",i);
        for(auto rez : vecinatati[i]){
            std::cout<<rez<<" ";
        }
        printf("\n");
    }
}

void AlgoritmGenetic::stergeVecin(int locatie, std::vector<std::set<int>> *unire) {
    std::vector<std::set<int>>::iterator it;
    for(it = unire->begin(); it != unire->end(); it++)
        it->erase(locatie);
}

Solutie AlgoritmGenetic::mutatie(Solutie sol) {
        std::random_device rd;
        std::mt19937 gen(time(NULL));
        std::uniform_real_distribution<> dis(0, 1);
        for(int i = 0;  i < this->harta->getNumarOrase(); i++){
            if(0.05> dis(gen)){
                int changeWith = rand() % this->harta->getNumarOrase();
                int aux = sol.getEncodareLocatie(i);
                sol.setInfo(i,sol.getEncodareLocatie(changeWith));
                sol.setInfo(changeWith, aux);
            }
        }
        return  sol;
}

Solutie AlgoritmGenetic::inversionMutation(Solutie sol) {
    int start, final;
    start = rand() % this->harta->getNumarOrase();
    final = rand() % this->harta->getNumarOrase();
    //printf("start = %d, final = %d\n",start, final);

    if (start > final){
        int aux = start;
        start = final;
        final = aux;
    }

    int dimensiuneSecventa = (final - start) + 1;
    int pozitieStartInserare;
    if((this->harta->getNumarOrase() - dimensiuneSecventa) != 0)
        pozitieStartInserare = rand() % (this->harta->getNumarOrase() - dimensiuneSecventa);
    else
        pozitieStartInserare = 0;
   // printf("Pozitie start inserare:%d\n",pozitieStartInserare);

    std::vector<int>secventa;
    for(int i = start; i <= final; i++){
        secventa.push_back(sol.getEncodareLocatie(i));
    }
    Solutie mutatie(this->harta->getNumarOrase());
    int contor = 0;
    for(int i = 0; i < this->harta->getNumarOrase(); i++){
        if(contor == start) {
            contor = final + 1;
        }
        if(i >= pozitieStartInserare && i < (pozitieStartInserare+dimensiuneSecventa)){
            mutatie.addLocation(secventa[dimensiuneSecventa - ((i-pozitieStartInserare)+1)]);
        }else{
            mutatie.addLocation(sol.getEncodareLocatie(contor));
            contor++;
            //printf("contor=%d\n",contor);
        }
    }
    //mutatie.afisareSolutie();
    return  mutatie;
}