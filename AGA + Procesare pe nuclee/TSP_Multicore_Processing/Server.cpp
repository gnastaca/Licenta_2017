//
// Created by Geo on 5/3/2017.
//

#include "Server.h"
#include <stdio.h>
#include <winsock2.h>
#include "SocketDataTransfer.h"
#include "SpatiuDeSolutii.h"
#include "AlgoritmGenetic.h"
#include "Harta.h"
#include <ctime>

Server::Server() {

}

void Server::asteaptaConexiune() {
    WSADATA wsa;
    if (WSAStartup(MAKEWORD(2,2),&wsa) != 0) {
        printf("Failed. Error Code : %d",WSAGetLastError());
    }
    SOCKET s , new_socket;
    struct sockaddr_in server , client;
    int c;
    char *message;
    //Create a socket
    if((s = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET) {
        printf("Could not create socket : %d" , WSAGetLastError());
    }

    printf("Socket created for migration.\n");

    //Prepare the sockaddr_in structure
    server.sin_family = AF_INET;
    server.sin_addr.s_addr = ADDR_ANY;
    server.sin_port = htons(7777);

    //Bind
    if( bind(s ,(struct sockaddr *)&server , sizeof(server)) == SOCKET_ERROR) {
        printf("Bind failed with error code : %d", WSAGetLastError());
    }
    puts("Bind done");

    //Listen to incoming connections
    listen(s , 3);

    //Accept and incoming connection
    puts("Waiting for incoming connections...");
    c = sizeof(struct sockaddr_in);

    bool serverFlag = true;
    Json::Value root;
    while(serverFlag){
        new_socket = accept(s, (struct sockaddr *) &client, &c);
        if (new_socket == INVALID_SOCKET) {
            printf("accept failed with error code : %d", WSAGetLastError());
        }
        printf("\nS-a creat o conexiune!\n");
        root["code"] = 200;
        SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());

        printf("Am trimis 200OK\n");

        //Acum trebuie sa citesc traseul
        int flag = 0;
        do {
            Harta harta;
            try {
                string traseu = SocketDataTransfer::reading(new_socket);
                Json::Value jsonTraseu = SocketDataTransfer::stringToJson(traseu);
                harta.jsonToHarta(jsonTraseu);
                root["code"] = 200;
                SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
            } catch (...) {
                root["code"] = 201;
                SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
            }

            //Acum trebuie sa primesc parametrii
            int populationSize;
            int mutation;
            int tournamentSize;
            bool elitism;
            int epoci;
            int stopCond;
            try {

                string param = SocketDataTransfer::reading(new_socket);
                Json::Value jsonParam = SocketDataTransfer::stringToJson(param);

                populationSize = jsonParam["populationSize"].asInt();
                mutation = jsonParam["mutation"].asInt();
                tournamentSize = jsonParam["tournamentSize"].asInt();
                elitism = jsonParam["elitism"].asBool();
                epoci = jsonParam["epochs"].asInt();
                stopCond = jsonParam["stopCond"].asInt();
                root["code"] = 200;
                SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
            } catch (...) {
                root["code"] = 202;
                SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
            }

            try {
                string start = SocketDataTransfer::reading(new_socket);
                Json::Value jsonStart = SocketDataTransfer::stringToJson(start);

                if (jsonStart["start"].asInt() == 1) {
                    root["code"] = 200;
                    SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
                    SpatiuDeSolutii spatiu(populationSize, harta.getNumarOrase(), harta);
                    spatiu.creareSpatiuDeSolutii();
                    AlgoritmGenetic alg(&spatiu, &harta, epoci, populationSize - mutation, mutation, tournamentSize,
                                        new_socket, stopCond);
                    clock_t begin = clock();
                    Solutie boss = alg.rezolvare();
                    clock_t end = clock();
                    double elapsed_secs = double(end - begin) / CLOCKS_PER_SEC;
                    printf("Timpul total de rulare:%f\n", elapsed_secs);

                    printf("Sefu:%f\n", harta.getDistantaTraseuDupaSolutie(boss));
                    Json::Value rezultat = harta.solutieToJson(boss);
                    rezultat["ep"] = epoci;
                    rezultat["aep"] = epoci;
                    //100 reprezinta ca aceasta este solutia finala
                    rezultat["code"] = 100;
                    SocketDataTransfer::writing(new_socket, rezultat.toStyledString().c_str());
                }
            } catch (...) {
                root["code"] = 203;
                SocketDataTransfer::writing(new_socket, root.toStyledString().c_str());
            }

            //Verificam daca se inchide conexiunea sau mai rezolvam
            try{
                string conditie = SocketDataTransfer::reading(new_socket);
                Json::Value jsonConditie = SocketDataTransfer::stringToJson(conditie);
                flag = jsonConditie["actiune"].asInt();
                if(flag == 0) {
                    closesocket(new_socket);
                    printf("Conexiunea a fost inchisa\n");
                }
            }catch(...){
                printf("[EROARE]Eroare la inchidere conexiune\n");
            }
        }while(flag == 1);
    }
    closesocket(s);
    WSACleanup();
}
