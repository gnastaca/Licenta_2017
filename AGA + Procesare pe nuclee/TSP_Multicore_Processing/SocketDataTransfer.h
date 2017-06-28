//
// Created by Geo on 3/16/2017.
//

#ifndef CLIENT_SOCKETDATATRANSFER_H
#include <winsock2.h>
#include <string>
#include "json/json.h"
#define CLIENT_SOCKETDATATRANSFER_H
using namespace std;

class SocketDataTransfer {
public:
    static void writing(SOCKET s, const char *data) {
        char len[4];
        unsigned int n = strlen(data);
       // printf("[Lungime trimisa]%d\n",n);

        len[0] = (n >> 24) & 0xFF;
        len[1] = (n >> 16) & 0xFF;
        len[2] = (n >> 8) & 0xFF;
        len[3] = n & 0xFF;
        send(s, len, 4, 0);
        send(s, data, strlen(data), 0);
    }

    static string reading(SOCKET s) {
        //cout<<"Vreau sa citesc!"<<endl;
        char len[4];
        recv(s, len, 4, 0);
        unsigned int n = 0;
        unsigned int x = 0;
//        n = n | len[0];
//        n = ((n << 8) | len[1]) & 0xFF;
//        n = ((n << 8) | len[2]) & 0xFF;
//        n = ((n << 8) | len[3]) & 0xFF;;

        n = int((unsigned char)(len[0]) << 24 |
                    (unsigned char)(len[1]) << 16 |
                    (unsigned char)(len[2]) << 8 |
                    (unsigned char)(len[3]));
        //cout <<"[Lungime]"<<n<<endl;


        char text[n+1];
        recv(s, text, n, 0);
        //cout<<"TEXT:"<<text<<endl;
        text[n] = '\0';
        return string(text);
    }

    static Json::Value stringToJson(std::string str) {
        Json::Value root;   // will contains the root value after parsing.
        Json::Reader reader;
        bool parsingSuccessful = reader.parse( str.c_str(), root );
        if ( !parsingSuccessful )
        {
            printf("Parsarea a esuat!");
        }
        return root;
    }
};


#endif //CLIENT_SOCKETDATATRANSFER_H
