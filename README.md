# moveo-analytics-android

## Table of Contents
- [Introduction](#introduction)
- [Quick Start Guide](#quick-start-guide)
  - [Initialize](#initialize)
  - [Setup](#setup)
  - [Track Data](#track-data)
  - [Obtain API KEY](#obtain-api-key)
  - [Use Results](#use-results)

## Introduction

Welcome to the official Moveo One Android library.

Moveo One analytics is a user cognitive-behavioral analytics tool. moveo-analytics-android is an SDK for Android client apps to use Moveo One tools.

## Quick Start Guide

Moveo One Android SDK is a pure Java/Kotlin implementation of Moveo One Analytics tracker.

### Initialize

Initialization is usually done in your Application class or main Activity. To obtain a token, please contact us at: info@moveo.one and request an API token. We are working on bringing token creation to our dashboard, but for now, due to the early phase, contact us and we will be more than happy to provide you with an API token.

```java
import one.moveo.androidlib.MoveoOne;

MoveoOne.getInstance().initialize(token);
MoveoOne.getInstance().identify(userId);
```

The `userId` is your tracking unique ID of the user who is using the app in order to create personalized analytics. It is used on Dashboard and WebHook to deliver calculated results, so you will need to have the notion of how this user Id correlates with your unique real userID.

Note: Do not provide user-identifiable information to Moveo One - we do not store them, but nonetheless, we don't need that data, so it's better to create custom bindings.

### Track Data

#### a) Semantic Groups
Semantic groups are one level of abstraction. A semantic group is usually a screen - something that is semantically atomic from the user perspective. In our example app, we use "main_activity_semantic" as the semantic group for the main screen.

#### b) Component Types
The library supports different component types that can be tracked:
- `TEXT` - For text components
- `BUTTON` - For clickable buttons
- Other types as needed

#### c) Actions
Available actions for tracking:
- `CLICK` - User interactions like button clicks
- `APPEAR` - Component appearances on screen
- `VIEW` - Content viewing events

#### d) Example Implementation

Here's how to track different components:

```java
// Track button clicks
button1.setOnClickListener(v -> {
    MoveoOne.getInstance().tick(
        new MoveoOneData(
            "main_activity_semantic",  // semantic group
            "button_1",               // component id
            BUTTON,                   // component type
            CLICK,                    // action
            button1.getText().toString(), // value
            null                      // metadata
        )
    );
});

// Track component appearances
@Override
protected void onResume() {
    super.onResume();
    
    MoveoOne.getInstance().tick(
        new MoveoOneData(
            "main_activity_semantic",
            "text_1",
            TEXT,
            APPEAR,
            text1.getText().toString(),
            null
        )
    );
}
```

#### e) MoveoOneData Structure
The MoveoOneData class is used to send analytics data:
```java
MoveoOneData(
    String semanticGroup,  // Screen or context identifier
    String id,            // Component identifier
    MoveoOneType type,    // Component type (BUTTON, TEXT, etc.)
    MoveoOneAction action, // Action type (CLICK, APPEAR, etc.)
    String value,         // Component value
    Map<String, String> metadata  // Optional additional data
)
```

### Obtain API KEY

To obtain an API key, please contact us at info@moveo.one. We will provide you with the necessary credentials to start using the analytics service.

### Use Results

#### Data Ingestion
The MoveoOne library automatically handles data ingestion by sending analytics events to our servers when you call the `tick()` method.

#### Dashboard
Once your data is being collected, you can view and analyze it through our dashboard. Contact us for dashboard access and detailed analytics of your users' behavior.
```

