@objc(ToughpadScannerReader)
class ToughpadScannerReader: NSObject {

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

  @objc(initAPI:withB:withResolver:withRejecter:)
  func initAPI(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }

  @objc(hasReader:withB:withResolver:withRejecter:)
  func hasReader(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }

  @objc(activateReader:withB:withResolver:withRejecter:)
  func activateReader(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }

  @objc(deactivateReader:withB:withResolver:withRejecter:)
  func deactivateReader(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }
}
