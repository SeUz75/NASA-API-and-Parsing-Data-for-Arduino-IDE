package com.example;
import com.fazecast.jSerialComm.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;

public class Functions {
    public String getDataMyLocation(String portDescriptor){
        SerialPort arduinoPort = SerialPort.getCommPort(portDescriptor);
        try{
            // Configure Port
            arduinoPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);
            if(!arduinoPort.openPort()){
                System.out.println("Failed to open port: " + portDescriptor);
                if (arduinoPort.getLastErrorCode() != 0) {
                    System.out.println("Error Code: " + arduinoPort.getLastErrorCode());
                }
                return null;
            }

            System.out.println("Port opened : "  + portDescriptor);
            // Give the Arduino time to reset after connection
            String receivedData = "";
            // Read data from serial port
            //  IMPORTANT FOR CYCLE !!! IMPROVES VERY MUCH THE SERIAL COMMUNICATION ! CORRECT THE GPS COORDINATES ! 
            for(int i =0; i<=2; i++){   
            Thread.sleep(2000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(arduinoPort.getInputStream()));
            receivedData = reader.readLine();
            System.out.println("Data received from arduino is : "  + receivedData);
            }

            if(receivedData !=null){
                System.out.println("Data received from arduino is : "  + receivedData);
                return receivedData;
            }


        }catch(Exception e){
            System.out.println("Error reading from serial port: " + e.getMessage());
            String myLoc = "42.1354,24.7453";
            System.out.println("Using hard coded coordinates : " + myLoc );
           // e.printStackTrace();
            return myLoc;
        }finally{
            arduinoPort.closePort();
            System.out.println("Port closed: " + portDescriptor); // CLOSING PORT ! REALLY IMPORTANT LINE OF CODE
        }
        return null;
    }


    public String celestialCoordinations(String jsonFile){
        JSONObject json = new JSONObject(jsonFile);
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

        return azimuth+","+altitude;
    }



    public void sendIntToArduino(SerialPort port, String moonsCoord) {
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
