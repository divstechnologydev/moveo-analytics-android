# Moveo One Analytics for Android

![Moveo One Logo](https://www.moveo.one/assets/og_white.png)

## Table of Contents
- [Introduction](#introduction)
- [Quick Start Guide](#quick-start-guide)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Library Initialization](#library-initialization)
  - [Setup](#setup)
  - [Metadata and Additional Metadata](#metadata-and-additional-metadata)
  - [Track Data](#track-data)
- [Event Types and Actions](#event-types-and-actions)
- [Comprehensive Example Usage](#comprehensive-example-usage)
- [Obtain API Key](#obtain-api-key)
- [Dashboard Access](#dashboard-access)
- [Support](#support)

## Introduction

Moveo One Analytics is a user cognitive-behavioral analytics tool designed to provide deep insights into user behavior and interaction patterns. The moveo-analytics-android SDK enables Android applications to leverage Moveo One's advanced analytics capabilities with a lightweight, non-intrusive integration.

**Current version:** 1.0.12

### Key Features
- User interaction tracking
- Semantic grouping of user actions
- Component-level analytics
- Non-intrusive integration
- Privacy-focused design
- Automatic data batching and transmission

## Quick Start Guide

### Prerequisites
- Android API level 21 or higher
- Internet connectivity for data transmission
- Moveo One API key (obtain from [app.moveo.one](https://app.moveo.one/))

### Installation

Add the JitPack repository to your project-level `build.gradle`:

```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

Add the dependency to your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.divstechnologydev:moveo-analytics-android:v1.0.12'
}
```

### Library Initialization

Initialize the library early in your application lifecycle, typically in your Application class:

```java
import one.moveo.androidlib.MoveoOne;

public class YourApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize with your API token
        MoveoOne.getInstance().initialize("YOUR_API_KEY");
        
        // Start tracking session (must be called before any track/tick events)
        MoveoOne.getInstance().start("your_context_name");
    }
}
```



### Setup

Configure optional parameters for your tracking needs:

```java
// Enable debug logging (optional)
MoveoOne.getInstance().setLogging(true);

// Set custom flush interval in seconds (default: 10 seconds)
MoveoOne.getInstance().setFlushInterval(20);
```

### Metadata and Additional Metadata

The library supports metadata in both `track()` and `tick()` events, plus session-level metadata management.

#### Metadata in Track and Tick Events

```java
Map<String, String> metadata = new HashMap<>();
sessionMetadata.put("test", "a");
sessionMetadata.put("locale", "eng");
sessionMetadata.put("app_version", "2.1.0");

// Using metadata with track()
MoveoOne.getInstance().track(
    "checkout_screen",
    new MoveoOneData(
        "user_interactions",
        "checkout_button",
        Constants.MoveoOneType.BUTTON,
        Constants.MoveoOneAction.CLICK,
        "proceed_to_payment",
        metadata
    )
);

// Using metadata with tick()
MoveoOne.getInstance().tick(
    new MoveoOneData(
        "content_interactions",
        "product_card",
        Constants.MoveoOneType.CARD,
        Constants.MoveoOneAction.APPEAR,
        "product_view",
        metadata
    )
);
```

#### Session Metadata Management

**updateSessionMetadata()** - Updates current session metadata. Session metadata should split sessions by information that influences content or creates visually different variations of the same application. Sessions split by these parameters will be analyzed separately by our UX analyzer.

```java
Map<String, String> sessionMetadata = new HashMap<>();
sessionMetadata.put("test", "a");
sessionMetadata.put("locale", "eng");
sessionMetadata.put("app_version", "2.1.0");

MoveoOne.getInstance().updateSessionMetadata(sessionMetadata);
```

**updateAdditionalMetadata()** - Updates additional metadata for the session. This is used as data enrichment and enables specific queries or analysis by the defined split.

```java
Map<String, String> additionalMetadata = new HashMap<>();
additionalMetadata.put("user_country", "US");
additionalMetadata.put("company", "example_company");
additionalMetadata.put("user_role", "admin"); // or "user", "manager", "viewer"
additionalMetadata.put("acquisition_channel", "organic"); // or "paid", "referral", "direct"
additionalMetadata.put("device_category", "mobile"); // or "desktop", "tablet"
additionalMetadata.put("subscription_plan", "pro"); // or "basic", "enterprise"
additionalMetadata.put("has_purchased", "true"); // or "false"

MoveoOne.getInstance().updateAdditionalMetadata(additionalMetadata);
```

### Track Data

#### Understanding Context and Session Management

**Single Session, Single Start**
- Call `start()` only **once at the beginning of a session**
- Must be called before any `track()` or `tick()` calls
- You do **not** need multiple `start()` calls for multiple contexts

#### When to Use Each Tracking Method

**Use `track()` when:**
- You want to explicitly specify the event context
- You need to change context between events
- You want to use a different context than the one specified in the start method

**Use `tick()` when:**
- You're tracking events within the same context
- You want tracking without explicitly defining context
- You want to track events in the same context specified in the start method

#### Context and Semantic Groups

**Context** represents large, independent parts of the application and serves to divide the app into functional units that can exist independently of each other.

Examples: `onboarding`, `main_app_flow`, `checkout_process`

**Semantic Groups** are logical units within a context that group related elements. Depending on the application, this could be a group of elements or an entire screen (most common).

Examples: `navigation`, `user_input`, `content_interaction`

#### Tracking Examples

```java
// Track button click with explicit context
MoveoOne.getInstance().track(
    "checkout_screen",
    new MoveoOneData(
        "user_interactions",
        "submit_order_button",
        Constants.MoveoOneType.BUTTON,
        Constants.MoveoOneAction.CLICK,
        "Submit Order",
        null
    )
);

// Track component appearance in current context
MoveoOne.getInstance().tick(
    new MoveoOneData(
        "content_interactions",
        "product_list",
        Constants.MoveoOneType.COLLECTION,
        Constants.MoveoOneAction.APPEAR,
        "Product Catalog",
        null
    )
);

// Track text input with metadata
Map<String, String> sessionMetadata = new HashMap<>();
sessionMetadata.put("test", "a");
sessionMetadata.put("locale", "eng");
sessionMetadata.put("app_version", "2.1.0");

MoveoOne.getInstance().tick(
    new MoveoOneData(
        "user_input",
        "email_field",
        Constants.MoveoOneType.TEXT_EDIT,
        Constants.MoveoOneAction.INPUT,
        "user@example.com",
        sessionMetadata
    )
);
```

## Event Types and Actions

### Event Types

The library supports comprehensive UI component types:

**Basic Components:**
- `BUTTON` - Clickable buttons, action triggers
- `TEXT` - Text displays, labels, descriptions
- `TEXT_EDIT` - Text input fields
- `IMAGE` - Single images
- `IMAGES` - Multiple images
- `IMAGE_SCROLL_HORIZONTAL` - Horizontally scrollable images
- `IMAGE_SCROLL_VERTICAL` - Vertically scrollable images

**Interactive Controls:**
- `PICKER` - Selection pickers
- `SLIDER` - Slider controls
- `SWITCH_CONTROL` - Toggle switches
- `PROGRESS_BAR` - Progress indicators
- `CHECKBOX` - Checkbox controls
- `RADIO_BUTTON` - Radio button groups
- `SEGMENTED_CONTROL` - Segmented controls
- `STEPPER` - Stepper controls
- `DATE_PICKER` - Date selection
- `TIME_PICKER` - Time selection
- `SEARCH_BAR` - Search input fields

**Layout Components:**
- `TABLE` - Data tables
- `COLLECTION` - Collections of items
- `SCROLL_VIEW` - Scrollable views
- `VIEW` - Generic views
- `GRID` - Grid layouts
- `CARD` - Card components
- `CHIP` - Chip components

**Media Components:**
- `VIDEO` - Video content
- `VIDEO_PLAYER` - Video players
- `AUDIO_PLAYER` - Audio players
- `MAP` - Map components

**Navigation Components:**
- `TAB_BAR` - Tab bars
- `TAB_BAR_PAGE` - Tab bar pages
- `TAB_BAR_PAGE_TITLE` - Tab page titles
- `TAB_BAR_PAGE_SUBTITLE` - Tab page subtitles
- `TOOLBAR` - Toolbars

**Feedback Components:**
- `ALERT` - Alert dialogs
- `ALERT_TITLE` - Alert titles
- `ALERT_SUBTITLE` - Alert subtitles
- `MODAL` - Modal dialogs
- `TOAST` - Toast messages
- `BADGE` - Badge indicators
- `DROPDOWN` - Dropdown menus
- `ACTIVITY_INDICATOR` - Loading indicators

**Custom:**
- `CUSTOM` - Custom component types

### Event Actions

**User Interactions:**
- `CLICK` - User taps or clicks
- `TAP` - Single tap
- `DOUBLE_TAP` - Double tap
- `LONG_PRESS` - Long press gesture
- `SWIPE` - Swipe gesture
- `DRAG` - Drag gesture
- `DROP` - Drop action
- `PINCH` - Pinch gesture
- `ZOOM` - Zoom gesture
- `ROTATE` - Rotate gesture

**Visibility Events:**
- `APPEAR` - Component becomes visible
- `DISAPPEAR` - Component becomes hidden
- `VIEW` - Content viewing events

**Input Events:**
- `INPUT` - Text input events
- `VALUE_CHANGE` - Value changes
- `FOCUS` - Component gains focus
- `BLUR` - Component loses focus
- `SELECT` - Selection made
- `DESELECT` - Selection removed

**Navigation Events:**
- `SCROLL` - Scrolling events
- `DRAG_START` - Drag begins
- `DRAG_END` - Drag ends
- `OPEN` - Component opens
- `CLOSE` - Component closes
- `EXPAND` - Component expands
- `COLLAPSE` - Component collapses

**Form Events:**
- `SUBMIT` - Form submission
- `EDIT` - Editing begins
- `CANCEL` - Action cancelled
- `RETRY` - Retry action

**Media Events:**
- `PLAY` - Media starts playing
- `PAUSE` - Media pauses
- `STOP` - Media stops
- `SEEK` - Media seeking
- `LOAD` - Content loads
- `UNLOAD` - Content unloads
- `REFRESH` - Content refreshes

**Status Events:**
- `SUCCESS` - Successful action
- `ERROR` - Error occurs
- `SHARE` - Content sharing
- `HOVER` - Hover over component

**Custom:**
- `CUSTOM` - Custom actions

## Comprehensive Example Usage

Here's a complete example showing different tracking scenarios in an e-commerce app:

```java
public class MainActivity extends AppCompatActivity {
    private static final String SEMANTIC_GROUP = "main_activity_semantic";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize Moveo One (if not done in Application class)
        MoveoOne.getInstance().initialize("YOUR_API_KEY");
        MoveoOne.getInstance().start("main_app_flow");
        
        setupTracking();
    }
    
    private void setupTracking() {
        // Track button interactions
        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        addToCartButton.setOnClickListener(v -> {
            
            MoveoOne.getInstance().tick(
                new MoveoOneData(
                    SEMANTIC_GROUP,
                    "add_to_cart_button",
                    Constants.MoveoOneType.BUTTON,
                    Constants.MoveoOneAction.CLICK,
                    "Add to Cart",
                    null
                )
            );
        });

        // Track search input
        EditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                
                MoveoOne.getInstance().tick(
                    new MoveoOneData(
                        SEMANTIC_GROUP,
                        "search_input",
                        Constants.MoveoOneType.SEARCH_BAR,
                        Constants.MoveoOneAction.INPUT,
                        s.toString(),
                        null
                    )
                );
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        
        // Track product list scrolling
        RecyclerView productList = findViewById(R.id.product_list);
        productList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (Math.abs(dy) > 10) { // Only track significant scrolls
                    MoveoOne.getInstance().tick(
                        new MoveoOneData(
                            SEMANTIC_GROUP,
                            "product_list",
                            Constants.MoveoOneType.COLLECTION,
                            Constants.MoveoOneAction.SCROLL,
                            "vertical_scroll",
                        null
                    )
                );
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Track UI component appearances
        trackComponentAppearance("welcome_text", Constants.MoveoOneType.TEXT, "Welcome to our store");
        trackComponentAppearance("product_grid", Constants.MoveoOneType.GRID, "Product Catalog");
        trackComponentAppearance("search_bar", Constants.MoveoOneType.SEARCH_BAR, "Search products");
    }

    private void trackComponentAppearance(String id, Constants.MoveoOneType type, String value) {
        MoveoOne.getInstance().tick(
            new MoveoOneData(
                SEMANTIC_GROUP,
                id,
                type,
                Constants.MoveoOneAction.APPEAR,
                value,
                null
            )
        );
    }
    
    // Example of switching context for checkout flow
    private void startCheckoutFlow() {
        // Switch to checkout context
        MoveoOne.getInstance().track(
            "checkout_flow",
    new MoveoOneData(
                "checkout_initiation",
                "checkout_button",
                Constants.MoveoOneType.BUTTON,
                Constants.MoveoOneAction.CLICK,
                "Proceed to Checkout",
                null
            )
        );
        
    }
}
```

## Obtain API Key

To obtain an API key for Moveo One Analytics:

1. Visit [app.moveo.one](https://app.moveo.one/)
2. Create an account or sign in
3. Navigate to your organization settings
4. Generate a new API key
5. Use this key in your `initialize()` method

## Dashboard Access

Once your data is being tracked, you can access your analytics through the Moveo One Dashboard at [https://app.moveo.one/](https://app.moveo.one/)

The dashboard provides:
- Analytics viewing
- User behavior patterns
- Interaction flow visualization
- Custom report generation
- Data export capabilities
- Session replay and analysis

## Support

For any issues or support, feel free to:

- Open an **issue** on our [GitHub repository](https://github.com/divstechnologydev/moveoone-flutter/issues)
- Email us at [**info@moveo.one**](mailto:info@moveo.one)

We're here to help you get the most out of Moveo One Analytics!

