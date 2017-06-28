//
// Created by Geo on 2/7/2017.
//

#ifndef TSP_GA_SINGLETHREAD_INDIVIDUAL_H
#define TSP_GA_SINGLETHREAD_INDIVIDUAL_H

#include <vector>
#include "Location.h"
#include "Map.h"
#include <algorithm>
using namespace std;

class Individual {
private:
    int distance = 0;
    double fitness = 0;
    vector<Location> individual;

public:
    Individual(Map *map){
        vector<Location>::iterator it = map->locations.begin();
        while(it != map->locations.end()){
            this->individual.push_back(*it);
            it++;
        }
        int nr = rand()%7 + 3;
        int i = 0;
        while(i < nr){
            random_shuffle(this->individual.begin(), this->individual.end());
            i++;
        }
    }

    Individual(int Size){
        for(int i = 0; i < Size; i++) {
            Location l = Location(-1,-1);
            this->individual.push_back(l);
        }
    }

    void Copy(Individual ind){
        this->individual.clear();
        vector<Location>::iterator it = ind.individual.begin();

        while(it != ind.individual.end()){
           this->individual.push_back(*it);
            it++;
        }
    }

    void PrintIndividual(){
        vector<Location>::iterator it = this->individual.begin();
        while(it != this->individual.end()){
            printf("%f-%f|", it->GetXCoordinate(), it->GetYCoordinate());
            it++;
        }
        printf("\n");
    }

    Location GetLocation(int index){
        return this->individual[index];
    }

    void InsertLocation(int index, Location location){
        this->fitness = 0;
        this->distance = 0;
        this->individual[index] = location;
    }

    int GetDistance(){
        if(distance == 0){
            vector<Location>::iterator it =  this->individual.begin();
            while(it !=  this->individual.end()-1){
                distance += it->DistanceToLocation(*(it+1));
                it++;
            }
            distance += this->individual[0].DistanceToLocation(this->individual[this->individual.size()-1]);
        }
        return distance;
    }

    double GetFitness(){
        if (fitness == 0){
            return 1 / (double)this->GetDistance();
        }
    }

    bool ExistLocation(Location location){
        vector<Location>::iterator it =  this->individual.begin();
        int ok = 0;
        while(it !=  this->individual.end()){
            if(it->GetXCoordinate() == location.GetXCoordinate() and it->GetYCoordinate() == location.GetYCoordinate())
                ok = 1;
            if(ok == 1)
                break;
            it++;
        }
        if(ok == 1)
            return 1;
        else
            return 0;
    }

    int IndividualSize(){
        return this->individual.size();
    }

    void ClearIndividual(){
        this->individual.clear();
    }
    int GetIndexByLocation(Location loc){
        vector<Location>::iterator it = this->individual.begin();

        int index = 0;
        while(it != this->individual.end()){
            if(it->GetXCoordinate() == loc.GetXCoordinate() and it->GetYCoordinate() == loc.GetYCoordinate())
                return index;
            index++;
            it++;
        }
        return -1;
    }
};


#endif //TSP_GA_SINGLETHREAD_INDIVIDUAL_H
