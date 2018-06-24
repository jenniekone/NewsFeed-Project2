package com.example.android.newsfeed;


import android.text.TextUtils;
import android.util.Log;

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
 * Helper methods related to requesting and receiving news data from the url.
 */
public final class QueryUtils {


    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods,
     * * Create a private constructor because no one should ever create a {@link QueryUtils} object..
     */
    private QueryUtils() {
    }


    /**
     * Query the USGS dataset and return a list of {@link NewsFeed} objects.
     */
    public static List <NewsFeed> fetchNewsFeedData(String requestUrl) {

        // Create URL object
        URL url = createUrl( requestUrl );

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest( url );
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem making the HTTP request.", e );
        }


        // Extract relevant fields from the JSON response and create a list of {@link news}s
        List <NewsFeed> newsfeeds = extractResultsFromJson( jsonResponse );

        // Return the list of {@link Newsfeed}s
        return newsfeeds;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e( LOG_TAG, "Problem building the URL ", e );
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout( 10000 /* milliseconds */ );
            urlConnection.setConnectTimeout( 15000 /* milliseconds */ );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            } else {
                Log.e( LOG_TAG, "Error response code: " + urlConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem retrieving the news JSON results.", e );
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream, Charset.forName( "UTF-8" ) );
            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();
            while (line != null) {
                output.append( line );
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link NewsFeed} objects that has been built up from
     * parsing a JSON response.
     * parsing the given JSON response.
     */
    private static List <NewsFeed> extractResultsFromJson(String newsfeedJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty( newsfeedJSON )) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List <NewsFeed> newsfeeds = new ArrayList <>();

        // parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.

        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject( newsfeedJSON );

            // For a given news, extract the JSONObject associated with the
            // key called "response",
            JSONObject response = baseJsonResponse.getJSONObject( "response" );
            // Extract the JSONArray with the key called "result",
            // which represents a list of results (or news).
            JSONArray newsfeedArray = response.getJSONArray( "results" );

            //in the newsfeedArray, I create an {@link news} object
            for (int i = 0; i < newsfeedArray.length(); i++) {

                // Get a single news at position i within the list of news
                JSONObject currentNewsFeed = newsfeedArray.getJSONObject( i );


                // Extract the value for the key called "type"
                String id = currentNewsFeed.getString( "sectionId" );

                // Extract the value for the key called "name"
                String name = currentNewsFeed.getString( "sectionName" );

                // Extract the value for the key called "title"
                String title = currentNewsFeed.getString( "webTitle" );

                // Extract the value for the key called "date"
                String date = currentNewsFeed.getString( "webPublicationDate" );

                // Extract the value for the key called author tied to webtitle
                String author;
                JSONArray tags = currentNewsFeed.getJSONArray( "tags" );
                if (tags != null && tags.length() > 0) {
                    JSONObject tagsObject = tags.getJSONObject( 0 );
                    author = tagsObject.optString( "webTitle", "No Author" );
                } else author = "No author name";

                // Extract the value for the key called "url"
                String url = currentNewsFeed.getString( "webUrl" );

                //{@link news} object with the type, name, title, time,
                // and url from the JSON response.
                NewsFeed newsfeed = new NewsFeed( id, name, title, date, author, url );

                // Add the new {@link News Feed} to the list of news feeds.
                newsfeeds.add( newsfeed );
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e( "QueryUtils", "Problem parsing the news feed JSON results", e );
        }

        // Return the list of news
        return newsfeeds;
    }
}