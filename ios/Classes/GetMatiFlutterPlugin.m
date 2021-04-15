#import "GetMatiFlutterPlugin.h"
#if __has_include(<get_mati_flutter/get_mati_flutter-Swift.h>)
#import <get_mati_flutter/get_mati_flutter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "get_mati_flutter-Swift.h"
#endif

@implementation GetMatiFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftGetMatiFlutterPlugin registerWithRegistrar:registrar];
}
@end
