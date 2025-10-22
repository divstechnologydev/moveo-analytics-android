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
    private static final int PREDICTION_TIMEOUT = 400;

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

    /**
     * Converts a list of MoveoOneEntity to JSON Array format used by analytics and prediction APIs.
     */
    private static JSONArray convertEntitiesToJsonArray(List<MoveoOneEntity> entities) {
        JSONArray jsonArray = new JSONArray();
        for (MoveoOneEntity entity : entities) {
            try {
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
            } catch (org.json.JSONException e) {
                Log.e(TAG, "Failed to convert entity to JSON", e);
            }
        }
        return jsonArray;
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("events", convertEntitiesToJsonArray(entities));
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

    /**
     * Performs a prediction request to the Dolphin service.
     * 
     * @param modelId The model ID for prediction
     * @param sessionId The current session ID
     * @param events The events from the current buffer
     * @param token The authorization token
     * @return PredictionResponse containing the result
     * @throws IOException if request fails
     */
    public static PredictionResponse performPredictionRequest(String modelId, String sessionId, List<MoveoOneEntity> events, String token) throws IOException {
        Log.d(TAG, "Attempting prediction request for model: " + modelId + " with " + events.size() + " events");
        
        HttpURLConnection connection = null;
        OutputStream out = null;
        BufferedOutputStream bout = null;
        InputStream in = null;

        try {
            // Construct the prediction endpoint URL
            String endpointUrl = Constants.DOLPHIN_BASE_URL + "/api/models/" + 
                    java.net.URLEncoder.encode(modelId, "UTF-8") + "/predict";
            
            // Setup connection with shorter timeout for predictions
            URL url = new URL(endpointUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(PREDICTION_TIMEOUT);
            connection.setReadTimeout(PREDICTION_TIMEOUT);

            // Setup request method and headers
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", token);
            connection.setDoOutput(true);
            
            // Prepare the request payload with events and session ID
            // Reuse the same conversion method as analytics API
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("events", convertEntitiesToJsonArray(events));
                jsonObject.put("session_id", sessionId);
            } catch (org.json.JSONException e) {
                Log.e(TAG, "Failed to create prediction request JSON", e);
                return new PredictionResponse(false, "error", "Failed to create request JSON");
            }
            
            // Convert JSON to bytes
            String jsonBody = jsonObject.toString();
            Log.d(TAG, "Prediction request body: " + jsonBody);
            byte[] postData = jsonBody.getBytes(StandardCharsets.UTF_8);
            
            // Write JSON to connection
            connection.setFixedLengthStreamingMode(postData.length);
            out = connection.getOutputStream();
            bout = new BufferedOutputStream(out);
            bout.write(postData);
            bout.flush();

            // Get response
            int responseCode = connection.getResponseCode();
            
            if (responseCode == 202) {
                // Model is loading/validating - pending state
                String responseBody;
                try {
                    InputStream errorStream = connection.getErrorStream();
                    responseBody = errorStream != null ? 
                        new String(readStream(errorStream), StandardCharsets.UTF_8) : "Model is loading";
                } catch (Exception e) {
                    responseBody = "Model is loading, please try again";
                }
                
                return new PredictionResponse(false, "pending", 
                    responseBody.contains("message") ? parseMessage(responseBody) : "Model is loading, please try again");
            }
            
            if (responseCode == 404) {
                String errorResponse = "";
                try {
                    InputStream errorStream = connection.getErrorStream();
                    errorResponse = errorStream != null ? 
                        new String(readStream(errorStream), StandardCharsets.UTF_8) : "Model not found";
                } catch (Exception e) { 
                    errorResponse = "Model '" + modelId + "' not found or not accessible";
                }
                
                return new PredictionResponse(false, "not_found", 
                    errorResponse.contains("detail") ? parseMessage(errorResponse) : "Model '" + modelId + "' not found or not accessible");
            }
            
            if (responseCode == 409) {
                String errorResponse = "";
                try {
                    InputStream errorStream = connection.getErrorStream();
                    errorResponse = errorStream != null ? 
                        new String(readStream(errorStream), StandardCharsets.UTF_8) : "Conditional event not found";
                } catch (Exception e) {
                    errorResponse = "Conditional event not found";
                }
                
                return new PredictionResponse(false, "conflict", 
                    errorResponse.contains("detail") ? parseMessage(errorResponse) : "Conditional event not found");
            }
            
            if (responseCode == 422) {
                String errorResponse = "";
                try {
                    InputStream errorStream = connection.getErrorStream();
                    errorResponse = errorStream != null ? 
                        new String(readStream(errorStream), StandardCharsets.UTF_8) : "Invalid prediction data";
                } catch (Exception e) {
                    errorResponse = "Invalid prediction data";
                }
                
                // Check if this is a TargetAlreadyReachedError
                if (errorResponse.contains("TargetAlreadyReachedError") || 
                    errorResponse.contains("Completion target already reached")) {
                    return new PredictionResponse(false, "target_already_reached", 
                        "Completion target already reached - prediction not applicable");
                }
                
                return new PredictionResponse(false, "invalid_data", 
                    errorResponse.contains("detail") ? parseMessage(errorResponse) : "Invalid prediction data");
            }
            
            if (responseCode == 500) {
                return new PredictionResponse(false, "server_error", "Server error processing prediction request");
            }
            
            if (responseCode >= 200 && responseCode < 300) {
                // Success - parse prediction response
                in = connection.getInputStream();
                byte[] responseData = readStream(in);
                String responseBody = new String(responseData, StandardCharsets.UTF_8);
                
                return parsePredictionResponse(responseBody);
            } else {
                return new PredictionResponse(false, "error", "Request failed with status " + responseCode);
            }

        } catch (java.net.SocketTimeoutException e) {
            Log.e(TAG, "Prediction request timed out", e);
            return new PredictionResponse(false, "timeout", "Request timed out after " + PREDICTION_TIMEOUT + " milliseconds");
        } catch (IOException e) {
            Log.e(TAG, "Prediction request failed", e);
            return new PredictionResponse(false, "network_error", "Network error - please check your connection");
        } finally {
            // Cleanup
            try { if (bout != null) bout.close(); } catch (IOException ignored) {}
            try { if (out != null) out.close(); } catch (IOException ignored) {}
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            if (connection != null) connection.disconnect();
        }
    }
    
    /**
     * Parses a successful prediction response JSON to PredictionResponse object.
     */
    private static PredictionResponse parsePredictionResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);
            Double predictionProbability = null;
            Boolean predictionBinary = null;
            
            if (json.has("prediction_probability")) {
                predictionProbability = json.optDouble("prediction_probability");
            }
            if (json.has("prediction_binary")) {
                predictionBinary = json.optBoolean("prediction_binary");
            }
            
            return new PredictionResponse(true, "success", "Prediction completed successfully", 
                predictionProbability, predictionBinary);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse prediction response", e);
            return new PredictionResponse(false, "error", "Failed to parse prediction response");
        }
    }
    
    /**
     * Helper method to extract message from error response.
     */
    private static String parseMessage(String errorResponse) {
        try {
            JSONObject json = new JSONObject(errorResponse);
            return json.optString("message", json.optString("detail", "Unknown error"));
        } catch (Exception e) {
            return "Unknown error";
        }
    }

    /**
     * Sends latency data to the prediction-latency endpoint.
     * This method is called asynchronously after prediction response is returned to user.
     * 
     * @param latencyRequest The latency request containing execution metrics
     * @param token The authorization token
     * @throws IOException if request fails
     */
    public static void sendLatencyData(LatencyRequest latencyRequest, String token) throws IOException {
        Log.d(TAG, "Sending latency data for model: " + latencyRequest.getModelId() + 
              " with execution time: " + latencyRequest.getTotalExecutionTimeMs() + "ms");
        
        HttpURLConnection connection = null;
        OutputStream out = null;
        BufferedOutputStream bout = null;
        InputStream in = null;

        try {
            // Construct the prediction-latency endpoint URL
            String endpointUrl = Constants.DOLPHIN_BASE_URL + "/api/prediction-latency";
            
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
            
            // Prepare the request payload
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("model_id", latencyRequest.getModelId());
                jsonObject.put("session_id", latencyRequest.getSessionId());
                jsonObject.put("client", latencyRequest.getClient());
                jsonObject.put("total_execution_time_ms", latencyRequest.getTotalExecutionTimeMs());
                
                // Convert latency data map to JSON object
                JSONObject latencyDataJson = new JSONObject();
                if (latencyRequest.getLatencyData() != null) {
                    for (Map.Entry<String, Object> entry : latencyRequest.getLatencyData().entrySet()) {
                        latencyDataJson.put(entry.getKey(), entry.getValue());
                    }
                }
                jsonObject.put("latency_data", latencyDataJson);
                
            } catch (org.json.JSONException e) {
                Log.e(TAG, "Failed to create latency request JSON", e);
                return;
            }
            
            // Convert JSON to bytes
            String jsonBody = jsonObject.toString();
            Log.d(TAG, "Latency request body: " + jsonBody);
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
                byte[] responseData = readStream(in);
                String responseBody = new String(responseData, StandardCharsets.UTF_8);
                Log.d(TAG, "Latency data sent successfully: " + responseBody);
            } else {
                // Read error response
                InputStream errorStream = connection.getErrorStream();
                String errorResponse = errorStream != null ? 
                    new String(readStream(errorStream), StandardCharsets.UTF_8) : "No error details";
                Log.e(TAG, "Failed to send latency data. Server returned code " + responseCode + " with error: " + errorResponse);
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to send latency data", e);
            throw e;
        } finally {
            // Cleanup
            try { if (bout != null) bout.close(); } catch (IOException ignored) {}
            try { if (out != null) out.close(); } catch (IOException ignored) {}
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            if (connection != null) connection.disconnect();
        }
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
