package codeLoading.baseProcesses

import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class HostProcess implements CSProcess{
  String hostIP
  List <String> nodeIPs
  NetChannelInput fromNodeAgents
  List <NetChannelOutput> toNodeAgents

//  // now declare the properties by which the application net channels can be created
//  // these can be inserted by the builder
//  List <Integer> inChannelVCNs = []
//  List outChannelLocations = []  // each entry is [IPstring, vcn]

  @Override
  void run() {
    int nodes = nodeIPs.size()
//    List <TCPIPNodeAddress> nodeIPAddresses = []
//    for ( n in 0 ..< nodes) {
//      nodeIPAddresses << new TCPIPNodeAddress(nodeIPs[n], 1000)
//    }
//    NetChannelInput fromNodeAgents = NetChannel.numberedNet2One(2 )
//    List <NetChannelOutput> toNodeAgents = []
//    for ( n in 0 ..<nodes){
//      toNodeAgents << NetChannel.one2net(nodeIPAddresses[n], 2, new CodeLoadingChannelFilter.FilterTX())
//    }
    // tell nodes to create input channels
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
    // create net input channels - defined by builder
    println "Created host net input channels"
    for ( n in 0 ..< nodes){
//      println " From node $n have read ${fromNodeAgents.read()}"
      assert fromNodeAgents.read() == nodeIPs[n]:"Error creating net inputs for ${nodeIPs[n]}"
    }
    // tell nodes to create output channels
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
    // create net output channels - defined by builder
    println "Created host net output channels"
    for ( n in 0 ..< nodes){
//      println " From node $n have read ${fromNodeAgents.read()}"
      assert fromNodeAgents.read() == nodeIPs[n]:"Error creating net outputs for ${nodeIPs[n]}"
    }
    // tell nodes to start processing
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
    println "Host about to run its process network"
    // host network specification - defined by builder
    CSProcess hp = new TestHostProcess()
    List <CSProcess> network
    network = [hp]
    println "Host about to run application network"
    new PAR(network).run()
    println "Host process network terminating"
  }
}
