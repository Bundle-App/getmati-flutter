import 'dart:async';

import 'package:flutter/services.dart';

class GetMatiFlutter {
  static const MethodChannel _channel = const MethodChannel('get_mati_flutter');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  // ignore: slash_for_doc_comments
  /** {String clientId,
      String flowId,
      buttonTitle,
      List<Map<String, dynamic>> metadata}
   **/
  static Future<dynamic> startVerification(Map body) async {
    var result;
    try {
      result = await _channel.invokeMethod('startVerification', {"body": body});
    } on PlatformException catch (e) {
      result = null;
    }
    return result;
  }
}
