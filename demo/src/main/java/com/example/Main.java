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

        MoonClient client = new MoonClient();

       // HERE WILL BE THE FIRST COMMUCATION OF ARDUINO AND JAVA, GETTING GPS COORDINATES.........
       String myLocation = client.getDataMyLocation(port);
       String moons_data ="0,0";

        // next step is to do an HTTPS request for the moons position....
        moons_data = client.moonPosition(myLocation);
        System.out.println("Hello ? " + moons_data);

        JSONObject json = new JSONObject(moons_data);
        JSONObject cell = json
            .getJSONObject("data")
            .getJSONObject("table")
            .getJSONArray("rows").getJSONObject(0)
            .getJSONArray("cells").getJSONObject(0);

        String azimuth = cell
            .getJSONObject("position")
            .getJSONObject("horizontal")
            .getJSONObject("azimuth")
            .getString("degrees");

        String altitude = cell
            .getJSONObject("position")
            .getJSONObject("horizontal")
            .getJSONObject("altitude")
            .getString("degrees");

        // Example: Send to Arduino over Serial
        String messageToSend = azimuth + ","+ altitude;
        System.out.println("Sending this to arduino motors : " + messageToSend);

      //---------------------------- HERE ITS THE SECOND SERIAL COMM GIVING ARDUINO THE COORDS FOR THE MOON
    
       if(arduinoPort.openPort()){
            System.out.println("Port opened successfully");
            // Add delay to ensure data is sent
            try {
                Thread.sleep(2000);  // Wait 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sendIntToArduino(arduinoPort,messageToSend);

            arduinoPort.closePort();
       }else{
        System.out.println("Port couldnt be opened");
        return;
       }
    }


    public static void sendIntToArduino(SerialPort port, String moonsCoord) {
        // Convert to string with newline
        byte[] buffer = moonsCoord.getBytes();
    
        // Actually send the data
        port.writeBytes(buffer, buffer.length);
    
        System.out.println("Data was SENT: " + buffer);
        System.out.print("Data was buffer in bit:");
        for(byte bit : buffer){
            System.out.print(bit);
        }
    }
}
