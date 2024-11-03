package com.example; 
import com.example.MoonDataDlient;

import main.java.com.example.MoonDataClient;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        
        MoonDataClient client = new MoonDataClient();
        System.out.println(client.findAll());

    }
}
