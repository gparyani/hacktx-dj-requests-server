package cs371m.godj;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements FirebaseCreateNewAccountFragment.FirebaseCreateAccountInterface {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page_layout);

        Button userLogin = (Button) findViewById(R.id.user_signin);
        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startUserActivity = new Intent(getApplicationContext(), UserMainActivity.class);
                startActivity(startUserActivity);
            }
        });
    }

    @Override
    public void firebaseLoginFinish() {

    }
}