package com.example;

import org.json.JSONObject;

import com.fazecast.jSerialComm.*;

public class Main {
    public static void main(String[] args) {
        String port = "COM4";
       SerialPort arduinoPort = SerialPort.getCommPort(port);
       //Configure Port
       arduinoPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
       arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
        Functions functions = new Functions();
        MoonClient client = new MoonClient();
       // HERE WILL BE THE FIRST COMMUCATION OF ARDUINO AND JAVA, GETTING GPS COORDINATES.........
       String myLocation = functions.getDataMyLocation(port);
       String moons_data ="0,0";

        // next step is to do an HTTPS request for the moons position....
        moons_data = client.moonPosition(myLocation);
        System.out.println("Hello ? " + moons_data);

        String celestialObjectCoords = functions.celestialCoordinations(moons_data);
        // Example: Send to Arduino over Serial
     
        System.out.println("Sending this to arduino motors : " + celestialObjectCoords);

      //---------------------------- HERE ITS THE SECOND SERIAL COMM GIVING ARDUINO THE COORDS FOR THE MOON
    
       if(arduinoPort.openPort()){
            System.out.println("Port opened successfully");
            // Add delay to ensure data is sent
            try {
                Thread.sleep(2000);  // Wait 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            functions.sendIntToArduino(arduinoPort,celestialObjectCoords);

            arduinoPort.closePort();
       }else{
        System.out.println("Port couldnt be opened");
        return;
       }
    }
}
