package codeLoading.baseProcesses

import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.cluster.connectors.OneNodeRequestedList
import groovyParallelPatterns.connectors.reducers.AnyFanOne
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import initialVersion.cluster.data.MCpiData
import initialVersion.cluster.data.MCpiResultsSerialised
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.tcpip.TCPIPNodeAddress

class MCpiHostProcess implements CSProcess{
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
    NetChannelInput inputToEmit0 = NetChannel.numberedNet2One(100)
    NetChannelInput inputToCollect0 = NetChannel.numberedNet2One(101)
    ChannelInputList onrlInputList0 = []
    onrlInputList0.append(inputToEmit0)
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
//    TCPIPNodeAddress addressNode0 = new TCPIPNodeAddress(nodeIPs[0], 1000)
    NetChannelOutput outputToNode0 = NetChannel.one2net(new TCPIPNodeAddress(nodeIPs[0], 1000),100)
    ChannelOutputList onrlOutputList0 = []
    onrlOutputList0.append(outputToNode0)
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
    def emitDetails = new DataDetails(
        dName: MCpiData.getName(),
        dInitMethod: MCpiData.init,
        dInitData: [1024],
        dCreateMethod: MCpiData.create,
        dCreateData: [100000]
    )

    def resultDetails = new ResultDetails(
        rName: MCpiResultsSerialised.getName(),
        rInitMethod: MCpiResultsSerialised.init,
        rCollectMethod: MCpiResultsSerialised.collector,
        rFinaliseMethod: MCpiResultsSerialised.finalise
    )
//    int cores = 4
    int clusters = 1
    def chan0 = Channel.one2one()
    def chan1 = Channel.one2one()
    def emit = new Emit (
        eDetails: emitDetails,
        output: chan0.out()
    )
    def onrl = new OneNodeRequestedList(
        input: chan0.in(),
        request: onrlInputList0,
        response: onrlOutputList0
    )

    def afoC = new AnyFanOne(
        inputAny: inputToCollect0,
        output: chan1.out(),
        sources: clusters
    )
    def collector = new Collect(
        input: chan1.in(),
        rDetails: resultDetails
    )

    List <CSProcess> network
    network = [emit, onrl, afoC, collector]
//    println "Host about to run application network"
    new PAR(network).run()
//    println "Host process network terminating"
  }
}
