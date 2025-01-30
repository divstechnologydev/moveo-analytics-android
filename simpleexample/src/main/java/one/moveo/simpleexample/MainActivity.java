package one.moveo.simpleexample;

import static one.moveo.androidlib.Constants.MoveoOneAction.APPEAR;
import static one.moveo.androidlib.Constants.MoveoOneAction.CLICK;
import static one.moveo.androidlib.Constants.MoveoOneType.BUTTON;
import static one.moveo.androidlib.Constants.MoveoOneType.TEXT;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import one.moveo.androidlib.Constants;
import one.moveo.androidlib.MoveoOne;
import one.moveo.androidlib.MoveoOneData;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView text1;
    private Button button1;
    private Button button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Initialize views
        text1 = findViewById(R.id.text_1);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners
        button1.setOnClickListener(v -> {
            MoveoOne.getInstance().tick(
                new MoveoOneData(
                    "main_activity_semantic",
                    "button_1",
                    BUTTON,
                    CLICK,
                    button1.getText().toString(),
                    null
                )
            );
            Log.d(TAG, "Button 1 clicked");
        });

        button2.setOnClickListener(v -> {
            MoveoOne.getInstance().tick(
                new MoveoOneData(
                    "main_activity_semantic",
                    "button_2",
                    BUTTON,
                    CLICK,
                    button2.getText().toString(),
                    null
                )
            );
            Log.d(TAG, "Button 2 clicked");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        // Log the content of text1 and button states
        String text1text = text1 != null ? text1.getText().toString() : "null";
        String button1text = button1 != null ? button1.getText().toString() : "null";
        String button2text = button2 != null ? button2.getText().toString() : "null";
        Log.d(TAG, "Text1 content: " + text1text);
        Log.d(TAG, "Button1 text: " + button1text);
        Log.d(TAG, "Button2 state: " + button2text);

        // Log analytics for resume event
        MoveoOne.getInstance().tick(
            new MoveoOneData(
                "main_activity_semantic",
                "text_1",
                TEXT,
                APPEAR,
                text1text,
                null
            )
        );

        MoveoOne.getInstance().tick(
                new MoveoOneData(
                        "main_activity_semantic",
                        "button_1",
                        BUTTON,
                        APPEAR,
                        button1text,
                        null
                )
        );


        MoveoOne.getInstance().tick(
                new MoveoOneData(
                        "main_activity_semantic",
                        "button_2",
                        BUTTON,
                        APPEAR,
                        button2text,
                        null
                )
        );
    }
}