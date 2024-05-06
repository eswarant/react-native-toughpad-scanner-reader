import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-toughpad-scanner-reader' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ToughpadScannerReader = NativeModules.ToughpadScannerReader
  ? NativeModules.ToughpadScannerReader
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return ToughpadScannerReader.multiply(a, b);
}

export function initAPI() {
  return ToughpadScannerReader.initAPI();
}

export function activateReader(onReadCallback: (barcodeText: string) => void) {
  return ToughpadScannerReader.activateReader(onReadCallback);
}

export function hasReader(): boolean {
  return ToughpadScannerReader.hasReader();
}

export function deactivateReader(): void {
  return ToughpadScannerReader.deactivateReader();
}
