//
// Created by Geo on 5/7/2017.
//

#include "Node.h"
#include "Granita.h"
#include <Ws2tcpip.h>
#include <thread>

/**
 * Initializarea conexiunii cu serverul
 * @return
 */
int Node::InitConnection() {
    WSADATA wsa;
    struct sockaddr_in server;

    printf("[CONECTARE NOD SERVER]Initializare socket!\n");
    if (WSAStartup(MAKEWORD(2,2),&wsa) != 0)
    {
        printf("[CONECTARE NOD SERVER]Failed. Error Code : %d\n",WSAGetLastError());
        return 1;
    }

    //Create a socket
    if((this->sock = socket(AF_INET , SOCK_STREAM , 0 )) == INVALID_SOCKET)
    {
        printf("[CONECTARE NOD SERVER]Socketul nu a putut fi creat: %d\n" , WSAGetLastError());
    }
    char optval[] = {"1234567"};
    int x = setsockopt(this->sock, SOL_SOCKET, SO_KEEPALIVE, optval, (int) 7); // size of input buffer
    if(x == 0){
        printf("[KEEPALIVE]OK\n");
    }else{
        printf("[KEEPALIVE]NOT OK\n");
    }

    server.sin_addr.s_addr = inet_addr(this->ip);
    server.sin_family = AF_INET;
    server.sin_port = htons(this->port);

    //Connect to remote server
    if (connect(this->sock , (struct sockaddr *)&server , sizeof(server)) < 0) {
        printf("[CONECTARE NOD SERVER]Eroare la conectarea cu serverull!\n");
        return 1;
    }else{
        Json::Value p;
        p["port"] = Granita::port;
        //printf("Portul pe care il trimit este:%d\n", p["port"].asInt());
        SocketDataTransfer::writing(this->sock, p.toStyledString().c_str());
    }
    return 0;
}

void Node::PrintRecived(Json::Value json) {
    std::cout<<"[Server]"<<json["msg"]<<std::endl;
}

Json::Value Node::stringToJson(std::string str) {
    Json::Value root;   // will contains the root value after parsing.
    Json::Reader reader;
    bool parsingSuccessful = reader.parse( str.c_str(), root );
    if( !parsingSuccessful ) {
        printf("[EROARE]STRING TO JSON ERRORDIN NODE\n");
        throw std::invalid_argument( "[Error]Eroare intampinata la conversie din string in json!" );
    }
    return root;
}

/**
 * Bucla in care se trimit si se primesc mesajele
 * Aceasta functie coordoneaza intraga subpopulatie
 */
void Node::Comunication() {
    char data[100];

    Json::Value obj;
    obj["function_code"] = 0;
    obj["msg"] = "Saluare";
    Json::Value recived;

    int populationSize = 0;
    int mutation = 0;
    int tournament = 0;
    int epochs = 0;
    int stopCond = 0;

    Harta harta;

    try {
        while (true) {
            try{
                std::string str;
                str = SocketDataTransfer::reading(this->sock);
                printf("###############################################\n");
                printf("%s",str.c_str());
                printf("\n###############################################\n");
                recived = this->stringToJson(str);
            }catch(const std::invalid_argument& e){
                obj["msg"] = 99;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());

                closesocket(this->sock);
                this->InitConnection();
                printf("[Eroare]Am prins eroarea din stringToJson\n");
            }catch(...){
                printf("[Node]O eroare exceptionala a aparut in node!\n");
            }


            //Se realizeaza strangerea de mana
            if (recived["function_code"] == 100) {
                obj["msg"] = "OK";
                std::cout <<"[Cluster Manager]Check connection!"<<std::endl;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            //Verificare conexiune
            if (recived["function_code"] == 0) {
                this->PrintRecived(recived);
                obj["msg"] = "[SUCCES]Connection";
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            //Se seteaza numele clientului
            if (recived["function_code"] == 1) {
                this->name = recived["msg"].toStyledString();
                std::cout<<"[Server]Setare nume-"<<this->name;
                obj["msg"] = 200;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            //Initializare harta
            try {
                if (recived["function_code"] == 2) {
                    std::cout << "[Server]Initializeaza harta." << std::endl;
                    harta.curataHarta();
                    if(harta.jsonToHarta(recived) == true)
                        obj["msg"] = 200;
                    else
                        obj["msg"] = 202;
                    SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
                    std::cout << "[Server]Am initializat harta." << std::endl;
                }
            }catch(...){
                obj["msg"] = 202;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }
            try{
                if (recived["function_code"] == 3) {
                    std::cout<<"[Server]Setare parametrii algoritm genetic."<<std::endl;
                    //Parametrii algoritm genetic
                    populationSize = recived["populationSize"].asInt();
                    mutation = recived["mutation"].asInt();
                    tournament = recived["tournamentSize"].asInt();
                    epochs = recived["epochs"].asInt();
                    stopCond = recived["stopCond"].asInt();

//                Migration migr = Migration();
//                migr.setAdressOfPCFromCluste(this->pcOfCluster);
//                this->SP.setMigration(migr);
                    obj["msg"] = 200;
                    SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
                }
            }catch(...) {
                obj["msg"] = 203;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            try{
                if (recived["function_code"] == 4) {
                    std::cout << "[Cluster Manager] Start genetic algorithm!!!";
                    SpatiuDeSolutii spatiu(populationSize, harta.getNumarOrase(), harta);
                    spatiu.creareSpatiuDeSolutii();
                    AlgoritmGenetic AG(&spatiu, &harta, epochs, spatiu.getDimensiuneSpatiu() - mutation, mutation, tournament, this->sock, this->pcOfCluster, stopCond);
                    Granita::flag = true;
                    AG.rezolvare();
                }
            }catch(...){
                obj["msg"] = 204;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            try {
                if (recived["function_code"] == 5) {
                    std::cout << "[Server]Lista pc-uri cluster." << std::endl;
                    std::cout << recived.toStyledString() << std::endl;
                    this->pcOfCluster = recived;
                    obj["msg"] = 200;
                    SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
                }
            }catch(...){
                obj["msg"] = 205;
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
            }

            if (recived["function_code"] == 6) {
                closesocket(this->sock);
                this->InitConnection();
            }

            if (recived["function_code"] == 7) {
                AlgoritmGenetic::epochs = 0;
            }

            if (recived["function_code"] == -1) {
                this->PrintRecived(recived);
                obj["msg"] = "[SUCCES]Am inchis.";
                SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
                break;
            }
        }
    }catch(...){
        obj["msg"] = "[ERROR] Function" + recived["function_code"].toStyledString();
        SocketDataTransfer::writing(this->sock, obj.toStyledString().c_str());
    }

}

void Node::Test(){
    char text[2000];
    std::cout<<"Introduceti textul:";
    std::cin.get(text,2000);
    SocketDataTransfer::writing(this->sock, text);
    std::string str =  SocketDataTransfer::reading(this->sock);
    std::cout<<str;
    closesocket(this->sock);
}


void Node::setNonBlock() {
    int iResult;
    u_long iMode = 1;

    iResult = ioctlsocket(this->sock, FIONBIO, &iMode);
    if (iResult != NO_ERROR)
        printf("ioctlsocket failed with error: %ld\n", iResult);

}

void Node::closeSocketNode() {
    closesocket(this->sock);
}
bool Node::status = false;
bool Node::restoreConnection = false;

