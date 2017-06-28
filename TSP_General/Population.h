//
// Created by Geo on 2/7/2017.
//

#ifndef TSP_GA_SINGLETHREAD_POPULATION_H
#define TSP_GA_SINGLETHREAD_POPULATION_H
#include <set>
#include "Individual.h"
using namespace std;

class Population {
private:
    vector<Individual> population;
    int Individuals;
public:
    Population(int NumberOfIndividuals, Map *map){
        while(this->population.size() < NumberOfIndividuals){
            Individual ind =  Individual(map);
            this->population.push_back(ind);
        }
    }

    Population(int NumberOfIndividuals, int IndividualSize){
        this->Individuals = NumberOfIndividuals;
        for(int i = 0; i < this->Individuals; i++) {
            Individual ind = Individual(IndividualSize);
            this->population.push_back(ind);
        }

    }

    ~Population() {
        this->population.clear();
    }

    void CopyPopulation(Population * pop){
        this->population.clear();
        vector<Individual>::iterator it = pop->population.begin();
        while(it != pop->population.end()){
            this->population.push_back(*it);
            it++;
        }
        this->Individuals = pop->Individuals;
    }

    Individual GetIndividual(int index){
        return this->population[index];
    }

    void SetIndividual(int index, Individual ind){
        this->population[index] = ind;
    }

    Individual GetBestIndividual(){
        int i = 0;
        Individual BestIndividual = this->population[i];

        vector<Individual>::iterator it =  this->population.begin();
        int index = 0;
        while(it != this->population.end()) {
            if (BestIndividual.GetFitness() <= it->GetFitness()) {
                BestIndividual = *it;
                i = index;
            }
            it++;
            index ++;
        }
        return BestIndividual;
    }

    Individual GetNBestIndividual(){
        int i = 0;
        Individual BestIndividual = this->population[i];

        vector<Individual>::iterator it =  this->population.begin();
        int index = 0;
        while(it != this->population.end()) {
            if (BestIndividual.GetFitness() <= it->GetFitness()) {
                BestIndividual = *it;
                i = index;
            }
            it++;
            index ++;
        }
        this->population.erase(this->population.begin()+i);
        return BestIndividual;
    }

    void showPopulation(){
        for(int i = 0; i < this->PopulationSize(); i++){
            this->population[i].PrintIndividual();
        }
    }
    int PopulationSize(){
        return this->population.size();
    }
};


#endif //TSP_GA_SINGLETHREAD_POPULATION_H
