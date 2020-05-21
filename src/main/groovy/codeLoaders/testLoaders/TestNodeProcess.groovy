package codeLoaders.testLoaders

import codeLoaders.loaders.LoaderConstants
import codeLoaders.loaders.NodeConnection
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.NetLocation
import jcsp.net2.tcpip.TCPIPNodeAddress

/*
This code needs the following changes at the bracketed points indicated:

//@ InputVCNs   ... //@ End
//@ OutputVCNs  ... //@ End
//@ NodeProcess ... //@ End

 */

class TestNodeProcess implements CSProcess, Serializable, NodeConnection, LoaderConstants{
  String nodeIP, hostIP
  NetLocation toHostLocation
  NetChannelInput fromHost

  def connectFromHost (NetChannelInput fromHost){
    this.fromHost = fromHost
  }

  @Override
  void run() {
    // this basic text will be modified by the builder
    long initialTime = System.currentTimeMillis()
    // create basic connections for node
    NetChannelOutput node2host = NetChannel.one2net(toHostLocation)
    node2host.write(nodeProcessInitiation)
    // read in application net input channel VCNs [ vcn, ... ]
    List inputVCNs = fromHost.read() as List
    //@ InputVCNs

    NetChannelInput netIn1 = NetChannel.numberedNet2One(inputVCNs[0])

    //@ End

    // acknowledge creation of net input channels
    node2host.write(nodeApplicationInChannelsCreated)

    // read in application net output channel locations [ [ip, vcn], ... ]
    List outputVCNs = fromHost.read()
    //@ OutputVCNs

    NetChannelOutput netOut1 = NetChannel.one2net(
        new TCPIPNodeAddress(outputVCNs[0][0], 2000),
        outputVCNs[0][1]
    )

    //@ End

    // acknowledge creation of net output channels
    node2host.write(nodeApplicationOutChannelsCreated)
    long processStart = System.currentTimeMillis()
    // now start the process - inserted by builder
    //@ NodeProcess

    def copier = new Copy(
        netIn1: netIn1,
        netOut1: netOut1
    )

    new PAR([copier]).run()

    //@ End

    long processEnd = System.currentTimeMillis()
    node2host.write([nodeIP, (processStart - initialTime), (processEnd - processStart)])
  }
}
