package codeLoaders.testLoaders

import jcsp.lang.CSProcess
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class Copy implements CSProcess{

  NetChannelInput  netIn1
  NetChannelOutput netOut1

  @Override
  void run() {
    println " Copy running"
    TestMessage message = netIn1.read()
    while ( message.string != "END"){
      netOut1.write(message)
      message = netIn1.read()
    }
    netOut1.write(message)
  }
}
