#pragma once
#ifndef ENCYPTION
#define ENCYPTION
#include <string>
using namespace std;
string encryptByAES(const char* data, const char* secretKey, const char* iv, int iMode);
string decryptByAES(const char* data, const char* secretKey, const char* iv, int iMode);
int getModeByName(const char * iModeName);

#endif

