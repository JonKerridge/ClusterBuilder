package codeLoaders.testLoaders

import jcsp.lang.CSProcess
import jcsp.net2.NetChannelOutput

class Sender implements CSProcess{

  NetChannelOutput netOut1

  @Override
  void run() {
    println "Sender running"
    for ( i in 1 .. 1000) netOut1.write(new TestMessage(string: "Message number $i"))
    netOut1.write(new TestMessage(string:  "END"))
  }
}
