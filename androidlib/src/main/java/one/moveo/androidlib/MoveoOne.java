package one.moveo.androidlib;

import static one.moveo.androidlib.Constants.MoveoOneEventType.START_SESSION;
import static one.moveo.androidlib.Constants.MoveoOneEventType.TRACK;
import static one.moveo.androidlib.Constants.MoveoOneEventType.UPDATE_METADATA;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;


public class MoveoOne {
    public static final String TAG = "MOVEO_ONE";
    public static final String API_ENDPOINT = "https://api.moveo.one/api/analytic/event";

    private static final MoveoOne instance = new MoveoOne();
    private final List<MoveoOneEntity> buffer = new ArrayList<>();

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String token = "";
    private String userId = "";


    private boolean logging = false;

    private int flushInterval = 10;
    private int maxThreshold = 500;
    private Timer flushTimer = null;
    private boolean started = false;
    private String context = "";
    private String sessionId = "";
    private boolean customPush = false;

    private MoveoOne() {

    }

    public static MoveoOne getInstance() {
        return instance;
    }

    public void initialize(String token) {
        this.token = token;
        log("Initialized");
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public void identify(String userId) {
        this.userId = userId;
    }

    public boolean isCustomFlush() {
        return this.customPush;
    }

    public void start(String context) {
        log("start");
        if (!this.started) {
            this.flushOrRecord(true);
            this.started = true;
            this.context = context;
            this.sessionId = "sid_" + UUID.randomUUID().toString();

            Map<String, String> updatedMetadata = new HashMap<>();
            updatedMetadata.put("lib_version", Constants.libVersion);


            this.addEventToBuffer(
                    context,
                    START_SESSION,
                    new HashMap<>(),
                    this.userId,
                    this.sessionId,
                    updatedMetadata
                    );
            this.flushOrRecord(false);
        }
    }

    public void track(String context, MoveoOneData data) {
        log("track");
        Map<String, String> properties = new HashMap<>();
        properties.put("sg", data.getSemanticGroup());
        properties.put("eID", data.getId());
        properties.put("eA", data.getAction().getValue());
        properties.put("eT", data.getType().getValue());
        if (data.getValue() instanceof String) {
            properties.put("eV", (String) data.getValue());
        } else if (data.getValue() instanceof String[]) {
            String[] array = (String[]) data.getValue();
            properties.put("eV", String.join(",", array));
        } else if (data.getValue() instanceof Integer) {
            properties.put("eV", Integer.toString((Integer) data.getValue()));
        } else if (data.getValue() instanceof Double) {
            properties.put("eV", Double.toString((Double) data.getValue()));
        } else if (data.getValue() instanceof Float) {
            properties.put("eV", Float.toString((Float) data.getValue()));
        } else if (data.getValue() instanceof List<?>) {
            List<?> list = (List<?>) data.getValue();
            if (!list.isEmpty() && list.get(0) instanceof String) {
                List<String> stringList = (List<String>) list;
                String[] array = stringList.toArray(new String[0]);
                properties.put("eV", String.join(",", array));
            }
        } else {
            properties.put("eV", "-");
        }
        trackInternal(context, properties, data.getMetadata() == null ? new HashMap<>() : data.getMetadata());
    }

    public void tick(MoveoOneData data) {
        log("tick");
        Map<String, String> properties = new HashMap<>();
        properties.put("sg", data.getSemanticGroup());
        properties.put("eID", data.getId());
        properties.put("eA", data.getAction().getValue());
        properties.put("eT", data.getType().getValue());
        if (data.getValue() instanceof String) {
            properties.put("eV", (String) data.getValue());
        } else if (data.getValue() instanceof String[]) {
            String[] array = (String[]) data.getValue();
            properties.put("eV", String.join(",", array));
        } else if (data.getValue() instanceof Integer) {
            properties.put("eV", Integer.toString((Integer) data.getValue()));
        } else if (data.getValue() instanceof Double) {
            properties.put("eV", Double.toString((Double) data.getValue()));
        } else if (data.getValue() instanceof Float) {
            properties.put("eV", Float.toString((Float) data.getValue()));
        } else if (data.getValue() instanceof List<?>) {
            List<?> list = (List<?>) data.getValue();
            if (!list.isEmpty() && list.get(0) instanceof String) {
                List<String> stringList = (List<String>) list;
                String[] array = stringList.toArray(new String[0]);
                properties.put("eV", String.join(",", array));
            }
        } else {
            properties.put("eV", "-");
        }
        tickInternal(properties, data.getMetadata() == null ? new HashMap<>() : data.getMetadata());
    }

    public void updateSessionMetadata(Map<String, String> metadata) {
        log("update session metadata");
        if (this.started) {
            this.addEventToBuffer(
                    this.context,
                    UPDATE_METADATA,
                    new HashMap<>(),
                    this.userId,
                    this.sessionId,
                    metadata
            );
            this.flushOrRecord(false);
        }
    }

    private void flushOrRecord(boolean isStopOrStart) {
        if (!customPush) {
            if (buffer.size() >= maxThreshold || isStopOrStart) {
                flush();
            } else if (flushTimer == null) {
                setFlushTimeout();
            }
        }
    }

    private void trackInternal(String context, Map<String, String> properties, Map<String, String> metadata) {
        if (!this.started) {
            this.start(context);
        }
        this.addEventToBuffer(
                context,
                TRACK,
                properties,
                this.userId,
                this.sessionId,
                metadata
        );
        this.flushOrRecord(false);
    }

    private void tickInternal(Map<String, String> properties, Map<String, String> metadata) {
        if ("".equals(this.context)) {
            this.start("default_ctx");
        }
        this.addEventToBuffer(
                this.context,
                TRACK,
                properties,
                this.userId,
                this.sessionId,
                metadata
        );
        this.flushOrRecord(false);
    }

    private void addEventToBuffer(
            String context,
            Constants.MoveoOneEventType type,
            Map<String, String> prop,
            String userId,
            String sessionId,
            Map<String, String> meta
    ) {
        long now = System.currentTimeMillis();
        this.buffer.add(new MoveoOneEntity(
                context,
                type.getValue(),
                userId,
                now,
                prop,
                meta,
                sessionId
        ));
    }

    private void flush() {
        if (!customPush && !buffer.isEmpty()) {
            log("flushing");

            this.clearFlushTimeout();
            List<MoveoOneEntity> dataToSend = new ArrayList<>();
            for (MoveoOneEntity entity : buffer) {
                dataToSend.add(new MoveoOneEntity(
                        entity.getC(),
                        entity.getType(),
                        entity.getUserId(),
                        entity.getT(),
                        entity.getProp(),
                        entity.getMeta(),
                        entity.getSId()
                ));

            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                CompletableFuture.runAsync(() -> {
                    try {
                        Util.performPostRequest(API_ENDPOINT, dataToSend, this.token);
                    } catch (IOException e) {
                        log("Error Future:" + e);
                    }
                });
            } else {
                executor.execute(() -> {
                    try {
                        Util.performPostRequest(API_ENDPOINT, dataToSend, this.token);
                    } catch (IOException e) {
                        log("Error Executor:" + e);
                    }
                });
            }

            buffer.clear();
            log("Flushing data...");
        }
    }

    private void setFlushTimeout() {
        log("setting flush timeout");
        flushTimer = new Timer();
        flushTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                flush();
            }
        }, flushInterval * 1000);
    }

    private void clearFlushTimeout() {
        if (flushTimer != null) {
            flushTimer.cancel();
            flushTimer.purge();
            flushTimer = null;
        }
    }

    private void log(String message) {
        if (logging) {
            Log.i(TAG, message);
        }
    }
}
