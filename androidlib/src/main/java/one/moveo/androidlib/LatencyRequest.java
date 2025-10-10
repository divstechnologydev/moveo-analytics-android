package one.moveo.androidlib;

import java.util.Map;

/**
 * Request model for prediction latency tracking.
 * Contains latency metrics and execution information for monitoring.
 */
public class LatencyRequest {
    private String modelId;
    private String sessionId;
    private String client;
    private int totalExecutionTimeMs;
    private Map<String, Object> latencyData;

    public LatencyRequest() {
    }

    public LatencyRequest(String modelId, String sessionId, String client, int totalExecutionTimeMs, Map<String, Object> latencyData) {
        this.modelId = modelId;
        this.sessionId = sessionId;
        this.client = client;
        this.totalExecutionTimeMs = totalExecutionTimeMs;
        this.latencyData = latencyData;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public int getTotalExecutionTimeMs() {
        return totalExecutionTimeMs;
    }

    public void setTotalExecutionTimeMs(int totalExecutionTimeMs) {
        this.totalExecutionTimeMs = totalExecutionTimeMs;
    }

    public Map<String, Object> getLatencyData() {
        return latencyData;
    }

    public void setLatencyData(Map<String, Object> latencyData) {
        this.latencyData = latencyData;
    }

    @Override
    public String toString() {
        return "LatencyRequest{" +
                "modelId='" + modelId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", client='" + client + '\'' +
                ", totalExecutionTimeMs=" + totalExecutionTimeMs +
                ", latencyData=" + latencyData +
                '}';
    }
}
