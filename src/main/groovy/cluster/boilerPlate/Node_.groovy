package cluster.boilerPlate


import jcsp.net2.Node
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.tcpip.TCPIPNodeAddress
import jcsp.userIO.Ask

/**
 * The script used to create a node process network containing an emit and collector processes.
 * Thus Node has to be modified for each solution.
 * There will be a version of this script for each node
 * There will be no code loading over network in this solution
 * due to problems with Java Groovy and the change from Java8 to Java 9+
 */

class Node_ {
  static void main(String[] args) {
// the first part assumes the Nodes are running on a loop-back 127.0.0.? network
// it assumes the host is running on 127.0.0.1
//String nodeAddress4 = Ask.Int( "what is the fourth part of the node's IP-address?  ", 2, 254)
//String nodeIP = "127.0.0." + nodeAddress4
//def nodeAddress = new TCPIPNodeAddress(nodeIP, 1000)
//String hostIP = "127.0.0.1"
//Node.getInstance().init(nodeAddress)

// this section assumes the system is running on a real network
// there are two ways of getting hostIP either by interaction of by argument passing
// choose one only!
//String hostIP = args[0]
    String hostIP = Ask.string("Host IP address? ")
    def nodeAddress = new TCPIPNodeAddress(2000)  // create nodeAddress for most global IP address
    Node.getInstance().init(nodeAddress)
    String nodeIP = nodeAddress.getIpAddress()

// the rest of the script is common

    println "Run Node is located at $nodeIP and host is $hostIP"
    NetChannelInput hostResponse = NetChannel.numberedNet2One(1, )
    def hostAddress = new TCPIPNodeAddress(hostIP, 1000)
// create host request channel
    def hostRequest = NetChannel.any2net(hostAddress, 1)
// send host the IP of this Node
    hostRequest.write(nodeIP)
    println "Run Node written $nodeIP to host"
    String message = hostResponse.read()
    assert (message == hostIP): "Run Node - $nodeIP: expected $hostIP received $message : initial interaction"

// now create all the net input channels
// this bit filled in by Builder

// inform host that input channels have been created
    hostRequest.write(nodeIP)
    message = hostResponse.read()
    assert (message == hostIP): "Run Node - $nodeIP: expected $hostIP received $message : input channel creation"

// now create all the net output channels
// this bit filled in by Builder

// inform host that output channels have been created
    hostRequest.write(nodeIP)
    message = hostResponse.read()
    assert (message == hostIP): "Run Node - $nodeIP: expected $hostIP received $message : output channel creation"

// now define the processes for the node including the additional ones required

// inform host that process network has been defined
    hostRequest.write(nodeIP)
    message = hostResponse.read()
    assert (message == hostIP): "Run Node - $nodeIP: expected $hostIP received $message: process definition"

// now run process network
// this bit filled in by Builder

    println "Run Node - $nodeIP: has terminated"

  }
}