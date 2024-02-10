import * as React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import {
  initializeBarcodeReader,
  scanBarcode,
} from 'react-native-toughpad-scanner-reader';

export default function App() {
  const [barcodeText, setBarcodeText] = React.useState('');

  React.useEffect(() => {
    initializeBarcodeReader();
  }, []);

  const onBarcodeReadCallback = (text: string) => {
    setBarcodeText(text);
  };

  const onPressScanButton = () => {
    scanBarcode(onBarcodeReadCallback);
  };

  return (
    <View style={styles.container}>
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
