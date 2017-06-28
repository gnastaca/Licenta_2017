//
// Created by Geo on 5/7/2017.
//

#include "Granita.h"
#include <time.h>
#include "../GeneticAlgorithm/Solutie.h"
#include "../GeneticAlgorithm/AlgoritmGenetic.h"

Granita::Granita() {

}

void Granita::connectionBetweenNode(){
    srand((unsigned int)time(NULL));

    WSADATA wsa;
    printf("\n[Transfer]Se initializeaza modulul de asteptare al migrantilor!(granita)\n");
    if (WSAStartup(MAKEWORD(2,2),&wsa) != 0) {
        printf("[Transfer]Failed. Error Code : %d",WSAGetLastError());
    }
    SOCKET s , new_socket;
    struct sockaddr_in server , client;
    int c;
    char *message;
    //Create a socket
    if((s = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET) {
        printf("[Transfer]Initializre socket a esuat!: %d\n" , WSAGetLastError());
    }else {
        printf("[Transfer]Initializarea s-a realizat cu succes!\n");

        //Prepare the sockaddr_in structure
        server.sin_family = AF_INET;
        server.sin_addr.s_addr = INADDR_ANY;
        port = 7000 + (rand() % 100);
        //port = 9000;
        printf("[Transfer]Portul este:%d\n", port);
        server.sin_port = htons(port);

        //Bind
        if (bind(s, (struct sockaddr *) &server, sizeof(server)) == SOCKET_ERROR) {
            printf("[Transfer]Bind failed with error code : %d\n", WSAGetLastError());
            exit(0);
        } else {
            printf("[Transfer]Bind done\n");

            //Listen to incoming connections
            listen(s, 10);

            //Accept and incoming connection
            printf("[Transfer]Suntem pregatiti sa intampinam migranti!\n");


            c = sizeof(struct sockaddr_in);
            Json::Value jRecv;
            Json::Value jSend;
            bool fWhile = true;
            migrant.setNumarOrase(0);
            try {
                while (fWhile) {
                    new_socket = accept(s, (struct sockaddr *) &client, &c);
                    if (new_socket == INVALID_SOCKET) {
                        printf("[Transfer]Accept failed with error code : %d\n", WSAGetLastError());
                    }
                    try {
                        std::string data = SocketDataTransfer::reading(new_socket);
                        jRecv = SocketDataTransfer::stringToJson(data);
                        if(jRecv["cod"].asInt() == 777){
                            AlgoritmGenetic::epochs = 0;
                        }
                        if (flag == true && jRecv["cod"].asInt() == 201){
                            flag = false;
                            printf("[Granita]Am primit cerere de transfer!\n");
                            jSend["cod"] = 200;
                            SocketDataTransfer::writing(new_socket, jSend.toStyledString().c_str());
                            data = SocketDataTransfer::reading(new_socket);
                            printf("[Granita]Am citit solutia trimisa!\n");
                            jRecv = SocketDataTransfer::stringToJson(data);
                            printf("[Granita]Am transformat solutia in json!\n");

                            migrant.setNumarOrase(jRecv["dimensiune"].asInt());
                            migrant.initializareSolutie();
                            migrant.jsonToSolutie(jRecv);

                            printf("[Granita]Am transformat jsonul in solutie!\n");
                            //migrant.afisareSolutie();
                            printf("[Granita]Am cerut inchidere!\n");
                            jSend["cod"] = 210;//close
                            SocketDataTransfer::writing(new_socket, jSend.toStyledString().c_str());
                            printf("[Granita]Am trimis cererea de inchidere!\n");
                        } else {
                            printf("[Granita]Granita este deja ocupata!\n");
                            jSend["cod"] = 300;
                            SocketDataTransfer::writing(new_socket, jSend.toStyledString().c_str());
                        }
                        closesocket(new_socket);
                    } catch (...) {
                        printf("EROARE LA GRANITA\n");
                    }
                }
            }catch(...){
                printf("[EROARE]EROARE LA GRANITA WHILE!!!\n");
            }
        }
        closesocket(s);
        WSACleanup();
    }
}

bool Granita::flag = true;
int Granita::port = 0;
Solutie Granita::migrant = Solutie(0);