import 'dart:async';

import 'package:flutter/services.dart';

class Torch {
  static const MethodChannel _channel = const MethodChannel('snowcorp/torch');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future turnOn() => _channel.invokeMethod('turnOn');

  static Future turnOff() => _channel.invokeMethod('turnOff');

  static Future dispose() => _channel.invokeMethod('dispose');

  static Future<bool> get hasTorch async =>
      await _channel.invokeMethod('hasTorch');
}
