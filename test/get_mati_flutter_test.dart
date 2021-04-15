import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:get_mati_flutter/get_mati_flutter.dart';

void main() {
  const MethodChannel channel = MethodChannel('get_mati_flutter');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await GetMatiFlutter.platformVersion, '42');
  });
}
