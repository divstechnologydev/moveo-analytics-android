package one.moveo.androidlib;

import static one.moveo.androidlib.Constants.MoveoOneEventType.START_SESSION;
import static one.moveo.androidlib.Constants.MoveoOneEventType.TRACK;
import static one.moveo.androidlib.Constants.MoveoOneEventType.UPDATE_METADATA;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MoveoOne {
    public static final String TAG = "MOVEO_ONE";
    private static final MoveoOne instance = new MoveoOne();
    private final List<MoveoOneEntity> buffer = new ArrayList<>();
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
    }

    public void identify(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return this.token;
    }

    public void setLogging(boolean enabled) {
        this.logging = enabled;
    }

    public void setFlushInterval(int interval) {
        this.flushInterval = interval;
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
            this.addEventToBuffer(
                    context,
                    START_SESSION,
                    new HashMap<>(),
                    this.userId,
                    this.sessionId,
                    new HashMap<>()
                    );
            this.flushOrRecord(false);
        }
    }

//    og(msg: "track")
//        var properties: [String: String] = [:]
//        properties["sg"] = moveoOneData.semanticGroup
//        properties["eID"] = moveoOneData.id
//        properties["eA"] = moveoOneData.action.rawValue
//        properties["eT"] = moveoOneData.type.rawValue
//        if let stringValue = moveoOneData.value as? String {
//            properties["eV"] = stringValue
//        } else if let stringArray = moveoOneData.value as? [String] {
//            properties["eV"] = stringArray.joined(separator: ",")
//        } else if let intValue = moveoOneData.value as? Int {
//            properties["eV"] = String(intValue)
//        } else if let doubleValue = moveoOneData.value as? Double {
//            properties["eV"] = String(doubleValue)
//        } else {
//            properties["eV"] = "-"
//        }
//
//        track(context: context, properties: properties, metadata: moveoOneData.metadata ?? [:])
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
            log("flush");
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

    private void log(String message) {
        if (logging) {
            Log.i(TAG, message);
        }
    }
}
