//
// Created by Geo on 5/7/2017.
//

/**
 * Se initializeaza subpopulatia si se trimit datele necesare pentru a se crea conexiunea
 * @return
 */

#ifndef MAIN_CPP
#define MAIN_CPP
#include "Cluster/Node.h"
#include "Cluster/Granita.h"
#include <time.h>
#include <thread>

void resetConnection(Node * node){
    bool flag = 1;
    while(flag){
        Sleep(15000);
        std::cout<<"Status:"<<Node::status<<std::endl;
        if(Node::status == 0){
            node->restoreConnection = true;
            node->closeSocketNode();
            //node->InitConnection();
            //node->restoreConnection = false;
        }
    }
}

int main(){
    srand((unsigned int)time(NULL));
    std::thread t1(&Granita::connectionBetweenNode, Granita());
    Sleep(1000);
    printf("\n[CONECTARE NOD SERVER]Introduceti adresa ip a serverului:");
    char ip[16];
    std::cin.get(ip,16);
    Node *node = new Node(7777,ip);

    if(node->InitConnection() == 1){
        printf("[CONECTARE NOD SERVER]Crearea conexiunii a esuat!!!\n");
        exit(-1);
    }
   // std::thread t2(resetConnection, node);
    node->Comunication();
}

#endif