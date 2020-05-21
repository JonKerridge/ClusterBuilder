package codeLoaders.testLoaders

import jcsp.lang.CSProcess
import jcsp.net2.NetChannelInput

class Receiver implements CSProcess {

  NetChannelInput netIn1

  @Override
  void run() {
    println "Receiver running"
    TestMessage message = netIn1.read()
    while ( message.string != "END") {
      println "Received: ${message.string}"
      message = netIn1.read()
    }
  }
}
