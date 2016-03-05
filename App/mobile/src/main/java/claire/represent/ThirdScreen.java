package claire.represent;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;

/**
 * Created by clairelee on 3/1/16.
 */

public class ThirdScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed);

        Intent activityThatCalled = getIntent();

    }

    public void onBackButton(View view) {
        Intent getThirdScreenIntent = new Intent();
        setResult(RESULT_OK, getThirdScreenIntent);
        finish();
    }


}
