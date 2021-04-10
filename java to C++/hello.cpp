#include <jni.h>
#include <iostream>
#include "HelloWorldJNI.h"

using namespace std;

JNIEXPORT void JNICALL Java_HelloWorldJNI_sayHello(JNIEnv *env, jobject thisObject){
  cout << "Hello from C++ !!" << endl;
}