import Flutter
import UIKit
import MatiGlobalIDSDK

public class SwiftGetMatiFlutterPlugin: NSObject, FlutterPlugin {
    var bundleGetMatiFlutterResult: FlutterResult!
    let button = MFKYCButton()
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "get_mati_flutter", binaryMessenger: registrar.messenger())
    let instance = SwiftGetMatiFlutterPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if call.method == "startVerification" {
        guard let dictionary = (call.arguments as! [String: Any])["body"] as? [String: Any] else {
            result(FlutterError(code: "INVALID DATA", message: "Missing some required data", details: nil))
            return
        }
        guard let clientId = dictionary["clientId"] as? String,
              let flowId = dictionary["flowId"] as? String else {
            result(FlutterError(code: "INVALID DATA", message: "Missing some required data", details: nil))
            return
        }
        DispatchQueue.main.async {
            let metadata = dictionary["metadata"] as? [String: Any] ?? [:]
            self.bundleGetMatiFlutterResult = result
            MFKYC.register(clientId: clientId, metadata: metadata)
            MFKYC.instance.delegate = self
            self.button.flowId = flowId
            self.button.sendActions(for: .touchUpInside)
        }
    }
  }
}

extension SwiftGetMatiFlutterPlugin: MFKYCDelegate {
    public func mfKYCLoginSuccess(identityId: String) {
        bundleGetMatiFlutterResult(["identityId": identityId])
    }
    
    public func mfKYCLoginCancelled() {
        bundleGetMatiFlutterResult(nil)
    }
}
