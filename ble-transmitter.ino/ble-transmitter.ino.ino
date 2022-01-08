#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <BLE2902.h>

#define DEVICENAME "Zoo Sample Device"

#define SEND "9c81420d-8a1e-49f8-a42f-d4679c7330be"
#define SEND_STRING "1394b0cc-e9b7-4cc1-bc50-d6c9a9505f21"

#define RECIVE "f159cdfb-df60-4a4a-b1fa-004bcc379bb6"
#define RECIVE_STRING "cd468881-1cda-47a1-9373-dc812d15d727"
#define UI_ID "dfa653"

bool deviceConnected = false;

BLECharacteristic *sSendString;

String strToString(std::string str) {
  return str.c_str();
}

int strToInt(std::string str) {
  const char* encoded = str.c_str();
  return 256 * int(encoded[1]) + int(encoded[0]);
}

double intToDouble(int value, double max) {
  return (1.0 * value) / max;
}

bool intToBool(int value) {
  if (value == 0) {
    return false;
  }
  return true;
}

//class ConnectionServerCallbacks: public BLEServerCallbacks {
//    void onConnect(BLEServer* pServer) {
//      deviceConnected = true;
//      Serial.println("Connected");
//    };
//
//    void onDisconnect(BLEServer* pServer) {
//      deviceConnected = false;
//      Serial.println("Disconnected");
//    }
//};

class WriteString: public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic *pCharacteristic) {
      String str = strToString(pCharacteristic->getValue());
      sSendString->setValue("default");
      Serial.print("Recived String:");
      Serial.println(str);
      if( str == "request_id") {
        Serial.println("sending response: request_id!");
        sSendString->setValue(UI_ID); //randomly generated device UI identifier
        sSendString->notify();
      } else {
        //handle action! - this is where we'd interact with peripherals
        Serial.print("we got a new action: ");
        Serial.println(str);
      }

    }
};

void setup() {
  Serial.begin(115200);
  Serial.print("Device Name:");
  Serial.println(DEVICENAME);

  BLEDevice::init(DEVICENAME);
  BLEServer *btServer = BLEDevice::createServer();
  //btServer->setCallbacks(new ConnectionServerCallbacks());

  BLEService *sRecive = btServer->createService(RECIVE);
  uint32_t cwrite = BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE;

  BLECharacteristic *sReciveString = sRecive->createCharacteristic(RECIVE_STRING, cwrite);
  sReciveString->setCallbacks(new WriteString());


  BLEService *sSend = btServer->createService(SEND);
  uint32_t cnotify = BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_WRITE  |
                     BLECharacteristic::PROPERTY_NOTIFY | BLECharacteristic::PROPERTY_INDICATE;

  sSendString = sSend->createCharacteristic(SEND_STRING, cnotify);
  sSendString->addDescriptor(new BLE2902());
  sSendString->setValue("BLE Val");

  //sRecive->start();
  sSend->start();
  
  BLEAdvertising *pAdvertising = btServer->getAdvertising();
  pAdvertising->start();
}

void loop() {}
