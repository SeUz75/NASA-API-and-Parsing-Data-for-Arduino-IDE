package com.example; 
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.Math;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        
        MoonDataClient client = new MoonDataClient();
        // Getting my API answer for my CURRENT POSITION !!! 
        // Specify your port name  FOR EVERY OS, FOR WINDOWS COM3, FOR MACOS /dev/tty.usbmodem14101, FOR LINUX /dev/tty.USB0
        String portDescriptor = "COM4";

        // If my gps was WORKING I WOULD PROBABLY GET DATA FOR MY COORDS AND THE PROGRAM SHOULD RUN ! 
        // WAITING for new GPS MODULE !
        String gpsData = client.getData(portDescriptor);

         // Use hardcoded coordinates if GPS data is null or invalid
        if (gpsData == null || gpsData.isEmpty()) {
            System.out.println("GPS data is null or invalid. Using hardcoded coordinates...");
            gpsData = "42.1354,24.7453"; // Hardcoded coordinates for Plovdiv, Bulgaria
        }
        
        String moonposition = client.findMoonData(gpsData);
        double[] coordinates = dataAzimuthLantitude(moonposition);

        System.out.println("Azimuth,Lantitude cood in radians : " + coordinates[0]+","+coordinates[1]);

        // But I need the coordinates to be in degrees not in radians so : 
        double[] degrees=moonCoordInDegrees(coordinates);
        System.out.println("Azimuth, Altitude coord in degrees : " + degrees[0]+","+degrees[1]);


        String dataToSend = degrees[0]+","+degrees[1]+"\n";
        sendData(dataToSend, portDescriptor);
        

        // Read response from Arduino
        String arduinoResponse = client.getData(portDescriptor);
        if (arduinoResponse == null) {
            System.out.println("Arduino didn't receive anything.");
        } else {
            System.out.println("Arduino read: " + arduinoResponse);
        }  
    }  

    // |----------------------------------------------------- SENDING MOON LOCATION !!! -----------------------------------------------------|

    public static void sendData(String dataToSend, String portDescriptor){
         // Check if the port exists
         SerialPort comPort = SerialPort.getCommPort(portDescriptor);
         comPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
         comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 1000, 0);

         comPort.openPort();
        if (!comPort.openPort()) {
            System.out.println("Failed to open port: SEND DATA " + portDescriptor);
            if (comPort.getLastErrorCode() != 0) {
                System.out.println("Error Code: " + comPort.getLastErrorCode());
            }
            return;
        }
        try {
            String degreeString =dataToSend + "\n";
            byte[] buffer = degreeString.getBytes();
            comPort.writeBytes(buffer,buffer.length);
            System.out.println("Data was SENT: " + buffer);
            System.out.print("Data was buffer in bit:");
            for(byte bit : buffer){
                System.out.print(bit);
            }
            // // Send data to Arduino
            // OutputStream out = comPort.getOutputStream();
            // out.write((dataToSend +"\n").getBytes());
            // out.flush();
            // System.out.println("Sent to Arduino: " + dataToSend);
            // out.close();
        }catch(Exception e){
            System.out.println("Failed to send data because : " + e.getMessage());
        }finally{
            comPort.closePort();
        }
    }

    // |----------------------------------------------------- Parsing data from JSON to local double variables in my JavaProgram !!! -----------------------------------------------------|

    public static double[] dataAzimuthLantitude(String jsonAllData){
        double[] doubleMoonPosition= new double[2];
        try{
            if(jsonAllData == null){
                System.out.println("Bruh jsonAllData is null ");
            }
            JSONObject jsonObject = new JSONObject(jsonAllData);

             // Достъп до вложените JSON обекти и извличане на данни
        JSONObject moonObject = jsonObject.getJSONObject("data").getJSONObject("moon");
        double azimuth = moonObject.getDouble("azimuth");
        double altitude = moonObject.getDouble("altitude");

             doubleMoonPosition = new double[] {azimuth,altitude};

            return doubleMoonPosition;
        }catch(Exception e ){
            System.out.println("Unfortunately your data couldnt be parsed from JSON -> String -> double because of this error : "  + e.getMessage());
            doubleMoonPosition[0] = 1.2;
            doubleMoonPosition[1] =2.3;
            return doubleMoonPosition;
        }
    }


    // |----------------------------------------------------- Converting from Radians to degrees. -----------------------------------------------------|
    public static double[] moonCoordInDegrees(double[] coord){
        double[] degrees= new double[2];

        for(int i=0;i<coord.length;i++){
            degrees[i] = coord[i]*(180/Math.PI); // Because of Math.PI I will need to change my variable from float to double....
        }
        return degrees;
    }
}
