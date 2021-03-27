import 'package:mdns_plugin/mdns_plugin.dart';

class ServerInfo {
  String name;
  String serviceType;

  String? hostname;
  List<String> addresses = List.empty(growable: true);
  int? port;
  String? version;
  String? javaVersion;

  ServerInfo(MDNSService service)
      : name = service.name,
        serviceType = service.serviceType;

  bool equalsTo(MDNSService service) {
    return service.name == name && service.serviceType == serviceType;
  }

  bool isAvailableForConnection() {
    return hostname != null && port != null;
  }

  void applyChanges(MDNSService service) {
    this.hostname = service.hostName;
    this.addresses = service.addresses;
    this.port = service.port;
    this.version = new String.fromCharCodes(service.txt["version"]);
    this.javaVersion = new String.fromCharCodes(service.txt["java_version"]);
  }
}
