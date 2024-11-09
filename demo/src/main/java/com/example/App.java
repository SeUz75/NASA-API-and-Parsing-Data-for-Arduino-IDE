package com.example; 
import org.json.JSONObject;
import org.json.JSONStringer;
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
        //System.out.println(client.findAll());

        String moonposition = client.findAll();
        double[] coordinates = dataAzimuthLantitude(moonposition);

        System.out.println("Azimuth,Lantitude cood in radians : " + coordinates[0]+","+coordinates[1]);

        // But I need the coordinates to be in degrees not in radians so : 
        double[] degrees=moonCoordInDegrees(coordinates);
        System.out.println("Azimuth, Altitude coord in degrees : " + degrees[0]+","+degrees[1]);
        


        //System.out.println(moonposition);

        // Specify your port name  FOR EVERY OS, FOR WINDOWS COM3, FOR MACOS /dev/tty.usbmodem14101, FOR LINUX /dev/tty.USB0
        String portDescriptor = "/dev/tty.usbmodem14101";


        // Check if the port exists
        // SerialPort comPort = SerialPort.getCommPort(portDescriptor);
        // if(comPort == null){
        //     System.out.println("Specified port : " + portDescriptor + " is not available");
        //     return;
        // }




        // Replace with your Arduino's COM port(Like "COM3" on Windows,"/dev/ttyUSB0" on Linux)
        // comPort.setBaudRate(9600);
        // if (comPort.openPort()) {
        //     System.out.println("Port is OPEN!");
        // } else {
        //     System.out.println("Failed to open port. Ensure the device is connected and available.");
        //     return;
        // }
    }



    // Parsing data from JSON to local double variables in my JavaProgram

    public static double[] dataAzimuthLantitude(String jsonAllData){
        try{
            JSONObject jsonObject = new JSONObject(jsonAllData);

             // Достъп до вложените JSON обекти и извличане на данни
        JSONObject moonObject = jsonObject.getJSONObject("data").getJSONObject("moon");
        double azimuth = moonObject.getDouble("azimuth");
        double altitude = moonObject.getDouble("altitude");

            double[] doubleMoonPosition = new double[] {azimuth,altitude};

            return doubleMoonPosition;
        }catch(Exception e ){
            System.out.println("Unfortunately your data couldnt be parsed from JSON -> String -> double because of this error : "  + e.getMessage());
            return null;
        }
    }


    // Converting from Radians to degrees. 
    public static double[] moonCoordInDegrees(double[] coord){
        double[] degrees= new double[2];

        for(int i=0;i<coord.length;i++){
            degrees[i] = coord[i]*(180/Math.PI); // Because of Math.PI I will need to change my variable from float to double....
        }
        return degrees;
    }
}
