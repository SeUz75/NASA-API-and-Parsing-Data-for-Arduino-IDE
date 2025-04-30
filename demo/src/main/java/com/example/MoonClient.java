package com.example;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Base64;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fazecast.jSerialComm.*;

public class MoonClient {
        // HTTPS REQUEST .................... 
        // BUT FIRST IMMA NEED TO DO THE FIRST SERIAL COMMUNICATION WHICH IS 
        // READING THE GPS COORDINATES FROM MY GPS NEO 6Y MODULE
        private String apiKey = "API-ID";
        private String apiSecret = "API-ID";
        

    public String moonPosition(String mylocation) {
       try {
        // Replace these with your actual values
        String latitude,longitude;
        if(mylocation == null ){
            System.out.println("Using hardcoded coords : ");
             latitude = "42.1354";
             longitude = "24.7453";
        }else{
            String[] parts = mylocation.split(",");
            latitude = parts[0].trim();
            longitude = parts[1].trim();
        }
        
        String elevation = "0";
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String fromDate = now.format(dateFormatter);
        String toDate = fromDate;
        String time = now.format(timeFormatter);

        String userPass = apiKey + ":" + apiSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(userPass.getBytes(StandardCharsets.UTF_8));

        // Construct the full URL with query parameters

        String urlStr = String.format(
            "https://api.astronomyapi.com/api/v2/bodies/positions/moon?" +
            "latitude=%s&longitude=%s&elevation=%s&from_date=%s&to_date=%s&time=%s",
            latitude, longitude, elevation, fromDate, toDate, time
        );

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode >= 200 && responseCode < 300)
                ? conn.getInputStream()
                : conn.getErrorStream();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();

    } catch (Exception e) {
        e.printStackTrace();
        return "Error: " + e.getMessage();
    }
}

        
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
}
