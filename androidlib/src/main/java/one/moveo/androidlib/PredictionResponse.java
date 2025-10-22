package one.moveo.androidlib;

/**
 * Response model for prediction API calls.
 * Contains the result and metadata of a prediction request.
 */
public class PredictionResponse {
    private boolean success;
    private String status;
    private String message;
    private Double predictionProbability;
    private Boolean predictionBinary;

    public PredictionResponse() {
    }

    public PredictionResponse(boolean success, String status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }

    public PredictionResponse(boolean success, String status, String message, Double predictionProbability, Boolean predictionBinary) {
        this.success = success;
        this.status = status;
        this.message = message;
        this.predictionProbability = predictionProbability;
        this.predictionBinary = predictionBinary;

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getPredictionProbability() {
        return predictionProbability;
    }

    public void setPredictionProbability(Double predictionProbability) {
        this.predictionProbability = predictionProbability;
    }

    public Boolean getPredictionBinary() {
        return predictionBinary;
    }

    public void setPredictionBinary(Boolean predictionBinary) {
        this.predictionBinary = predictionBinary;
    }

    @Override
    public String toString() {
        return "PredictionResponse{" +
                "success=" + success +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", predictionProbability=" + predictionProbability +
                ", predictionBinary=" + predictionBinary +
                '}';
    }
}
