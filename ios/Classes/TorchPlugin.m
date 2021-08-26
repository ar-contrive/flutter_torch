#import "TorchPlugin.h"
#if __has_include(<torch/torch-Swift.h>)
#import <torch/torch-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "torch-Swift.h"
#endif

@implementation TorchPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTorchPlugin registerWithRegistrar:registrar];
}
@end
