import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:get_mati_flutter/get_mati_flutter.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    var result;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      result = await GetMatiFlutter.startVerification({
        "clientId": "CLIENT_ID",
        "flowId": "FLOW_ID",
        "buttonTitle": "Continue",
        "metadata": { // fields inside can be whatever we want.
          "userId": "userId",
        },
      });
    } on PlatformException {
      result = 'Error occured';
    }
    print('Result is $result');
    // Example: {verificationId: 60788c1793c323001b7d468c, status: success}

    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: FlatButton(
          child: Text('Click Demo'),
          onPressed: () => initPlatformState(),
        )),
      ),
    );
  }
}
