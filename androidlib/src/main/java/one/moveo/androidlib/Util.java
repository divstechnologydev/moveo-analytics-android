package one.moveo.androidlib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Util {
    private static final String TAG = "MoveoOneHttpService";
    private static final int CONNECTION_TIMEOUT = 2000;
    private static final int READ_TIMEOUT = 30000;
    private static final int MAX_RETRIES = 3;

    public static boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } catch (SecurityException e) {
            Log.e(TAG, "No permission to check connectivity", e);
            return true;
        }
    }

    public static byte[] performPostRequest(String endpointUrl, List<MoveoOneEntity> entities, String token) throws IOException {
        Log.d(TAG, "Attempting POST request to " + endpointUrl + " with " + entities.size() + " entities");
        
        int retries = 0;
        boolean succeeded = false;
        byte[] response = null;

        while (retries < MAX_RETRIES && !succeeded) {
            HttpURLConnection connection = null;
            OutputStream out = null;
            BufferedOutputStream bout = null;
            InputStream in = null;

            try {
                // Setup connection
                URL url = new URL(endpointUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.setReadTimeout(READ_TIMEOUT);

                // Setup request method and headers
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", token);
                connection.setDoOutput(true);
                
                // Convert List<MoveoOneEntity> to JSON Array
                JSONArray jsonArray = new JSONArray();
                for (MoveoOneEntity entity : entities) {
                    JSONObject jsonEntity = new JSONObject();
                    jsonEntity.put("c", entity.getC());
                    jsonEntity.put("type", entity.getType());
                    jsonEntity.put("t", entity.getT());
                    if (entity.getProp() != null) {
                        jsonEntity.put("prop", new JSONObject(entity.getProp()));
                    }
                    if (entity.getMeta() != null) {
                        jsonEntity.put("meta", new JSONObject(entity.getMeta()));
                    }
                    if (entity.getAdditionalMeta() != null){
                        jsonEntity.put("additionalMeta", new JSONObject(entity.getAdditionalMeta()));
                    }
                    jsonEntity.put("sId", entity.getSId());
                    jsonArray.put(jsonEntity);
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("events", jsonArray);
                // Convert JSON Array to bytes and log request body
                String jsonBody = jsonObject.toString();
                Log.d(TAG, "Request body: " + jsonBody);
                byte[] postData = jsonBody.getBytes(StandardCharsets.UTF_8);
                
                // Write JSON to connection
                connection.setFixedLengthStreamingMode(postData.length);
                out = connection.getOutputStream();
                bout = new BufferedOutputStream(out);
                bout.write(postData);
                bout.flush();

                // Get response
                int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    in = connection.getInputStream();
                    response = readStream(in);
                    succeeded = true;
                } else {
                    // Read error response
                    InputStream errorStream = connection.getErrorStream();
                    String errorResponse = errorStream != null ? 
                        new String(readStream(errorStream), StandardCharsets.UTF_8) : "No error details";
                    Log.e(TAG, "Server returned code " + responseCode + " with error: " + errorResponse);
                    throw new IOException("Server returned code " + responseCode + ": " + errorResponse);
                }

            } catch (Exception e) {
                Log.e(TAG, "Request failed, attempt " + (retries + 1) + " of " + MAX_RETRIES, e);
                retries++;
                if (retries >= MAX_RETRIES) {
                    throw new IOException("Failed after " + MAX_RETRIES + " attempts", e);
                }
            } finally {
                // Cleanup
                if (bout != null) try { bout.close(); } catch (IOException ignored) {}
                if (out != null) try { out.close(); } catch (IOException ignored) {}
                if (in != null) try { in.close(); } catch (IOException ignored) {}
                if (connection != null) connection.disconnect();
            }
        }

        return response;
    }

    private static byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[8192];
        
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        
        return buffer.toByteArray();
    }
}
