//
// Created by Geo on 2/7/2017.
//

#ifndef TSP_GA_SINGLETHREAD_LOCATION_H
#define TSP_GA_SINGLETHREAD_LOCATION_H

#include <random>
#include <time.h>
#include <stdio.h>

class Location {
private:
    double x;
    double y;
public:
    Location(double x, double y){
        this->x = x;
        this->y = y;
    }

    Location(){
        this->x = rand() % 300;
        this->y = rand() % 300;
    }

    double GetXCoordinate()const{
        return this->x;
    }

    double GetYCoordinate()const{
        return this->y;
    }

    double DistanceToLocation(Location location){
        double distance;
        distance =  sqrt(pow((this->x - location.GetXCoordinate()),2) + pow((this->y - location.GetYCoordinate()),2));
        return distance;
    }

    void ShowCoordinate(){
        printf("x = %f, y = %f| ", this->x,this->y);
    }
};


#endif //TSP_GA_SINGLETHREAD_LOCATION_H
