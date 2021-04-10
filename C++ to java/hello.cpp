#include <jni.h>
#include <iostream>
using namespace std;

int main(){
  JavaVM *jvm;
  JNIEnv *env;

  JavaVMInitArgs vm_args;
  JavaVMOption* options = new JavaVMOption[1];
  options[0].optionString = (char*)"-Djava.class.path=.";
  vm_args.version = JNI_VERSION_1_6;
  vm_args.nOptions = 1;
  vm_args.options = options;
  vm_args.ignoreUnrecognized = false;

  jint rc = JNI_CreateJavaVM(&jvm, (void**)&env, &vm_args);
  delete options;
  if (rc != JNI_OK) {
        cin.get();
        exit(EXIT_FAILURE);
  }

  jclass cls2 = env->FindClass("HelloWorld");
  if(cls2 == nullptr) {
      cerr << "ERROR: class not found !";
  }
  else {
      jmethodID mid = env->GetStaticMethodID(cls2, "mymain", "()V");
      if(mid == nullptr)
          cerr << "ERROR: method void mymain() not found !" << endl;
      else {
          env->CallStaticVoidMethod(cls2, mid);
          cout << endl;
      }
  }

  jvm->DestroyJavaVM();
  cin.get();
  return 0;
}