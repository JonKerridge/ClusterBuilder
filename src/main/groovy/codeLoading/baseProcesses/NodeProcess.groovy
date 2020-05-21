package codeLoading.baseProcesses

import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class NodeProcess implements CSProcess {

  String nodeIP, hostIP

//  // now declare the properties by which the application net channels can be created
//  // these can be inserted by the builder
//  List <Integer> inChannelVCNs = []
//  List outChannelLocations = []  // each entry is [IPstring, vcn]

  @Override
  void run() {
    String message
    long initialTime =System.currentTimeMillis()
    NetChannelInput fromHost = NetChannel.numberedNet2One(2, new CodeLoadingChannelFilter.FilterRX() )
    def hostAddress = new TCPIPNodeAddress(hostIP, 2000)
    // create host request output channel
    def toHost = NetChannel.any2net(hostAddress, 2)
    // send host the IP of this Node and get response
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : confirm node initialisation"
    // read input channel index numbers from host
    List < List <Integer> > inputVCNs= fromHost.read()
    // create net input channels - defined by builder - uses inputVCNs
    //@ NodeNetInputChannels
    toHost.write(nodeIP)
    List outChannels = fromHost.read()
    // create net output channels - defined by builder -uses outChannels
    // each entry in the List is a list comprising [nodeIP, vcn]
    //@ NodeNetOutputChannels
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : confirm output net channel creation"
    long processStart = System.currentTimeMillis()
    // node network specification - defined by builder
    //@ NodeProcess
    CSProcess tp = new TestNodeProcess()
    List <CSProcess> network
    network = [tp]
//    println "Node $nodeIP about to run application network"
    new PAR(network).run()
    long processEnd = System.currentTimeMillis()
    println "Node $nodeIP: Loading: ${processStart - initialTime} " +
        "Processing: ${processEnd - processStart}"
  }
}
