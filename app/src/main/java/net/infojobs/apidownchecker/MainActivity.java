package net.infojobs.apidownchecker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.retrofit.Ok3Client;

import net.infojobs.ApiDownChecker;
import net.infojobs.ApiDownException;
import net.infojobs.ApiDownInterceptor;

import okhttp3.OkHttpClient;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    private RadioGroup requestOptions;
    private RadioGroup serverOptions;
    private TextView status;
    private HttpstatApi httpstatApi;
    private UnreachableApi unreachableApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        status = ((TextView) findViewById(R.id.text_status));
        serverOptions = (RadioGroup) findViewById(R.id.radio_group_server);
        requestOptions = (RadioGroup) findViewById(R.id.radio_group_request);
        findViewById(R.id.button_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        setupApis();
    }

    private void setupApis() {
        httpstatApi = new RestAdapter.Builder()
          .setClient(getClient())
          .setEndpoint("http://httpstat.us/")
          .build().create(HttpstatApi.class);

        unreachableApi = new RestAdapter.Builder()
          .setClient(getClient())
          .setEndpoint("http://unknown.address/")
          .build().create(UnreachableApi.class);
    }

    @NonNull
    private Ok3Client getClient() {
        boolean isApiDown = serverOptions.getCheckedRadioButtonId() == R.id.radio_down;

        ApiDownChecker checker = new ApiDownChecker.Builder()
          .check(isApiDown ? "http://httpstat.us/503" : "http://httpstat.us/200")
          .build();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .addInterceptor(ApiDownInterceptor.create()
            .checkWith(checker)
            .build())
          .build();
        return new Ok3Client(okHttpClient);
    }

    private void sendRequest() {
        setupApis();

        switch (requestOptions.getCheckedRadioButtonId()) {
            case R.id.radio_working:
                requestWorking();
                break;
            case R.id.radio_broken:
                requestBroken();
                break;
            case R.id.radio_unreachable:
                requestUnreachable();
        }
    }

    private void requestWorking() {
        httpstatApi.get200(new Callback<Response>() {
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
        httpstatApi.get503(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                status.setText("It worked :S");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof ApiDownException) {
                    status.setText("API DOWN!!");
                } else {
                    status.setText("Failure: " + error.getMessage());
                }
            }
        });
    }

    private void requestUnreachable() {
        unreachableApi.getSomething(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                status.setText("It worked :S");
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getCause() instanceof ApiDownException) {
                    status.setText("API DOWN!!");
                } else {
                    status.setText("Failure: " + error.getMessage());
                }
            }
        });
    }


}
