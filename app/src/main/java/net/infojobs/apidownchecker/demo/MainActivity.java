package net.infojobs.apidownchecker.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.infojobs.apidownchecker.ApiDownException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private TextView status;
    private MyAPI myAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = ((TextView) findViewById(R.id.text_status));
        RadioGroup serverOptions = (RadioGroup) findViewById(R.id.radio_group_server);
        final View workingRequestButton = findViewById(R.id.button_request_working);
        workingRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWorking();
            }
        });
        findViewById(R.id.button_request_failing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBroken();
            }
        });
        serverOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                boolean isApiDown = checkedId == R.id.radio_down;
                Injections.getDemoInterceptor().forceApiDown(isApiDown);
                status.setText("");
                workingRequestButton.setEnabled(!isApiDown);
            }
        });
        myAPI = Injections.getMyApi();
    }

    private void requestWorking() {
        status.setText("");
        myAPI.get200(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                status.setText("It worked!");
            }

            @Override
            public void failure(RetrofitError error) {
                status.setText("Failure: " + error.getMessage());
            }
        });
    }

    private void requestBroken() {
        status.setText("");
        myAPI.get503(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                status.setText("It worked :S");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof ApiDownException) {
                    status.setText("\uD83D\uDD25\uD83D\uDD25\uD83D\uDD25 API DOWN!! \uD83D\uDD25\uD83D\uDD25\uD83D\uDD25");
                } else {
                    status.setText("Failure: " + error.getMessage());
                }
            }
        });
    }


}
