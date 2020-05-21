package codeLoaders.basicProcesses

import codeLoaders.loaders.LoaderConstants
import codeLoaders.loaders.NodeConnection
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelLocation
import jcsp.net2.NetChannelOutput
import jcsp.net2.NetLocation
import jcsp.net2.tcpip.TCPIPNodeAddress

/*
This code needs the following changes at the bracketed points indicated:

//@ InputVCNs   ... //@ End
//@ OutputVCNs  ... //@ End
//@ NodeProcess ... //@ End

 */

class BasicNodeProcess implements CSProcess, Serializable, NodeConnection, LoaderConstants{
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
    NetChannelOutput node2host = NetChannel.one2net(toHostLocation as NetChannelLocation)
    node2host.write(nodeProcessInitiation)
    // read in application net input channel VCNs [ vcn, ... ]
    List inputVCNs = fromHost.read() as List
    //@ InputVCNs

    //@ End

    // acknowledge creation of net input channels
    node2host.write(nodeApplicationInChannelsCreated)

    // read in application net output channel locations [ [ip, vcn], ... ]
    List outputVCNs = fromHost.read()
    //@ OutputVCNs

    //@ End

    // acknowledge creation of net output channels
    node2host.write(nodeApplicationOutChannelsCreated)
    long processStart = System.currentTimeMillis()
    // now start the process - inserted by builder
    //@ NodeProcess

    //@ End

    long processEnd = System.currentTimeMillis()
    node2host.write([nodeIP, (processStart - initialTime), (processEnd - processStart)])
  }
}
