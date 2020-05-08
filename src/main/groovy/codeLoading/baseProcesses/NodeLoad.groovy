package codeLoading.baseProcesses

import groovyJCSP.MobileAgent
import jcsp.lang.ProcessManager
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.Node
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class NodeLoad {
  static void main(String[] args) {
    String message
    // create this node
    def nodeAddress = new TCPIPNodeAddress(1000)
    Node.getInstance().init(nodeAddress)
    String nodeIP = nodeAddress.getIpAddress()
    println "Node $nodeIP running"
    // create net input channel from host node
    NetChannelInput fromHost = NetChannel.numberedNet2One(1, new CodeLoadingChannelFilter.FilterRX() )
    //connect to host
    String hostIP = args[0]
    def hostAddress = new TCPIPNodeAddress(hostIP, 2000)
    // create host request output channel
    def toHost = NetChannel.any2net(hostAddress, 1)
    // send host the IP of this Node and get response
    toHost.write(nodeIP)
    message = fromHost.read()
    assert (message == hostIP): "Node Load - $nodeIP: expected $hostIP received $message : initial interaction"
    println "Node $nodeIP awaiting agent load"
    // load Agent process from Host
    def agent = fromHost.read() as MobileAgent
    def processManager = new ProcessManager(agent)
    println "Node $nodeIP starting"
    processManager.start()
    processManager.join()
    println "Node $nodeIP has terminated"
  }
}
