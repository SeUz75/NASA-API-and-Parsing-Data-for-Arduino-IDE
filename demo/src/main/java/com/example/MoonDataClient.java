package com.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MoonDataClient {

    private final String BASE_URL = "https://api.apiverve.com/v1/moonposition?lat=37.7749&lon=-122.4194&date=11-03-2024";
    private final HttpClient client; 

    public MoonDataClient(){
        client = HttpClient.newHttpClient();
    }
    public String findAll(){
        try
        {
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .header("x-api-key", "API_KEY")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        }
        catch(Exception e)
        {
            System.out.println("Error occured with the exception : " + e );
            return null;
        }
    }
}



