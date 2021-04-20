#include <jni.h>
#include <iostream>
using namespace std;

template <class T>
void callFunctionInJava(JNIEnv *env, string functionName, string functionType, T classname, T javaFunctionName){
    jclass classPtr = env->FindClass("bootstrap");
    if (classPtr == nullptr){
        cerr << "ERROR: class not found!\n";
    }
    else{
        jmethodID mid = env->GetStaticMethodID(classPtr, functionName.c_str(), functionType.c_str());
        if (mid == nullptr){
            cerr << "ERROR: method not found!\n";
        }
        else{
            env->CallObjectMethod(classPtr, mid, classname, javaFunctionName);
            cout << endl;
        }
    }
}

template <class T>
void callVoidFunctionFromBootstrap(JNIEnv *env, string functionName, string functionType, T arr){
    jclass classPtr = env->FindClass("bootstrap");
    if (classPtr == nullptr){
        cerr << "ERROR: class not found!\n";
    }
    else{
        jmethodID mid = env->GetStaticMethodID(classPtr, functionName.c_str(), functionType.c_str());
        if (mid == nullptr){
            cerr << "ERROR: method not found!\n";
        }
        else{
            env->CallObjectMethod(classPtr, mid, arr);
            cout << endl;
        }
    }
}

template <class parameterType, class returnType>
returnType callFunctionFromBootstrap(JNIEnv *env, string functionName, string functionType, parameterType arr){
    returnType result = NULL;

    jclass classPtr = env->FindClass("bootstrap");
    if (classPtr == nullptr){
        cerr << "ERROR: class not found!\n";
    }
    else{
        jmethodID mid = env->GetStaticMethodID(classPtr, functionName.c_str(), functionType.c_str());
        if (mid == nullptr){
            cerr << "ERROR: method not found!\n";
        }
        else{
            result = env->CallObjectMethod(classPtr, mid, arr);
            cout << endl;
        }
    }
    return result;
}

int main(){
    JavaVM *jvm;
    JNIEnv *env;

    JavaVMInitArgs vm_args;
    JavaVMOption *options = new JavaVMOption[1];
    options[0].optionString = (char *)"-Djava.class.path=.";
    vm_args.version = JNI_VERSION_1_6;
    vm_args.nOptions = 1;
    vm_args.options = options;
    vm_args.ignoreUnrecognized = false;

    jint rc = JNI_CreateJavaVM(&jvm, (void **)&env, &vm_args);
    delete options;
    if (rc != JNI_OK){
        cin.get();
        exit(EXIT_FAILURE);
    }

    jint ver = env->GetVersion();
    cout << "JVM load succeeded: Version " << ((ver >> 16) & 0x0f) << "." << (ver & 0x0f) << endl
         << endl;

    //Main to create a path string array and call functions
    jobjectArray arr = env->NewObjectArray(2, env->FindClass("java/lang/String"), env->NewStringUTF(""));
    env->SetObjectArrayElement(arr, 0, env->NewStringUTF("./Test.java"));
    env->SetObjectArrayElement(arr, 1, env->NewStringUTF("../Test1.java"));

    jobject handleArr = callFunctionFromBootstrap<jobjectArray, jobject>(env, "loadFromFile", "([Ljava/lang/String;)[Ljava/lang/String;", arr);

    callVoidFunctionFromBootstrap<jstring>(env, "DiscoverData", "(Ljava/lang/String;)V", env->NewStringUTF("Test"));
    callVoidFunctionFromBootstrap<jstring>(env, "DiscoverData", "(Ljava/lang/String;)V", env->NewStringUTF("Test1"));

    callFunctionInJava<jstring>(env, "callFunction", "(Ljava/lang/String;Ljava/lang/String;)V", env->NewStringUTF("Test"), env->NewStringUTF("main") );
    callFunctionInJava<jstring>(env, "callFunction", "(Ljava/lang/String;Ljava/lang/String;)V", env->NewStringUTF("Test1"), env->NewStringUTF("main") );


    env->DeleteLocalRef(arr);
    jvm->DestroyJavaVM();
    return 0;
}