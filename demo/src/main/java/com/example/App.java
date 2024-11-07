package com.example; 
import org.json.JSONObject;
import org.json.JSONStringer;

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
        float[] coordinates = dataAzimuthLantitude(moonposition);

        System.out.println("Azimuth: " + coordinates[0]);
        System.out.println("Altitude: " + coordinates[1]);


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

    public static float[] dataAzimuthLantitude(String jsonAllData){
        try{
            JSONObject jsonObject = new JSONObject(jsonAllData);

             // Достъп до вложените JSON обекти и извличане на данни
        JSONObject moonObject = jsonObject.getJSONObject("data").getJSONObject("moon");
        float azimuth = moonObject.getFloat("azimuth");
        float altitude = moonObject.getFloat("altitude");

            float[] floatMoonPosition = new float[] {azimuth,altitude};

            return floatMoonPosition;
        }catch(Exception e ){
            System.out.println("Unfortunately your data couldnt be parsed from JSON -> String -> float because of this error : "  + e.getMessage());
            return null;
        }
    }
}
