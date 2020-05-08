package codeLoading.baseProcesses

import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class NodeProcess implements CSProcess {

  String nodeIP, hostIP

  // now declare the properties by which the application net channels can be created
  // these can be inserted by the builder
  List <Integer> inChannelVCNs = []
  List outChannelLocations = []  // each entry is [IPstring, vcn]

  @Override
  void run() {
    NetChannelInput fromHost = NetChannel.numberedNet2One(2, new CodeLoadingChannelFilter.FilterRX() )
    def hostAddress = new TCPIPNodeAddress(hostIP, 2000)
    // create host request output channel
    def toHost = NetChannel.any2net(hostAddress, 2)
    // send host the IP of this Node and get response
    toHost.write(nodeIP)
    String message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : initial interaction"
    println "DummyNode Process running"
  }
}
