import 'package:beamer/beamer.dart';
import 'package:flutter/material.dart';
import 'package:iopc_client/entity/server_info.dart';
import 'package:mdns_plugin/mdns_plugin.dart';

class ServersListScreen extends StatefulWidget {
  ServersListScreen({Key? key, this.title}) : super(key: key);

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String? title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<ServersListScreen>
    implements MDNSPluginDelegate {
  MDNSPlugin? _mdns;

  int _counter = 0;
  List<ServerInfo> servers = List.empty(growable: true);

  void _onManualAdd() async {
    setState(() {
      // This call to setState tells the Flutter framework that something has
      // changed in this State, which causes it to rerun the build method below
      // so that the display can reflect the updated values. If we changed
      // _counter without calling setState(), then the build method would not be
      // called again, and so nothing would appear to happen.
      _counter++;
    });
  }

  void _onServerTap(int index) {
    context.beamToNamed("/servers/add",
        data: {'preFillServerInfo': servers[index]}, beamBackOnPop: true);
  }

  @override
  void initState() {
    super.initState();
    this._mdns = new MDNSPlugin(this);
    this._mdns?.startDiscovery("_iopc-server._tcp", enableUpdating: true);
  }

  @override
  void dispose() {
    super.dispose();
    this._mdns?.stopDiscovery();
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title ?? "No title"),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: ListView.builder(
            itemBuilder: (context, index) {
              var server = servers[index];
              return ListTile(
                title: Text(server.name),
                subtitle: Text(server.isAvailableForConnection()
                    ? "${server.hostname}:${server.port} (v${server.version}, Java ${server.javaVersion})"
                    : "Loading information..."),
                onTap: () => {_onServerTap(index)},
              );
            },
            itemCount: servers.length),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _onManualAdd,
        tooltip: 'Increment',
        child: Icon(Icons.add),
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }

  @override
  void onDiscoveryStarted() {
    // TODO: implement onDiscoveryStarted
  }

  @override
  void onDiscoveryStopped() {}

  @override
  bool onServiceFound(MDNSService service) {
    print("found: $service");
    setState(() {
      servers.add(ServerInfo(service));
    });
    return true;
  }

  @override
  void onServiceRemoved(MDNSService service) {
    print("removed: $service");
    setState(() {
      servers.removeWhere((s) => s.equalsTo(service));
    });
  }

  @override
  void onServiceResolved(MDNSService service) {
    print("resolved: $service");
    setState(() {
      var server = servers.firstWhere((s) => s.equalsTo(service));
      server.applyChanges(service);
    });
  }

  @override
  void onServiceUpdated(MDNSService service) {
    print("updated: $service");
    setState(() {
      var server = servers.firstWhere((s) => s.equalsTo(service));
      server.applyChanges(service);
    });
  }
}

class ServersListLocation extends BeamLocation {
  @override
  List<BeamPage> pagesBuilder(BuildContext context) => [
        BeamPage(key: ValueKey("ServersListScreen"), child: ServersListScreen())
      ];

  @override
  List<String> get pathBlueprints => ["/servers"];
}
