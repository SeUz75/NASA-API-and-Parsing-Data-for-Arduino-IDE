package com.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fazecast.jSerialComm.SerialPort;
import java.time.LocalDate;

public class MoonDataClient {

    private final HttpClient client; 

    public MoonDataClient(){
        client = HttpClient.newHttpClient();
    }

     // Method to fetch Moon data from the API using dynamic GPS coordinates
     public String findMoonData(String gpsData) {
        try {
            // Parse GPS data to extract latitude and longitude
            String[] coords = gpsData.split(",");
            if (coords.length != 2) {
                System.out.println("Invalid GPS data format. Expected format: latitude,longitude");
                return null;
            }

            String latitude = coords[0];
            String longitude = coords[1];

            // Construct the dynamic URL
            LocalDate today = LocalDate.now();
            String dynamicURL = String.format(
            "https://api.apiverve.com/v1/moonposition?lat=%s&lon=%s&date=%s",
            latitude, longitude, today.toString()
            );

            // Create and send the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dynamicURL))
                .header("x-api-key", "API-KEY")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            System.out.println("Error occurred while fetching moon data: " + e);
            return null;
        }
    }  




     public String getData(String portDescriptor){
    

            // Check if the port exists
            SerialPort comPort = SerialPort.getCommPort(portDescriptor);
            comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0); // Adjust timeout
            String gpsData;


            comPort.openPort();
           if (!comPort.openPort()) {
               System.out.println("Failed to open port:" + portDescriptor);
               if (comPort.getLastErrorCode() != 0) {
                   System.out.println("Error Code: " + comPort.getLastErrorCode());
               }
               return null;
           }
           try {
             // Delay to let Arduino initialize
            Thread.sleep(2000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(comPort.getInputStream()));
            String receivedData = reader.readLine();

            System.out.println("Received from Arduino: " + receivedData);

            gpsData = receivedData;
           }catch(Exception e){
               System.out.println("Failed to get data because : " + e.getMessage());
               return null;
           }finally{
               comPort.closePort();
           }
           return gpsData;
    }
}



