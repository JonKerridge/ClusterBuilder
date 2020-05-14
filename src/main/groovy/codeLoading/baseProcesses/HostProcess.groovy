package codeLoading.baseProcesses

import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput

class HostProcess implements CSProcess{
  String hostIP
  List <String> nodeIPs
  NetChannelInput fromNodeAgents
  List <NetChannelOutput> toNodeAgents

  @Override
  void run() {
    int nodes = nodeIPs.size()
    // tell nodes to create input channels
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
    // create net input channels - defined by builder
    //@ HostNetInputChannels
//    println "Created host net input channels"
    for ( n in 0 ..< nodes){
      assert fromNodeAgents.read() == nodeIPs[n]:"Error creating net inputs for ${nodeIPs[n]}"
    }
    // tell nodes to create output channels

    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
    // create net output channels - defined by builder
    //@ HostNetOutputChannels
//    println "Created host net output channels"
    for ( n in 0 ..< nodes){
      assert fromNodeAgents.read() == nodeIPs[n]:"Error creating net outputs for ${nodeIPs[n]}"
    }
    // tell nodes to start processing
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
//    println "Host about to run its process network"
    // host network specification - defined by builder
    //@ HostProcess
    CSProcess hp = new TestHostProcess()
    List <CSProcess> network
    network = [hp]
//    println "Host about to run application network"
    new PAR(network).run()
//    println "Host process network terminating"
  }
}
