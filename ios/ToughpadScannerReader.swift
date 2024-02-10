@objc(ToughpadScannerReader)
class ToughpadScannerReader: NSObject {

  @objc(multiply:withB:withResolver:withRejecter:)
  func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) -> Void {
    resolve(a*b)
  }

  @objc(initializeBarcodeReader:withB:withResolver:withRejecter:)
  func initializeBarcodeReader(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }

  @objc(scanBarcode:withB:withResolver:withRejecter:)
  func scanBarcode(reject:RCTPromiseRejectBlock) -> Void {
    reject("Not supported for IOS")
  }
}
