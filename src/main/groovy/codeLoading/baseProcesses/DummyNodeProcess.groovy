package codeLoading.baseProcesses

import jcsp.lang.CSProcess

class DummyNodeProcess implements CSProcess {

  @Override
  void run() {
    println "DummyNode Process running"
  }
}
