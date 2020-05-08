package codeLoading.baseProcesses

import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.Node
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

class HostLoad {
  static void main(String[] args) {
    int nodes = Integer.parseInt(args[0])
    def nodeAddress = new TCPIPNodeAddress(2000)
    Node.getInstance().init(nodeAddress)
    String hostIP = nodeAddress.getIpAddress()
    println "Host $hostIP running"
    NetChannelInput fromNodes = NetChannel.numberedNet2One(1 )
    List <TCPIPNodeAddress> nodeIPAddresses = []
    List <String> nodeIPs = []
    List < NetChannelOutput> nodeInitialChannels = []
    for ( n in 0 ..< nodes) {
      String nodeIP = (fromNodes.read() as String)
      nodeIPs << nodeIP
      nodeIPAddresses << new TCPIPNodeAddress(nodeIP,1000)
      nodeInitialChannels << NetChannel.one2net(nodeIPAddresses[n], 1, new CodeLoadingChannelFilter.FilterTX())
    }
    for ( n in 0 ..< nodes){
      nodeInitialChannels[n].write(hostIP)
    }
    println "Host about to send Agents to Nodes"
    NetChannelInput fromNodeAgents = NetChannel.numberedNet2One(2 )
    List <NetChannelOutput> toNodeAgents = []
    for ( n in 0 ..< nodes){
      toNodeAgents << NetChannel.one2net(nodeIPAddresses[n], 2, new CodeLoadingChannelFilter.FilterTX())
    }
    for ( n in 0 ..< nodes){
      nodeInitialChannels[n].write(new NodeAgent(nodeIP: nodeIPs[n],
                                                 hostIP: hostIP)
                                  // initialise net channel parameters
                                  )
    }
    println "Host has sent Agents to Nodes"
    for ( n in 0 ..< nodes){
      println " From node $n have read ${fromNodeAgents.read()}"
    }
    for ( n in 0 ..< nodes) {
      toNodeAgents[n].write(hostIP)
    }
  }
}
