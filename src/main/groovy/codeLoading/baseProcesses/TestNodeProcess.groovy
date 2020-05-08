package codeLoading.baseProcesses

import jcsp.lang.CSProcess

class TestNodeProcess implements CSProcess{
  @Override
  void run() {
    println "Node TestProcess Running"
  }
}
