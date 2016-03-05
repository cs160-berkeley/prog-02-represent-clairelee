package claire.represent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSendUsersZip(View view) {

        EditText zipText = (EditText) findViewById(R.id.users_zip_edit_text);
        String zipCode = String.valueOf(zipText.getText());

        Intent getMainScreenIntent = new Intent(this,
                SecondScreen.class);

        getMainScreenIntent.putExtra("zipCode", zipCode);
        startActivity(getMainScreenIntent);
        //startActivityForResult(getMainScreenIntent, result);

        Intent watchIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
        watchIntent.putExtra("zipCode", zipCode);
        startService(watchIntent);

    }


}
