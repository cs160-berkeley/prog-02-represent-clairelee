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

public class SecondScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congressional_view);

        Intent activityThatCalled = getIntent();

//        String zip = activityThatCalled.getExtras().getString("zipCode");
//
//        TextView callingActivityMessage = (TextView)
//                findViewById(R.id.city_text_view);

        //callingActivityMessage.append(" " + zip);
    }

    public void onSeeMore(View view) {
        Intent getSecondScreenIntent = new Intent(this,
                ThirdScreen.class);

        startActivity(getSecondScreenIntent);
    }


}
