package com.example.shaha.eventfinderandroid.Utils;

import android.util.Log;

import com.example.shaha.eventfinderandroid.EventUser;
import com.example.shaha.eventfinderandroid.MyEvent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shaha on 08/01/2018.
 */

public class InternetUtils {
    static String baseUrl = "http://eventfinder.cloudapp.net/service1.svc/";

    public static int userExist(JSONObject json) {
        //set default value
        int result = -1;

        try {
            //create json object for the response
            URL url = createURl(baseUrl + "login");
            String res = sendPost(url, json);
            //handle res
            JSONObject jsonObject = new JSONObject(res);
            String jsonResult = jsonObject.getString("success");
            if (jsonResult.equals("false")) {
                result = -1;
            } else { //login succeeded
                String userIdStr = jsonObject.getString("data");
                result = Integer.valueOf(userIdStr);
                return result;
            }
        } catch (IOException e) {
            Log.e("InternetUtils", "Error with the request");
            result = -2;
        } catch (JSONException e) {
            Log.e("InternetUtils", "Error with the request");
            result = -2;
        } finally {
            return result;
        }
    }

    public static List<MyEvent> getAllEvents() {
        List<MyEvent> events = null;
        try {
            URL url = createURl(baseUrl + "events");
            String res = makeGetRequest(url);

            //transform into a json object
            JSONObject jsonObject = new JSONObject(res);

            //extract events from the json object
            events = extractEvents(jsonObject);
            return events;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return events;
        }
    }

    public static List<EventUser> getEventAttendings(int eventID) {
        List<EventUser> attendings = new ArrayList() {
        };
        try {
            URL url = createURl(baseUrl + "events/" + eventID + "/attendings");
            String res = makeGetRequest(url);

            //transform into a json object
            JSONObject jsonObject = new JSONObject(res);

            //extract events from the json object
            attendings = extractAttendings(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return attendings;
        }
    }

    private static List<EventUser> extractAttendings(JSONObject jsonObject) {
        List<EventUser> attendings = new ArrayList<>();
        try {
            JSONArray usersJson = jsonObject.getJSONArray("data");

            //iterate on each json object
            for (int i = 0; i < usersJson.length(); i++) {
                JSONObject userJson = (JSONObject) usersJson.get(i);
                int userID = userJson.getInt("ID");
                String firstName = userJson.getString("FirstName");
                String lastName = userJson.getString("LastName");
                String phoneNumber = userJson.getString("PhoneNumber");

                EventUser attending = new EventUser("", userID, phoneNumber, firstName + " " + lastName);
                attendings.add(attending);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return attendings;
        }
    }

    private static List<MyEvent> extractEvents(JSONObject jsonObject) {
        List<MyEvent> events = new ArrayList<>();
        try {
            JSONArray eventsJson = jsonObject.getJSONArray("data"); //fields ID,Description,Name,StartTime,EndTime,Latitude,Longtitude,UserID

            //iterate on each json object
            for (int i = 0; i < eventsJson.length(); i++) {
                JSONObject eventJson = (JSONObject) eventsJson.get(i);
                int eventID = eventJson.getInt("ID");
                String description = eventJson.getString("Description");
                String eventName = eventJson.getString("Name");
                String startTime = eventJson.getString("StartTime");
                String endTime = eventJson.getString("EndTime");
                Double latitude = eventJson.getDouble("Latitude");
                Double longtitude = eventJson.getDouble("Longtitude");
                int userId = eventJson.getInt("UserID");
                int type = eventJson.getInt("Type");

                MyEvent event = new MyEvent(eventName, startTime, endTime, description, userId, longtitude, latitude, eventID, type);
                events.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return events;
        }
    }

    public static List<MyEvent> getUserEvents(int userId) {
        List<MyEvent> events = null;
        try {
            URL url = createURl(baseUrl + "users/events2/" + userId);
            String res = makeGetRequest(url);

            //transform into a json object
            JSONObject jsonObject = new JSONObject(res);

            events = extractEvents(jsonObject);
            return events;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return events;
        }
    }

    public static boolean joinEvent(int eventId, int userID) {
        boolean success = false;
        try {

            URL url = createURl(baseUrl + "events/" + eventId + "/" + userID);
            String res = makeGetRequest(url);

            //transform into a json object
            JSONObject jsonObject = new JSONObject(res);
            String result = jsonObject.getString("success");
            if (result.equals("true")) {
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return success;
        }
    }

    private static URL createURl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("InternetUtils", "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeGetRequest(URL url) throws IOException {
        String jsonResponse = "";
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("InternetUtils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("InternetUtils", "Problem", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // HTTP POST request
    private static String sendPost(URL url, JSONObject json) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String jsonRes = "";

        try {
            HttpPost request = new HttpPost(url.toString());
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity resEntity = response.getEntity();
            jsonRes = EntityUtils.toString(resEntity);
            // handle response here...
        } catch (Exception ex) {
            // handle exception here
        } finally {
            httpClient.close();
        }
        return jsonRes;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static int registerUser(JSONObject json) {
        try {
            //create json object for the response
            URL url = createURl(baseUrl + "register");
            String res = sendPost(url, json);
            //handle res
            JSONObject jsonObject = new JSONObject(res);
            String result = jsonObject.getString("success");
            if (result.equals("false")) {
                return -1;
            } else {
                String userIdStr = jsonObject.getString("data");
                return Integer.valueOf(userIdStr);
            }
        } catch (IOException e) {
            Log.e("InternetUtils", "Error with the request");
        } catch (JSONException e) {
            Log.e("InternetUtils", "Error with the request");
        }
        return -1;
    }

    //add event: Description, Name, StartTime, EndTime, Latitude, Longtitude, UserId, Type
    public static boolean createEvent(String description, String name, String startTime, String endTime, Double latitude, Double longtitude, int userId, int type) {
        boolean ans = false;
        try {
            URL url = createURl(baseUrl + "events");
            JSONObject jsonObject = createJSON(description, name, startTime, endTime, latitude, longtitude, userId, type);

            String resStr = sendPost(url, jsonObject);

            //handle response
            JSONObject jsonResponse = new JSONObject(resStr);
            String result = jsonResponse.getString("success");
            if (!result.equals("false")) {
                ans = true;
            } else {
                ans = false;
            }

        } catch (Exception e) {
            Log.e("InternetUtils", "Error with createEvent");
        } finally {
            return ans;
        }
    }

    private static JSONObject createJSON(String description, String name, String startTime, String endTime, Double latitude, Double longtitude, int userId, int type) {
        JSONObject json = new JSONObject();
        try {
            json.put("Description", description);
            json.put("Name", name);
            json.put("StartTime", startTime);
            json.put("EndTime", endTime);
            json.put("Latitude", latitude);
            json.put("Longtitude", longtitude);
            json.put("UserID", userId);
            json.put("Type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
