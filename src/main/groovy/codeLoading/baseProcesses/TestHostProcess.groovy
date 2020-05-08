package codeLoading.baseProcesses

import jcsp.lang.CSProcess

class TestHostProcess implements CSProcess{
  @Override
  void run() {
    println "Host TestProcess Running"
  }
}
