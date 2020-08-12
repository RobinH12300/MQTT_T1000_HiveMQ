package com.example.mqtt_t1000_hivemq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.UnsupportedEncodingException;

public class Receiver extends AppCompatActivity {

    private Button btn_subscribe;
    private Button btn_unsubscribe;
    private Button btn_disconnect;
    private Button btn_publish;

    private EditText et_publish;

    private TextView dataReceived;
    private TextView tableC;
    private TextView tableT;

    private TableLayout table;

    private boolean isSubscribed = false;
    private boolean isDisconnected = false;

    private PCL pcl;

    private MqttAndroidClient CLIENT = MainActivity.CLIENT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        table = findViewById(R.id.table);
        tableC = findViewById(R.id.tableC);
        tableT = findViewById(R.id.tableT);

        pcl = MainActivity.pcl;

        if(CLIENT.isConnected()){
            tableC.setText("connected");
            tableC.setTextColor(getResources().getColor(R.color.green));
            tableC.setTextSize(13);
        }

        activatePCL();





        dataReceived = findViewById(R.id.dataReceived);

        btn_subscribe = findViewById(R.id.btn_subscribe);

        btn_subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribe(MainActivity.TOPIC, (byte) 1);
            }
        });


        btn_unsubscribe = findViewById(R.id.btn_unsubscribe);

        btn_unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unsubscribe(MainActivity.TOPIC);
            }
        });

        CLIENT.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
            }

            @Override
            public void connectionLost(Throwable cause) {
                MainActivity.pcl.setConnect("disconnected");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                dataReceived.setText(message.toString());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });


        btn_disconnect = findViewById(R.id.btn_disconnect);

        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDisconnected){
                    disconnect();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Already Disconnected",Toast.LENGTH_SHORT).show();
                }
            }
        });


        et_publish = findViewById(R.id.et_publish);

        btn_publish = findViewById(R.id.btn_publish);

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSubscribed) {
                    if(!(getMessage().equals(""))){
                        publish(MainActivity.TOPIC, getMessage());
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Enter a message", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"You have to subscribe first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getMessage() {

        return et_publish.getText().toString();

    }


    public void activatePCL() {
        pcl.addPropertyChangeListener(new PropertyChangeListener() {
                                          @Override
                                          public void propertyChange(PropertyChangeEvent e) {
                                              String state = (String) e.getNewValue();


                                              if(state.equals("disconnected")){
                                                  Log.d("else", state);
                                                  tableC.setText("disconnected");
                                                  tableC.setTextSize(13);
                                                  tableC.setTextColor(getResources().getColor(R.color.red));
                                              }
                                              else if(state.equals("connected")){
                                                  tableC.setText("connected");
                                                  tableC.setTextSize(13);
                                                  tableC.setTextColor(getResources().getColor(R.color.green));
                                              }
                                              else if(state.equals("subscribed")){
                                                  tableT.setText(MainActivity.TOPIC);
                                                  tableT.setTextSize(13);
                                              }
                                              else if(state.equals("unsubscribed")){
                                                  tableT.setText("unsubscribed");
                                                  tableT.setTextSize(13);
                                              }
                                          }
                                      }
        );
    }

    
    private void publish(String TOPIC, String msg){
        byte[] encodedPayload = new byte[0];
        try {

            encodedPayload = msg.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            CLIENT.publish(TOPIC, message);

        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }



    void disconnect() {
        try {
            IMqttToken disconToken = MainActivity.CLIENT.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.d("mqtt:", "disconnected");
                    Toast.makeText(getApplicationContext(),"Disconnected",Toast.LENGTH_SHORT).show();
                    pcl.setConnect("disconnected");
                    isDisconnected = true;
                    //Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    //startActivity(intent);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                    Log.d("mqtt:", "couldn't disconnect");
                    Toast.makeText(getApplicationContext(),"Failed to disconnect",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    void unsubscribe(String topic) {
       if(!isDisconnected) {

           if(isSubscribed) {

               try {

                   IMqttToken unsubToken = MainActivity.CLIENT.unsubscribe(topic);
                   unsubToken.setActionCallback(new IMqttActionListener() {

                       @Override
                       public void onSuccess(IMqttToken asyncActionToken) {


                           Log.d("mqtt:", "unsubcribed");
                           Toast.makeText(getApplicationContext(), "Unsubscribed from:  " + MainActivity.TOPIC, Toast.LENGTH_SHORT).show();
                           isSubscribed = false;
                           pcl.setTopic("unsubscribed");
                       }

                       @Override
                       public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                           Log.d("mqtt:", "couldnt unregister");
                           Toast.makeText(getApplicationContext(), "Failed to Unsubscribe", Toast.LENGTH_SHORT).show();

                       }
                   });

               } catch (MqttException e) {
                   e.printStackTrace();
               }
           }
           else{
               Toast.makeText(getApplicationContext(),"already unsubscribed",Toast.LENGTH_SHORT).show();
           }
       }
       else{
           Toast.makeText(getApplicationContext(),"no connection",Toast.LENGTH_SHORT).show();
       }
    }


    void subscribe(String topic, byte qos) {
        if(!isDisconnected) {

            if (isSubscribed) {
                Toast.makeText(getApplicationContext(), "already subscribed", Toast.LENGTH_SHORT).show();
            }
            else {

                try {

                    IMqttToken subToken = MainActivity.CLIENT.subscribe(topic, qos);
                    subToken.setActionCallback(new IMqttActionListener() {

                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {

                                Log.d("mqtt:", "subscribed" + asyncActionToken.toString());
                                Toast.makeText(getApplicationContext(), "Subscribed to:  " + MainActivity.TOPIC, Toast.LENGTH_SHORT).show();
                                isSubscribed = true;
                                pcl.setTopic("subscribed");
                                Log.d("pcl Topic", "topic set");

                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                            Log.d("mqtt:", "subscribing error");
                            Toast.makeText(getApplicationContext(), "Failed to subscribed to:    " + MainActivity.TOPIC, Toast.LENGTH_SHORT).show();
                            isSubscribed = false;

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"no connection",Toast.LENGTH_SHORT).show();
        }
    }

}


