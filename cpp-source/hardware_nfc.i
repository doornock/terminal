%module hardware_nfc

%{
#include "hardware_nfc.h"
%}

%include std_string.i
%include std_except.i
%include stdint.i
%include "arrays_java.i"


//// Exception NFCException

%typemap(throws, throws="java.io.IOException") NFCException {
  jclass excep = jenv->FindClass("java/io/IOException");
  if (excep)
    jenv->ThrowNew(excep, $1.what());
  return $null;
}

// Force the CustomException Java class to extend java.lang.Exception
%typemap(javabase) NFCException "java.lang.Exception";

// Override getMessage()
%typemap(javacode) NFCException %{
  public String getMessage() {
    return what();
  }
%}


//// call sendAPDU byte[] -> ByteArray*

%typemap(jtype) (ByteArray* bytes) "byte[]"
%typemap(jstype) (ByteArray* bytes) "byte[]"
%typemap(jni) (ByteArray* bytes) "jbyteArray"
%typemap(javain) (ByteArray* bytes) "$javainput"

%typemap(in,numinputs=1) (ByteArray* bytes) {
  $1 = new ByteArray(
     (char *) JCALL2(GetByteArrayElements, jenv, $input, NULL),
     (size_t) JCALL1(GetArrayLength, jenv, $input)
  );
}


%typemap(jtype) ByteArray* "byte[]"
%typemap(jstype) ByteArray* "byte[]"
%typemap(jni) ByteArray* "jbyteArray"
%typemap(out) ByteArray* {
  const size_t len = $1->length();
  $result = JCALL1(NewByteArray, jenv, len);
  // TODO: check that this succeeded
  JCALL4(SetByteArrayRegion, jenv, $result, 0, len, (const jbyte*)$1->data());
  
  delete $1;
}
%typemap(javaout) ByteArray* {  
  return $jnicall;  
}

%typemap(freearg) ByteArray* {
  // Or use  0 instead of ABORT to keep changes if it was a copy
  JCALL3(ReleaseByteArrayElements, jenv, $input, (jbyte *) $1, JNI_ABORT);
}

%apply ByteArray* { ( ByteArray* bytes) }


%include "hardware_nfc.h"