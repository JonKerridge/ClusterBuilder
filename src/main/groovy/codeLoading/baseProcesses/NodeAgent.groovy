package codeLoading.baseProcesses

import groovyJCSP.MobileAgent
import groovyJCSP.PAR
import jcsp.lang.CSProcess

class NodeAgent implements MobileAgent{

  @Override
  Object connect(Object x) {
    return null
  }

  @Override
  Object disconnect() {
    return null
  }

  String nodeIP, hostIP

  @Override
  void run() {
    //    println "Node $nodeIP invoking its process network"
    // chnage to name of application Node Process
    CSProcess np = new MCpiNodeProcess(hostIP: hostIP, nodeIP: nodeIP)
    List <CSProcess> network
    network = [ np ]
    new PAR(network).run()
//    println "Node $nodeIP terminating"
  }
}
