import 'package:beamer/beamer.dart';
import 'package:flutter/material.dart';
import 'package:iopc_client/entity/server_info.dart';
import 'package:iopc_client/screens/servers_list.dart';

class AddServerScreen extends StatefulWidget {
  final ServerInfo? preFillServerInfo;

  const AddServerScreen({Key? key, this.preFillServerInfo}) : super(key: key);

  @override
  _AddServerScreenState createState() => _AddServerScreenState();
}

class _AddServerScreenState extends State<AddServerScreen> {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text("Добавление сервера"),
      ),
      body: Padding(
          padding: EdgeInsets.all(16.0),
          // Center is a layout widget. It takes a single child and positions it
          // in the middle of the parent.
          child: Form(
              key: _formKey,
              child: Column(children: <Widget>[
                TextFormField(
                  initialValue: widget.preFillServerInfo?.hostname,
                  decoration: const InputDecoration(
                    icon: Icon(Icons.power),
                    hintText: 'Например, 192.168.0.2:8080',
                    labelText: 'Адрес сервера',
                  ),
                  // The validator receives the text that the user has entered.
                  validator: (value) {
                    if (value!.isEmpty) {
                      return 'Please enter some text';
                    }
                    return null;
                  },
                ),
                ElevatedButton(
                  onPressed: () {
                    // Validate returns true if the form is valid, otherwise false.
                    if (_formKey.currentState!.validate()) {
                      // If the form is valid, display a snackbar. In the real world,
                      // you'd often call a server or save the information in a database.

                      ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Processing Data')));
                    }
                  },
                  child: Text('Submit'),
                )
              ]))),
    );
  }
}

class AddServerLocation extends BeamLocation {
  @override
  List<BeamPage> pagesBuilder(BuildContext context) => [
        ...ServersListLocation().pagesBuilder(context),
        BeamPage(
            key: ValueKey("AddServerScreen"),
            child: AddServerScreen(
              preFillServerInfo: data["preFillServerInfo"],
            ))
      ];

  @override
  List<String> get pathBlueprints => ["/servers/add"];
}
