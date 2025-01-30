# moveo-analytics-android

## Table of Contents
- [Introduction](#introduction)
- [Quick Start Guide](#quick-start-guide)
  - [Initialize](#initialize)
  - [Track Data](#track-data)
  - [Advanced Usage](#advanced-usage)
  - [Obtain API KEY](#obtain-api-key)
  - [Use Results](#use-results)

## Introduction

Welcome to the official Moveo One Android library.

Moveo One analytics is a user cognitive-behavioral analytics tool designed to provide deep insights into user behavior and interaction patterns. The moveo-analytics-android SDK enables Android applications to leverage Moveo One's advanced analytics capabilities.

### Key Features
- Real-time user interaction tracking
- Semantic grouping of user actions
- Component-level analytics
- Non-intrusive integration
- Privacy-focused design

## Quick Start Guide

Moveo One Android SDK is a pure Java/Kotlin implementation of Moveo One Analytics tracker, designed to be lightweight and easy to integrate.

### Initialize

Initialization should be done early in your application lifecycle, typically in your Application class or main Activity.

```java
import one.moveo.androidlib.MoveoOne;

public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MoveoOne.getInstance().initialize(token);
        MoveoOne.getInstance().identify(userId);
    }
}
```

The `userId` parameter is your tracking unique identifier for the user. This ID is used to:
- Track individual user behavior patterns
- Link analytics data in the Dashboard
- Correlate data in WebHook deliveries

**Important Privacy Note**: Never use personally identifiable information (PII) as the userId. Instead:
- Use application-specific unique identifiers
- Consider using hashed or encoded values
- Maintain a separate mapping of analytics IDs to user data in your system

### Track Data

#### a) Semantic Groups
Semantic groups provide context for your analytics data. They typically represent:
- Screens or views
- Functional areas
- User flow segments

Example semantic group usage:
```java
"main_activity_semantic"    // Main screen
"checkout_semantic"         // Checkout flow
"profile_semantic"         // User profile area
```

#### b) Component Types
The library supports various UI component types:
- `TEXT` - Text displays, labels, descriptions
- `BUTTON` - Clickable buttons, action triggers
- `INPUT` - Text input fields
- `LIST` - Scrollable lists or grids
- `MODAL` - Popup dialogs or modals
- Custom types as needed

#### c) Actions
Available tracking actions:
- `CLICK` - User taps or clicks
- `APPEAR` - Component becomes visible
- `VIEW` - Content viewing events
- `SCROLL` - List scrolling events
- `INPUT` - Text input events
- `FOCUS` - Component focus events

#### d) Comprehensive Example

Here's a complete example showing different tracking scenarios:

```java
public class MainActivity extends AppCompatActivity {
    private static final String SEMANTIC_GROUP = "main_activity_semantic";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Track button interactions
        button1.setOnClickListener(v -> {
            MoveoOne.getInstance().tick(
                new MoveoOneData(
                    SEMANTIC_GROUP,
                    "button_1",
                    BUTTON,
                    CLICK,
                    button1.getText().toString(),
                    null
                )
            );
        });

        // Track text input
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                MoveoOne.getInstance().tick(
                    new MoveoOneData(
                        SEMANTIC_GROUP,
                        "input_field",
                        INPUT,
                        INPUT,
                        "text_changed",
                        null
                    )
                );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Track UI component appearances
        trackComponentAppearance("text_1", TEXT, text1.getText().toString());
        trackComponentAppearance("button_1", BUTTON, button1.getText().toString());
        trackComponentAppearance("button_2", BUTTON, button2.getText().toString());
    }

    private void trackComponentAppearance(String id, MoveoOneType type, String value) {
        MoveoOne.getInstance().tick(
            new MoveoOneData(
                SEMANTIC_GROUP,
                id,
                type,
                APPEAR,
                value,
                null
            )
        );
    }
}
```

#### e) MoveoOneData Structure
The MoveoOneData class encapsulates all tracking information:
```java
MoveoOneData(
    String semanticGroup,  // Context identifier (required)
    String id,            // Component identifier (required)
    MoveoOneType type,    // Component type (required)
    MoveoOneAction action, // Action type (required)
    String value,         // Component value (optional)
    Map<String, String> metadata  // Additional data (optional)
)
```

### Advanced Usage

#### Metadata
The metadata parameter allows you to include additional context:
```java
Map<String, String> metadata = new HashMap<>();
metadata.put("screen_orientation", "portrait");
metadata.put("network_status", "wifi");

MoveoOne.getInstance().tick(
    new MoveoOneData(
        SEMANTIC_GROUP,
        "button_1",
        BUTTON,
        CLICK,
        "submit",
        metadata
    )
);
```

### Obtain API KEY

To obtain an API key:
1. Contact us at info@moveo.one
2. Provide your application details
3. We'll provide you with a unique API token
4. Integration support is available upon request

### Use Results

#### Data Ingestion
The MoveoOne library handles:
- Automatic data collection
- Efficient event batching
- Reliable data transmission
- Offline data queuing

#### Dashboard Access
The Moveo One Dashboard provides:
- Real-time analytics viewing
- User behavior patterns
- Interaction flow visualization
- Custom report generation
- Data export capabilities


Contact us for dashboard access and detailed documentation on interpreting your analytics data.

