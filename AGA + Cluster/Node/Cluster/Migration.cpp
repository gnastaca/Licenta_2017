//
// Created by Geo on 5/7/2017.
//

#include "Migration.h"

Migration::Migration(Json::Value pcOfCluster) {
    this->pcOfCluster = pcOfCluster;
}
void Migration::addMigrant(Solutie migrant) {
    this->migrants.push_back(migrant);
}

Solutie Migration::getMigrant(int index) {
    return this->migrants[index];
}

void Migration::setNumberOfMigrants(int nr) {
    this->migrants.clear();
    this->n = nr;
}

Json::Value Migration::toJson(){
    Json::Value obj;
    obj["function_code"] = 1;
    obj["numberOfmigrants"] = this->migrants.size();
    obj["msg"] = "[Migrants]Migrantii au fost expediati\n";
    //cout <<"Migrants size:"<< this->migrants.size();
//    for(int i = 0; i < this->migrants.size(); i++){
//        obj[std::to_string(i)] = this->migrants[i].toString();
//    }
    return obj;
}

void Migration::setAdressOfPCFromCluste(Json::Value ips) {
    this->pcOfCluster = ips;
}
int Migration::startMigration(){
    printf("\nSunt in start migration\n");
    int nr = this->pcOfCluster["nrPcCluster"].asInt();
    printf("\nNumar pc cluster:%d\n",nr);
    SOCKET s;

    int pc = rand() % nr;
    printf("Migrez catre nodul:%d\n",pc);
    std::string ip = this->pcOfCluster[std::to_string(pc)].asString();
    std::string prt;
    prt.append("port_");prt.append(std::to_string(pc));
    int port = this->pcOfCluster[prt].asInt();
    printf("IP-ul:%s\n",ip.c_str());
    printf("port:%d\n",port);
    SOCKADDR_IN target; //Socket address information
    target.sin_family = AF_INET; // address family Internet
    target.sin_port = htons (port); //Port to connect on
    target.sin_addr.s_addr = inet_addr (ip.c_str()); //Target IP

    printf("[Migrare]Am initializat datele necesare pentru crearea socketului!!!\n");
    s = socket (AF_INET, SOCK_STREAM, IPPROTO_TCP); //Create socket
    if (s == INVALID_SOCKET)
    {
        printf("Couldn't create the socket\n");
        return 0;
    }

    printf("[Migrare]Am creat socketul\n");
    //Try connecting...
    try {
        printf("[Migrare]Astept sa se realizeze connect!\n");
        if (connect(s, (SOCKADDR *)&target, sizeof(target)) == SOCKET_ERROR)
        {
            printf("[Atentionare] Conectarea pentru migrare a esuat!\n");//
        }else {
            Json::Value sendM;
            sendM["cod"] = 201;
            SocketDataTransfer::writing(s, sendM.toStyledString().c_str());
            std::string str = SocketDataTransfer::reading(s);
            Json::Value js = SocketDataTransfer::stringToJson(str);

            if (js["cod"].asInt() == 300) {
                closesocket(s);
            } else {
                printf("[Migrare]Transform solutia in json!!!\n");
                js = this->migrants[0].solutieToJson();
                printf("[Migrare]Trimit solutia la granita\n");
                SocketDataTransfer::writing(s, js.toStyledString().c_str());

                str = SocketDataTransfer::reading(s);
                js = SocketDataTransfer::stringToJson(str);
                printf("[Migrare]Citesc codul de inchidere: %d\n",js["cod"].asInt());
                if (js["cod"].asInt() == 210) {
                    closesocket(s);
                }
                printf("[Migrare]Am terminat cu migrarea!!!\n");
            }
        }
    }catch(...){
        printf("EROARE DUPA CONNECT MIGRARE...\n");
    }
}