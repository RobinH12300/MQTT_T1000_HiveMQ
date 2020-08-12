package com.example.mqtt_t1000_hivemq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.Serializable;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class MainActivity extends AppCompatActivity {


    Button btn_connect;
    ImageButton btn_settings;


    public static String TOPIC = "test/1";

    private static String BROKER = "tcp://broker.mqttdashboard.com:1883";

    private static String USERNAME = "123";
    private static String PASSWORD = "123";

    public static MqttConnectOptions MQTT_CONNECTION_OPTIONS;

    public static MqttAndroidClient CLIENT;

    public static PCL pcl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pcl = new PCL();

        btn_connect = findViewById(R.id.btn_connect);

        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MqttSetup(getApplicationContext());
                MqttConnect();
            }
        });


        btn_settings = findViewById(R.id.btn_settings);

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
    }

    void MqttSetup(Context context) {

        CLIENT = new MqttAndroidClient(getBaseContext(), BROKER, MqttClient.generateClientId());

        MQTT_CONNECTION_OPTIONS = new MqttConnectOptions();

        MQTT_CONNECTION_OPTIONS.setUserName(USERNAME);

        MQTT_CONNECTION_OPTIONS.setPassword(PASSWORD.toCharArray());


        if (BROKER.contains("ssl")) {

            com.example.mqtt_t1000_hivemq.SocketFactory.SocketFactoryOptions socketFactoryOptions = new com.example.mqtt_t1000_hivemq.SocketFactory.SocketFactoryOptions();
            try {

                socketFactoryOptions.withCaInputStream(context.getResources().openRawResource(com.example.mqtt_t1000_hivemq.R.raw.mosquitto_org));
                MQTT_CONNECTION_OPTIONS.setSocketFactory(new com.example.mqtt_t1000_hivemq.SocketFactory(socketFactoryOptions));

            } catch (IOException | NoSuchAlgorithmException | KeyStoreException | CertificateException | KeyManagementException | UnrecoverableKeyException e) {

                e.printStackTrace();

            }
        }
    }



    void MqttConnect() {
        try {

            final IMqttToken token = CLIENT.connect(MQTT_CONNECTION_OPTIONS);
            token.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.d("mqtt:", "connected, token:" + asyncActionToken.toString());
                    Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Receiver.class);
                    startActivity(intent);
                    pcl.setConnect("true");

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    Log.d("mqtt:", "Connection failed" + asyncActionToken.toString());
                    Toast.makeText(getApplicationContext(), "Connection failed", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {

            e.printStackTrace();

        }

    }

    public static void setTopic(String s){
        TOPIC = s;
    }
}