//
// Created by Geo on 5/1/2017.
//

#include "AlgoritmGenetic.h"
#include "random"
#include <vector>
#include <iostream>
#include "../Helpful/SocketDataTransfer.h"
#include "../Cluster/Migration.h"
#include "../Cluster/Granita.h"
#include <omp.h>
#include <time.h>
AlgoritmGenetic::AlgoritmGenetic() {}
AlgoritmGenetic::AlgoritmGenetic(SpatiuDeSolutii * spatiu, Harta * harta, int epoci, int cross, int mutatie, int dimTurn, SOCKET s, Json::Value pcOfCluster, int stopCond) {
    this->spatiuDeSolutii = spatiu;
    this->harta = harta;
    this->epochs = epoci;
    this->populatieGenerataCrossover = cross;
    this->populatieGenerataMutatie = mutatie;
    this->dimensiuneTurneu = dimTurn;
    this->socket = s;
    this->pcOfCluster = pcOfCluster;
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

void AlgoritmGenetic::rezolvare() {
    //this->spatiuDeSolutii->setFitnessPentruToateSolutiile();
    double distantaCurenta = 0.0;
    double distantaTrecuta = 0.0;
    int iteratie;
    int contor = 0;
    const clock_t begin_time = clock();
    for(int  i = 0; i < this->epochs; i++){
        SpatiuDeSolutii * sp = new SpatiuDeSolutii(this->spatiuDeSolutii->getDimensiuneSpatiu(),this->harta->getNumarOrase(),*this->harta);
        sp->initializareSpatiuLiber();
        sp->setSolutie(0,this->spatiuDeSolutii->getCelMaiTare());
#pragma omp parallel for
        for(int j = 1; j < this->populatieGenerataCrossover; j++){

            //printf("Index:%d\n",i);
            Solutie sol1 = this->turneu(this->dimensiuneTurneu);
            Solutie sol2 = this->turneu(this->dimensiuneTurneu);
            sp->setSolutie(j, recombinareMuchiiCrossOver(sol1,sol2));
        }
#pragma omp parallel for
        for(int j =this->populatieGenerataCrossover; j < this->populatieGenerataCrossover + this->populatieGenerataMutatie; j++){
            //printf("nr_threads:%d\n",omp_get_num_threads());
            //printf("Index:%d\n",i);
            Solutie sol1 = this->turneu(this->dimensiuneTurneu);
            sp->setSolutie(j, this->inversionMutation(sol1));
        }
        //exit(0);
        this->spatiuDeSolutii = sp;
        //delete sp;
        this->spatiuDeSolutii->setFitnessPentruToateSolutiile();

        distantaCurenta = this->harta->getDistantaTraseuDupaSolutie(spatiuDeSolutii->getCelMaiTare());
        if(distantaCurenta == distantaTrecuta){
            contor++;
        }else{
            distantaTrecuta = distantaCurenta;
            iteratie = i;
            contor = 0;
        }
        printf("Epochs:%d,dist:%f\n",i,distantaCurenta);

        try {
            if (rand() % 50 < 1) {
                printf("[NODE]Trebuie sa migrez!\n");
                Migration migr(this->pcOfCluster);
                migr.addMigrant(this->spatiuDeSolutii->getSolutieDupaIndex(rand() % this->spatiuDeSolutii->getDimensiuneSpatiu()));
                migr.startMigration();
            }
        }catch(...){
            printf("[EROARE]EROARE LA MIGRARE!\n");
        }

        if(i % 30 == 0){
            printf("[Node]Progress-Trimit solutia\n");
            Json::Value json;
            json["cod"] = i;
            SocketDataTransfer::writing(this->socket,json.toStyledString().c_str());
        }

        try{
            if(Granita::flag == false){
                printf("Afisare individ:");
                Granita::migrant.afisareSolutie();
                int index  = this->spatiuDeSolutii->getIndexCeaMaiSlabaSolutie();
                printf("[AG-Migrant]Individul va fi setat pe pozitia %d\n", index);
                if(this->spatiuDeSolutii->getSolutieDupaIndex(index).getNumarOrase() == Granita::migrant.getNumarOrase()) {
                    Granita::migrant.setFitness(harta->getDistantaTraseuDupaSolutie(Granita::migrant));
                    this->spatiuDeSolutii->setSolutie(index, Granita::migrant);

                    printf("[AG-Migrare]Am adaugat solutia!\n");
                }else{
                    printf("[AG-Migrare]Solutia care migreaza nu corespunde la dimensiune:%d\n",Granita::migrant.getNumarOrase());
                }
                Granita::flag = true;
            }
        }catch(...){
            printf("[EORARE]EROARE LA INTEGRARE MIGRANT");
        }
        if(contor == stopCond)
            break;
    }
    printf("[Final]Am terminat procesarea\n");
    Json::Value json;
    json["cod"] = 10;
    SocketDataTransfer::writing(this->socket,json.toStyledString().c_str());

    Granita::flag = false;
    Granita::migrant.setNumarOrase(0);
    this->sendSolution(this->spatiuDeSolutii->getCelMaiTare());
}

void AlgoritmGenetic::rezolvareII() {

    for(int k = 0; k < 1000; k ++) {
        this->spatiuDeSolutii->setFitnessPentruToateSolutiile();
        for (int i = 0; i < this->spatiuDeSolutii->getDimensiuneSpatiu(); i++) {
            Solutie sol1 = this->turneu(5);
            Solutie sol2 = this->turneu(5);
            Solutie solutie = recombinareMuchiiCrossOver(sol1,sol2);

            if(this->spatiuDeSolutii->getCelMaiSlab().getFitness() < this->harta->getFitnessFupaSolutie(solutie)){
                solutie.setFitness(this->harta->getDistantaTraseuDupaSolutie(solutie));
              //  int index = this->spatiuDeSolutii->getIndexCeaMaiSlabaSolutie(this->spatiuDeSolutii->getCelMaiSlab().getFitness());
                //this->spatiuDeSolutii->setSolutie(index, solutie);
            }else{
                Solutie dupaMutatie = this->inversionMutation(solutie);
                dupaMutatie.setFitness(this->harta->getDistantaTraseuDupaSolutie(dupaMutatie));
                if(this->spatiuDeSolutii->getCelMaiSlab().getFitness() < this->harta->getFitnessFupaSolutie(dupaMutatie)){
                  //  int index = this->spatiuDeSolutii->getIndexCeaMaiSlabaSolutie(this->spatiuDeSolutii->getCelMaiSlab().getFitness());
                   // this->spatiuDeSolutii->setSolutie(index, dupaMutatie);
                }
            }
        }
        Solutie boss = this->spatiuDeSolutii->getCelMaiTare();
        printf("Cel mai bun: %f\n", this->harta->getDistantaTraseuDupaSolutie(boss));
    }
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


void AlgoritmGenetic::sendSolution(Solutie sol) {
    //std::cout<<"Distanta:"<<ind.GetDistance();
    printf("[Node]Trebuie sa trimit solutia!\n");
    Json::Value obj;
    obj["msg"] = 200;
    SocketDataTransfer::writing(this->socket, obj.toStyledString().c_str());

    Json::Value traseu = this->harta->solutieToJson(sol);
    traseu["distanta"] = harta->getDistantaTraseuDupaSolutie(sol);
    SocketDataTransfer::writing(this->socket, traseu.toStyledString().c_str());
    printf("\n[NODE]AM TRIMIS SOLUTIA OBTINUTA\n");
    printf("%s", traseu.toStyledString().c_str());
}

int AlgoritmGenetic::epochs = 0;