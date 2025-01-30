package one.moveo.simpleexample;

import static one.moveo.androidlib.Constants.MoveoOneType.BUTTON;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import one.moveo.androidlib.Constants;
import one.moveo.androidlib.MoveoOne;
import one.moveo.androidlib.MoveoOneData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MoveoOne moveoOne = MoveoOne.getInstance();
        moveoOne.start("context");
        moveoOne.setLogging(true);
        moveoOne.track("context", new MoveoOneData("sg", "id", BUTTON, Constants.MoveoOneAction.CLICK, "test", null));
    }
}