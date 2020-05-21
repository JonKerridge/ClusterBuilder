package codeLoaders.testLoaders


import codeLoaders.loaders.LoaderConstants
import groovyJCSP.PAR
import jcsp.net2.NetChannel
import jcsp.net2.NetChannelInput
import jcsp.net2.NetChannelOutput
import jcsp.net2.Node
import jcsp.net2.mobile.CodeLoadingChannelFilter
import jcsp.net2.tcpip.TCPIPNodeAddress

/*
This code needs only TWO changes

The name of the node process and

The name of the host process
 */
class TestHostLoader implements LoaderConstants{
  static void main(String[] args) {
    int nodes = Integer.parseInt(args[0]) // number of nodes in cluster excluding host
    // create node and net input channel used by NodeLoaders
    def nodeAddress = new TCPIPNodeAddress(2000)
    Node.getInstance().init(nodeAddress)
    String hostIP = nodeAddress.getIpAddress()
    println "Host $hostIP running"
    NetChannelInput fromNodes = NetChannel.numberedNet2One(1 )
    // wait for all the nodes to send their IP addresses
    // create a List for the IPs
    List <String> nodeIPs = []
    // create list of individual net output channel to each node
    List < NetChannelOutput> toNodes = []
    for ( n in 0 ..< nodes) {
      String nodeIP = (fromNodes.read() as String)
      nodeIPs << nodeIP
      toNodes << NetChannel.one2net(
          new TCPIPNodeAddress(nodeIP,1000),
          1,
          new CodeLoadingChannelFilter.FilterTX())  // must be code loading because node process will be sent
    }
    // can now start timing as from now on the interactions are contiguous and
    // do not rely on Nodes being started from command line
    long initialTime = System.currentTimeMillis()
    // acknowledge receipt of NodeIPs to nNodes
    for ( n in 0 ..< nodes){
      toNodes[n].write(acknowledgeNodeIPRead)
    }
    // now send the built Node process to each Node - name modified by builder
    //@ NodeProcess
    for ( n in 0 ..< nodes){
      toNodes[n].write(new TestNodeProcess(
          hostIP: hostIP,
          nodeIP: nodeIPs[n],
          toHostLocation: fromNodes.getLocation()
        )
      )
    }
    // now read acknowledgements from Nodes
    for ( n in 0 ..< nodes){
      assert  fromNodes.read() == nodeProcessRead :
          "Failed to read Node Process read acknowledgement from ${nodeIPs[n]}"
    }
    // tell nodes to start their processes
    for ( n in 0 ..< nodes){
      toNodes[n].write(startNodeProcess)
    }
    long processStart = System.currentTimeMillis()
    // builder modifies the name of the host process
    //@ HostProcess
    new PAR([new TestHostProcess(
        hostIP: hostIP,
        nodeIPs: nodeIPs,
        nodes2host: fromNodes,
        host2nodes: toNodes
        )]).run()

    long processEnd = System.currentTimeMillis()
    println "Host Node: Load Phase= ${processStart - initialTime} " +
        "Processing Phase = ${processEnd - processStart} " +
        "Total time = ${processEnd - initialTime}"
  }
}
