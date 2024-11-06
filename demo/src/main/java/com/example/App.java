package com.example; 
import com.example.MoonDataDlient;
import main.java.com.example.MoonDataClient;
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

        System.out.println(moonposition);

        // 
        // Replace with your Arduino's COM port(Like "COM3" on Windows,"/dev/ttyUSB0" on Linux)
        SerialPort comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudrate(9600);
        if(comPort.openPort()){
            System.out.println("Port is OPEN !");
        }else{
            System.out.println("Failed to open");
            return;
        }



    }
}
