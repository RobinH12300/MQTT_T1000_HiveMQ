package com.example.mqtt_t1000_hivemq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class Settings extends AppCompatActivity {

    private EditText et_ipHost;
    private EditText et_username;
    private EditText et_pw;
    private EditText et_topic;

    private Button btn_enter;

    private String BROKER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        et_ipHost = findViewById(R.id.et_ipHost);
        et_pw = findViewById(R.id.et_pw);
        et_username = findViewById(R.id.et_username);
        et_topic = findViewById(R.id.et_topic);

        btn_enter = findViewById(R.id.btn_enter);

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInputs();
                MqttSetup(getApplicationContext());
                MqttConnect();
            }
        });
    }

    void MqttSetup(Context context) {

        MainActivity.CLIENT = new MqttAndroidClient(getBaseContext(), BROKER, MqttClient.generateClientId());

        MainActivity.MQTT_CONNECTION_OPTIONS = new MqttConnectOptions();

        if(!(et_username.getText().toString().equals(""))){
            MainActivity.MQTT_CONNECTION_OPTIONS.setUserName(et_username.getText().toString());
        }

        if(!(et_pw.getText().toString().toCharArray().equals(""))){
            MainActivity.MQTT_CONNECTION_OPTIONS.setPassword(et_pw.getText().toString().toCharArray());
        }



        if (BROKER.contains("ssl")) {

            com.example.mqtt_t1000_hivemq.SocketFactory.SocketFactoryOptions socketFactoryOptions = new com.example.mqtt_t1000_hivemq.SocketFactory.SocketFactoryOptions();
            try {

                socketFactoryOptions.withCaInputStream(context.getResources().openRawResource(com.example.mqtt_t1000_hivemq.R.raw.mosquitto_org));
                MainActivity.MQTT_CONNECTION_OPTIONS.setSocketFactory(new com.example.mqtt_t1000_hivemq.SocketFactory(socketFactoryOptions));

            } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {

                e.printStackTrace();

            }
        }
    }


    void MqttConnect() {
        try {
            final IMqttToken token = MainActivity.CLIENT.connect(MainActivity.MQTT_CONNECTION_OPTIONS);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt:", "connected, token:" + asyncActionToken.toString());
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Receiver.class);
                    startActivity(intent);
                    MainActivity.pcl.setConnect("connected");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("mqtt:", "not connected" + asyncActionToken.toString());
                    Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }


    private void getInputs() {

        BROKER = et_ipHost.getText().toString();
        setTopic();
    }

    private void setTopic() {
        MainActivity.setTopic(et_topic.getText().toString());
        MainActivity.pcl.setTopic(et_topic.getText().toString());
    }
}
