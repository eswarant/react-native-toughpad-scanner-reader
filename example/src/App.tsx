import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import {
  initAPI,
  activateReader,
  hasReader
} from 'react-native-toughpad-scanner-reader';

type TInbuiltScannerInitResult = {
  isError: boolean | null;
  loading: boolean;
  hasReader: boolean | null;
};

export default function App() {
  const [barcodeText, setBarcodeText] = React.useState('');
  const [inbuiltScannerInitResult, setInbuiltScannerInitResult] =
    React.useState<TInbuiltScannerInitResult>({
      isError: null,
      loading: true,
      hasReader: null,
    });

  const initBarcodeReader = async () => {
    try {
      await initAPI();
      const reader = hasReader();
      setInbuiltScannerInitResult({
        isError: false,
        loading: false,
        hasReader: reader,
      });
    } catch (ex) {
      console.error(ex);
      setInbuiltScannerInitResult({
        isError: true,
        loading: false,
        hasReader: null,
      });
    }
  };

  React.useEffect(() => {
    initBarcodeReader();
  }, []);

  const onBarcodeReadCallback = (text: string) => {
    setBarcodeText(text);
  };

  const onPressScanButton = () => {
    activateReader(onBarcodeReadCallback);
  };

  return (
    <View style={styles.container}>
      {inbuiltScannerInitResult.isError && <Text>{'Error'}</Text>}
      {inbuiltScannerInitResult.loading && <Text>{'Loading'}</Text>}
      <Text>Barcode: {barcodeText}</Text>
      <View style={styles.button}>
        <TouchableOpacity onPress={onPressScanButton}>
          <Text style={styles.buttonText}>Scan</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
  button: {
    padding: 10,
    backgroundColor: 'blue',
    marginVertical: 10,
  },
  buttonText: {
    color: 'white',
  },
});
