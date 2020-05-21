package codeLoading.baseProcesses

import groovyJCSP.PAR
import groovyParallelPatterns.cluster.connectors.NodeRequestingFanAny
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.functionals.groups.AnyGroupAny
import initialVersion.cluster.data.SerializedMCpiData
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class MCpiNodeProcess implements CSProcess {

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
    // send host the IP of this Node and get response confirming receipt
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : confirm node process load"
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : start input net channel creation"
    // create net input channels - defined by builder
    //@ NodeNetInputChannels
    NetChannelInput inputFromEmit0 = NetChannel.numberedNet2One(100)
//    println "Created node $nodeIP net input channels"
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : confirm input net channel creation"
    // create net output channels - defined by builder
    //@ NodeNetOutputChannels
    NetChannelOutput outputToEmit0 = NetChannel.one2net(new TCPIPNodeAddress(hostIP, 2000),100)
    NetChannelOutput outputToCollect0 = NetChannel.one2net(new TCPIPNodeAddress(hostIP, 2000),101)
//    println "Created node $nodeIP net output channels"
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : confirm output net channel creation"
//    println "Node $nodeIP about to run its process network"
    // node network specification - defined by builder
    long processStart = System.currentTimeMillis()
    //@ NodeProcess
    int cores = 2
    def chan1 = Channel.any2any()
    def chan2 = Channel.any2any()
    def nrfa = new NodeRequestingFanAny(
        request: outputToEmit0,
        response: inputFromEmit0,
        outputAny: chan1.out(),
        destinations: cores
    )
    def group = new AnyGroupAny(
        inputAny: chan1.in(),
        outputAny: chan2.out(),
        workers: cores,
        function: SerializedMCpiData.withinOp
    )
    def afo = new AnyFanOne(
        inputAny: chan2.in(),
        output: outputToCollect0,
        sources: cores
    )

    List <CSProcess> network
    network = [nrfa, group, afo]
//    println "Node $nodeIP about to run application network"
    new PAR(network).run()
    long processEnd = System.currentTimeMillis()
    println "Node $nodeIP: Loading: ${processStart - initialTime} " +
        "Processing: ${processEnd - processStart}"
  }
}
