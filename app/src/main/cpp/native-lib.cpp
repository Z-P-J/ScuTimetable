/**
 * aes加密
 */
#include <jni.h>
#include <string>
#include <iostream>
#include "utils/LogUtils.h"
#include "utils/AES.h"
#include "utils/encryption.h"
#include <typeinfo>
extern "C" JNIEXPORT jstring JNICALL
//Java_com_scu_timetable_utils_EncryptionUtils_encryptByAES(
//        JNIEnv* env,
//        jclass type,
//        jstring data,
//        jstring secretKey,
//        jstring iv,
//        jstring iMode){
//    const char * c_data = env->GetStringUTFChars(data, 0);
//    const char * c_secret_key = env->GetStringUTFChars(secretKey, 0);
//    const char * c_iv = env->GetStringUTFChars(iv, 0);
//    const char * c_mode = env -> GetStringUTFChars(iMode, 0);
////    string encrypt_data = encryptByAES(c_data, c_secret_key, c_iv, getModeByName(c_mode));
//    string encrypt_data = encryptByAES(c_data, "rdntbgriodlkhgrbidtkfjhftg", "1234567899876543", AES::CFB);
//    return env->NewStringUTF(encrypt_data.c_str());
//}

Java_com_scu_timetable_utils_EncryptionUtils_encryptByAES(
        JNIEnv* env,
        jclass type,
        jstring data){
    const char * c_data = env->GetStringUTFChars(data, 0);
    string encrypt_data = encryptByAES(c_data, "rdntbgriodlkhgrbidtkfjhftg", "1234567899876543", AES::ECB);
    return env->NewStringUTF(encrypt_data.c_str());
}

//extern "C" JNIEXPORT jstring JNICALL
//Java_com_scu_timetable_utils_EncryptionUtils_decryptByAES(
//        JNIEnv* env,
//        jclass type,
//        jstring data,
//        jstring secretKey,
//        jstring iv,
//        jstring iMode){
//    const char * c_data = env->GetStringUTFChars(data, 0);
//    const char * c_secret_key = env->GetStringUTFChars(secretKey, 0);
//    const char * c_iv = env->GetStringUTFChars(iv, 0);
//    const char * c_mode = env -> GetStringUTFChars(iMode, 0);
////    string decrypt_data = decryptByAES(c_data, c_secret_key, c_iv, getModeByName(c_mode));
//    string decrypt_data = decryptByAES(c_data, "rdntbgriodlkhgrbidtkfjhftg", "1234567899876543", AES::CFB);
//    return env->NewStringUTF(decrypt_data.c_str());
//}

extern "C" JNIEXPORT jstring JNICALL
Java_com_scu_timetable_utils_EncryptionUtils_decryptByAES(
        JNIEnv* env,
        jclass type,
        jstring data){
    const char * c_data = env->GetStringUTFChars(data, 0);
    string decrypt_data = decryptByAES(c_data, "rdntbgriodlkhgrbidtkfjhftg", "1234567899876543", AES::ECB);
    return env->NewStringUTF(decrypt_data.c_str());
}

int getModeByName(const char * iModeName){
    if(strcmp("CFB", iModeName) == 0){
        return AES::CFB;
    }else if(strcmp("ECB", iModeName) == 0){
        return AES::ECB;
    }else{
        return AES::CBC;
    }
}
