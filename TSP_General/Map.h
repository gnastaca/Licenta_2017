//
// Created by Geo on 2/7/2017.
//

#ifndef TSP_GA_SINGLETHREAD_MAP_H
#define TSP_GA_SINGLETHREAD_MAP_H

#include <vector>
#include "Location.h"
using namespace std;

class Map {
public:
    vector<Location> locations;

    void AddLocation(Location location){
        this->locations.push_back(location);
    }

    Location GetLocationByIndex(int index){
        return this->locations[index];
    }

    void PrintMap(){
        vector<Location>::iterator it = locations.begin();
        while(it != this->locations.end()){
            printf("[x -> %f, y -> %f]\n",it->GetXCoordinate(), it->GetYCoordinate());
            it++;
        }
    }

   int MapDimension(){
        return this->locations.size();
    }
};


#endif //TSP_GA_SINGLETHREAD_MAP_H
