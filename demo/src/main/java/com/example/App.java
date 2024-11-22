package com.example; 
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;
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
        


        // //System.out.println(moonposition);

        // SerialPort[] ports = SerialPort.getCommPorts();
        // System.out.println("Available ports:");
        // for (SerialPort port : ports) {
        //     System.out.println(port.getSystemPortName());
        // } //TRYING TO BEDUG TO SEE WHICH PORTS ARE AVAILABLE

        // Specify your port name  FOR EVERY OS, FOR WINDOWS COM3, FOR MACOS /dev/tty.usbmodem14101, FOR LINUX /dev/tty.USB0
        String portDescriptor = "COM3";

        // Check if the port exists
        SerialPort comPort = SerialPort.getCommPort(portDescriptor);
        comPort.setComPortParameters(9600, 8, 1, 0);
        

        if (!comPort.openPort()) {
            System.out.println("Failed to open port: " + portDescriptor);
            if (comPort.getLastErrorCode() != 0) {
                System.out.println("Error Code: " + comPort.getLastErrorCode());
            }
            return;
        }


        String dataToSend = degrees[0]+","+degrees[1]+"\n";
        try{
            Thread.sleep(1000); // Wait 1 second before sending data
            comPort.getOutputStream().write(dataToSend.getBytes());
            comPort.getOutputStream().flush();
            System.out.println("Data send to arduino : " + dataToSend);

        }catch(Exception e){
            System.out.println("Failed to send data, error : "  + e.getMessage());
        }finally{
            comPort.closePort();
        }


        InputStream inputStream = comPort.getInputStream();
        try{
            int availableBytes = inputStream.available();
            if (availableBytes > 0) {
                byte[] readBuffer = new byte[availableBytes];
                inputStream.read(readBuffer);
                String response = new String(readBuffer);
                System.out.println("Arduino responded: " + response);
            }
        }catch(Exception e){
            System.out.println("Failed to receive data from the arduino.");
        }

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
