package ayuda.cl.mqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private final MemoryPersistence persistence = new MemoryPersistence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //++++++++++++++++++++++++++++++++++++++++++++
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.emqx.io:1883", "jajanoseayuda", persistence);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("La conexión se ha perdido");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Nuevo mensaje: " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Envío completado");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("La conexión fue exitosa");
                    try {
                        System.out.println("Suscribiendose a /test");
                        mqttAndroidClient.subscribe("android/mqtt", 0);
                        System.out.println("Suscrito a /test");
                        System.out.println("Publicando mensaje...");
                        mqttAndroidClient.publish("android/mqtt", new MqttMessage("Hola, quiero un 7 por favor...".getBytes()));
                    } catch (MqttException ex) {
                        ex.printStackTrace();
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Ha ocurrido un error al conectarse");
                    System.out.println("throwable: " + exception.toString());
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex);
        }
    }
}