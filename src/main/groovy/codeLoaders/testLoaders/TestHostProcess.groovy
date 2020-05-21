package codeLoaders.testLoaders

import codeLoaders.loaders.LoaderConstants
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.tcpip.TCPIPNodeAddress

/*
This code needs the following changes at the bracketed points indicated:

//@ InputVCNs     ...  //@ End
//@ HostInputs    ...  //@ End
//@ OutputVCNs    ...  //@ End
//@ HostOutputs   ...  //@ End
//@ HostProcess   ...  //@ End

 */

class TestHostProcess implements CSProcess, LoaderConstants{
  String hostIP
  List <String> nodeIPs
  NetChannelInput nodes2host
  List <NetChannelOutput> host2nodes

  @Override
  void run() {
    // this basic text will be modified by the builder
    int nodes = nodeIPs.size()
    // create basic process connections for host
    for ( n in 0 ..< nodes) {
      // wait for all nodes to start
      assert nodes2host.read() == nodeProcessInitiation :
        "Node ${nodeIPs[n]} failed to initialise node process"
      // create host2nodes channels - already have node IPs
     }
    long initialTime = System.currentTimeMillis()
    // send application channel data to nodes - inserted by Builder - also those at host
    //@ InputVCNs
    List inputVCNs = [[100] ]   // each node gets a list of input VCNs

    //@ End

    for ( n in 0 ..< nodes) host2nodes[n].write(inputVCNs[n])

    //@ HostInputs

    NetChannelInput netIn1 = NetChannel.numberedNet2One(100)

    //@ End

    // now read acknowledgments
    for ( n in 0 ..< nodes){
      assert nodes2host.read() == nodeApplicationInChannelsCreated :
          "Node ${nodeIPs[n]} failed to create node to host link channels"
    }
    // each node gets a list [IP, vcn] to which it is connected
    //@ OutputVCNs
    List outputVCNs = [ [ [hostIP, 100] ] ]

    //@ End

    for ( n in 0 ..< nodes) host2nodes[n].write(outputVCNs[n])

    //@ HostOutputs

    NetChannelOutput netOut1 = NetChannel.one2net(
        new TCPIPNodeAddress(nodeIPs[0], 1000),
        100)

    //@ End

    // now read acknowledgments
    for ( n in 0 ..< nodes){
      assert nodes2host.read() == nodeApplicationOutChannelsCreated :
          "Node ${nodeIPs[n]} failed to create node to host link channels"
    }
    // all the net application channels have been created
    long processStart = System.currentTimeMillis()
    // now start the process - inserted by builder
    //@ Hostprocess

    def send = new Sender(netOut1: netOut1)
    def receive = new Receiver(netIn1: netIn1)


    new PAR([send, receive]).run()

    //@ End

    long processEnd = System.currentTimeMillis()

    List times = [ ["Host", (processStart - initialTime), (processEnd - processStart)] ]
//    println "Host Application: Loading: ${processStart - initialTime} " +
//        "Processing: ${processEnd - processStart}"
    for ( n in 0 ..< nodes){
      times << (List)(nodes2host.read() )
    }
    times.each {println "$it"}
  }
}
