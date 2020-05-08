package codeLoading.baseProcesses

import groovyJCSP.MobileAgent
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

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
    NetChannelInput fromHost = NetChannel.numberedNet2One(2, new CodeLoadingChannelFilter.FilterRX() )
    def hostAddress = new TCPIPNodeAddress(hostIP, 2000)
    // create host request output channel
    def toHost = NetChannel.any2net(hostAddress, 2)
    // send host the IP of this Node and get response
    toHost.write(nodeIP)
    String message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : initial interaction"
    println "Node $nodeIP invoking its code"
    CSProcess dnp = new DummyNodeProcess()
    List <CSProcess> network = []
    network << dnp
    new PAR(network).run()
    println "Node $nodeIP terminating"
  }
}
