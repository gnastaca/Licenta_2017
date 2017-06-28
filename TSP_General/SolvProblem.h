//
// Created by Geo on 2/7/2017.
//

#ifndef TSP_GA_SINGLETHREAD_SOLVPROBLEM_H
#define TSP_GA_SINGLETHREAD_SOLVPROBLEM_H
#include "Population.h"

class SolvProblem {
private:
    double MutationRate;
    int TournamentSize, epochs;
    bool Elitism;
    Map * map;
    int IndividualSize = 0;
    int BestIndivids;
public:
    Population *population;
    SolvProblem(double MutationRate, int TournamentSize, bool Elitism,int nr_best_ind, Population *pop, Map * map, int epochs){
        this->MutationRate = MutationRate;
        this->TournamentSize = TournamentSize;
        this->Elitism = Elitism;
        this->population = pop;
        this->map = map;
        this->epochs = epochs;
        this->IndividualSize = this->population->GetIndividual(0).IndividualSize();
        this->BestIndivids = nr_best_ind;
    }


    Individual EvolvePopulation(){
        for(int k = 0; k < epochs; k++) {
            Population *NewPopulation = new Population(this->population->PopulationSize(),this->IndividualSize);
            NewPopulation->SetIndividual(0, this->population->GetBestIndividual());
            for(int i = 1;  i < this->population->PopulationSize();i++){
                Individual parentI = Tournament();
                Individual parentII = Tournament();
                Individual child = CrossOverI(parentI,parentII);
                child = this->Mutate(child);
                NewPopulation->SetIndividual(i, child);
            }
                printf("%d %d\n",k,this->population->GetBestIndividual().GetDistance());
            delete NewPopulation;
        }
        return this->population->GetBestIndividual();
    }

    Individual CrossOverI(Individual parentI, Individual parentII){
        int start = rand() % parentI.IndividualSize();
        int end = rand() % parentI.IndividualSize();

        if(end > start) {
            int aux = end;
            end = start;
            start = aux;
        }

        Individual child = Individual(parentI.IndividualSize());
        for(int i = start; i <= end; i++)
            child.InsertLocation(i, parentI.GetLocation(i));

        for(int i = 0; i < parentII.IndividualSize(); i++){
            if(child.ExistLocation(parentII.GetLocation(i)) == 0){
                for(int j = 0; j < child.IndividualSize(); j++)
                    if(child.GetLocation(j).GetXCoordinate() == -1) {
                        child.InsertLocation(j, parentII.GetLocation(i));
                        break;
                    }
            }
        }
        return child;
    }

    Individual CrossOverII(Individual parentI, Individual parentII, int CrossOverSplite);

    Individual CrossOverIII(Individual parentI, Individual parentII);


    Individual Mutate(Individual individual){
        std::random_device rd;
        std::mt19937 gen(time(NULL));
        std::uniform_real_distribution<> dis(0, 1);
        for(int i = 0;  i < individual.IndividualSize(); i++){
            if(this->MutationRate > dis(gen)){
                int changeWith = rand() % individual.IndividualSize();
                Location loc1 = individual.GetLocation(i);
                Location loc2 = individual.GetLocation(changeWith);

                individual.InsertLocation(i,loc2);
                individual.InsertLocation(changeWith,loc1);
            }
        }
        return  individual;
    }

    Individual Tournament(){
        Population players = Population(this->TournamentSize, this->population->GetIndividual(0).IndividualSize());
        for(int i = 0; i < this->TournamentSize; i++){
            players.SetIndividual(i,this->population->GetIndividual(rand()%this->population->PopulationSize()));
        }
        return players.GetBestIndividual();
    }
};


Individual SolvProblem::CrossOverII(Individual parentI, Individual parentII, int CrossOverSplite) {
    Individual child(parentI.IndividualSize());
    Individual ReParent(parentI.IndividualSize());
    ReParent.Copy(parentI);
    int index;
    for(int i = 0; i < CrossOverSplite; i++){
        Location aux = ReParent.GetLocation(i);
        index =  ReParent.GetIndexByLocation(parentII.GetLocation(i));
        ReParent.InsertLocation(i,parentII.GetLocation(i));
        ReParent.InsertLocation(index,aux);
    }

    for(int i = 0; i < parentI.IndividualSize(); i++){
        if(i < CrossOverSplite)
            child.InsertLocation(i,parentII.GetLocation(i));
        else
            child.InsertLocation(i,ReParent.GetLocation(i));
    }
    return child;
}


Individual SolvProblem::CrossOverIII(Individual parentI, Individual parentII) {
    Individual child(parentI.IndividualSize());
    parentI.PrintIndividual();
    parentII.PrintIndividual();
    int deLaCineSeColecteazaGene;
    int numarGeneColectate = 0;
    int cateGeneSeColecteaza, ok = 0;
    while(ok == 0){
        //De la ce indiv se preia mai intai gene (tata/mama)
        deLaCineSeColecteazaGene = rand() % 2;
        cateGeneSeColecteaza = rand() % (parentI.IndividualSize()/4);
        printf("\nde la cine se colecteaza %d\n",deLaCineSeColecteazaGene);
        printf("Cate gene colectez:%d\n",cateGeneSeColecteaza);

        for(int i = numarGeneColectate; i < numarGeneColectate + cateGeneSeColecteaza; i++){
            if(i == parentI.IndividualSize()) {
                ok = 1;
                break;
            }
            if(deLaCineSeColecteazaGene == 0){
                child.InsertLocation(i,parentI.GetLocation(i));
            }else{
                child.InsertLocation(i,parentII.GetLocation(i));
            }
        }
        numarGeneColectate += cateGeneSeColecteaza;
    }
    child.PrintIndividual();
    exit(0);
    return child;
}

#endif //TSP_GA_SINGLETHREAD_SOLVPROBLEM_H
