package com.atlanForms.demo.utils;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
@Component
public class HttpUtils {

    private CloseableHttpClient httpClient = null;
    private String geoCodeUrl = "https://maps.googleapis.com/maps/api/geocode/json";

    public void init() {
        int CONNECTION_TIMEOUT = 60000;


        SocketConfig socketConfig = SocketConfig.copy(SocketConfig.DEFAULT)
                .setSoTimeout(2 * CONNECTION_TIMEOUT).build();


        RequestConfig requestConfig = RequestConfig
                .copy(RequestConfig.DEFAULT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setRedirectsEnabled(false)
                // max redirects is set to 4
                .setMaxRedirects(4).setCircularRedirectsAllowed(false)
                .build();

        httpClient = HttpClientBuilder
                .create()
                .setDefaultSocketConfig(socketConfig)
                .setDefaultRequestConfig(requestConfig)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
    }

    public String getAddressFromLatLng(int latitude, int longitude) throws URISyntaxException, IOException {

        // Api key later to be added as Environment variable
        // URL to get response for coordinates
        URI requestURI = new URIBuilder(geoCodeUrl).
                addParameter("latlng", String.valueOf(latitude) + "," + String.valueOf(longitude)).
                addParameter("key", "AIzaSyCGWQJmndC4_iQs9_Vpj9lyOV90Pv8GVzc").build();
        HttpPost httpRequest = new HttpPost(requestURI);
        HttpResponse httpResponse = httpRequestExecute(httpRequest);
        JSONObject jsonResponse = convertResponseToJSONObject(httpResponse);
        String location = jsonResponse.getJSONObject("plus_code").getString("compound_code");
        return location;
    }

    private String convertResponseToString(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity()
                .getContent()));
        StringBuilder strLine = new StringBuilder();
        String resLine;
        while ((resLine = rd.readLine()) != null) {
            strLine.append(resLine);
        }
        String res = strLine.toString();
        return res;
    }

    private JSONObject convertResponseToJSONObject(HttpResponse response) throws IOException,
            JSONException {
        JSONObject obj;
        String res = convertResponseToString(response);

        obj = new JSONObject(res);


        return obj;
    }

    private HttpResponse httpRequestExecute(HttpRequestBase request) throws IOException {
        if (request == null) {
            throw new IllegalArgumentException("httpRequestExecute: request cannnot be null");
        }

        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");


        HttpResponse response = null;

        response = httpClient.execute(request);

        return response;
    }
}
