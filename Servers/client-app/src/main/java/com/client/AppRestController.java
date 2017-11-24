package com.client;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.ModelMap;
//import javax.servlet.http.HttpServletRequest;
//import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;


import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.*;
import org.apache.http.*;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.params.*;
import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Base64;

import java.net.URLEncoder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.nio.charset.StandardCharsets;
import java.net.*;
import java.io.*;
import javax.json.*;
import org.json.*;

import org.json.JSONObject;
@RestController
public class AppRestController {


    @RequestMapping(value = "/login-dbook", method=RequestMethod.GET)
    public RedirectView processForm1() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://localhost:9998/auth/oauth/authorize?response_type=code&client_id=123-456-789&redirect_url=http://localhost:9999/oauth/access?key=value&scope=dbook_user_read%20dbook_user_write");
        return redirectView;
    }
    @RequestMapping(value = "/login",params={"user_login"}, method=RequestMethod.POST)
    public String processForm() {

        return  "Welcome";
    }


    @RequestMapping(value = "/oauth/access", method = RequestMethod.GET)
    public String handleResponse(ModelMap model, @RequestParam(value = "code",required=true) String authCode) {

        String accessToken = getAccessToken(authCode); //Get the access token from the authorization server
        return getResource(accessToken); //Get the resource from the resource server using the access token

    }

    public String getAccessToken(String authCode)
    {
        String auth_url = "http://localhost:9998/auth/oauth/token";
        String POST_PARAMS = "grant_type=authorization_code&code="+authCode;
        String accessToken = "";

        try
        {
            URL obj = new URL(auth_url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            //Set Headers
            con.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Basic MTIzLTQ1Ni03ODk6YWJjLWFiYy1hYmM=");

            //Set Body
            con.setDoOutput(true);
            OutputStream os = con.getOutputStream();
            os.write(POST_PARAMS.getBytes());
            os.flush();
            os.close();


            //Execute and get the response
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK)//success
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
                in.close();

                //Convert the response to a json Object
                JSONObject jsonObj = new JSONObject(response.toString());

                //Get the access token from json object
                accessToken = jsonObj.getString("access_token");

            }
            else
            {
                System.out.println("Error : " + responseCode);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }

        return accessToken;
    }

    public String getResource(String accessToken)
    {
        try
        {
            String ResourceUrl = "http://localhost:9997/users";

            URL obj = new URL(ResourceUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            //Set Header
            con.setRequestProperty("Authorization", "Bearer "+accessToken);

            //Execute and get the response
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) //success
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer ResourceResponse = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                {
                    ResourceResponse.append(inputLine);
                }
                in.close();

                //Convert the ouput to json array
                JSONArray jsonArray = new JSONArray(ResourceResponse.toString());

                //Prepare the ouput from the json array
                String output = "<!DOCTYPE html> \n"
                        +"<html lang=\"en\"> \n"
                    +"<head> \n"
                        +"<style>\n"
                        +"*, *:before, *:after {box-sizing: border-box;}\n"
                        +"html {overflow-y: scroll;}\n"

                        +"body {background: #c1bdba;font-family: 'Titillium Web', sans-serif;}\n"
                        +"h1 {text-align: center;color: #ffffff;font-weight: 300;margin: 0 0 40px;}\n"
                        +".form {background: rgba(19, 35, 47, 0.9);padding: 40px;max-width: 600px;margin: 40px auto;border-radius: 4px;box-shadow: 0 4px 10px 4px rgba(19, 35, 47, 0.3);}\n"
                        +"table.minimalistBlack {border: 3px solid #FFFFFF;width: 100%;text-align: center;border-collapse: collapse;}\n"
                        +"table.minimalistBlack td, table.minimalistBlack th {border: 1px solid #FFFFFF;padding: 8px 8px;}\n"
                        +"table.minimalistBlack tbody td {font-size: 13px;color: #FFFFFF;}\n"
                        +"table.minimalistBlack thead {background: #FFFFFF;background: -moz-linear-gradient(top, #ffffff 0%, #ffffff 66%, #FFFFFF 100%);background: -webkit-linear-gradient(top, #ffffff 0%, #ffffff 66%, #FFFFFF 100%);background: linear-gradient(to bottom, #ffffff 0%, #ffffff 66%, #FFFFFF 100%);border-bottom: 3px solid #FFFFFF;}\n"
                        +"table.minimalistBlack thead th {font-size: 15px;font-weight: bold;color: #ffffff;text-align: center;}\n"
                        +"table.minimalistBlack tfoot td {font-size: 14px;}\n"
                        +"</style>\n"
                    +"</head>\n"
                    +"<body>\n"
                    +"<div class=\"form\">\n"
                    +"<h1>DBook - Friends List of Ben</h1>\n"
                    +"<table class=\"minimalistBlack\">\n"
                    +"<tr><th>ID</th><th>Name</th><th>Profession</th></tr>";
                System.out.println(jsonArray);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject explrObject = jsonArray.getJSONObject(i);
                    output += "<tr>";
                    output += "<td>" + explrObject.getString("id") + "</td>";
                    output += "<td>" + explrObject.getString("name") + "</td>";
                    output += "<td>" + explrObject.getString("profession") + "</td>";
                    output += "<tr>";
                }
                output += "</table>\n"
                        +"</div> <!-- /form -->\n"
                        +"<script src=\"js/jquery.min.js\"></script>\n"
                    +"<script  src=\"js/index.js\"></script>\n"
                    +"</body>\n"
                    +"</html>";
                return output;

            }
            else
            {
                System.out.println("Error : " + responseCode);
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex);
        }

        return null;
    }

}