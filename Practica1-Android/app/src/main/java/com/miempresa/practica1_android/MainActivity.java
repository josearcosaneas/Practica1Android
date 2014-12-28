package com.miempresa.practica1_android;

/**
 * Librerias necesarias para construir la aplicacion
 */
import android.os.Bundle;
import android.view.Menu;
import android.app.Activity;
import android.media.MediaPlayer;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.pm.ActivityInfo;
import java.util.List;


/**
 * Clase principal de la aplicacion.
 * @author José Arcos Aneas
 * @version 0.2
 * @see <a href="https://github.com/josearcosaneas/Nuevos-Paradigmas-Iteracci-n/tree/master/Practica1-Android"
 */
public class MainActivity extends Activity implements SensorEventListener {
    MediaPlayer mp;
    Button b1;
    int posicion = 0;
    public float curX = 0, curY = 0, curZ = 0;
    public static Integer[] mCancionesIds ={R.raw.elrey, R.raw.quienmanda ,R.raw.tengopena};
    public int numeroCanciones= mCancionesIds.length;
    public int numCan =0;


    /**
     * Se llama cuando la actividad se esta iniciando.
     * Aqui es donde debe ir la mayoria de la inicializacion llamando a
     * setContentview para iniciar la interfaz de usuario, tambien a findViewById
     * para interactuar mediante widgets en la interfaz de usuario...
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b1 = (Button) findViewById(R.id.button1);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Funcion que crea el menu de opciones
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * Funcion onResume funcion que reinicia el servicio si no ha sido
     * matado (killed) el proceso, o sea cuando es pausado y se mantiene
     * activo en segundo plano.
     * Aqui registramos el servicio.
     */
    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }
    }

    /**
     * Funcion que detiene el servicio (activity). Indicamos
     * que no queremos recibir mas.
     */
    @Override
    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        super.onStop();
    }

    /**
     * Funcion llamada cada vez que la precision del sensor cambia
     * @param sensor
     * @param accuracy
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Funcion que responde a las posiciones del terminal gracias al
     * acelerometor
     * Esta funcion es llamada cuando los valores del sensor cambian.
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            // el vector values indica los valores en x,y,z dependiendo
            // de la posicion estos valores se miden en m/s^2 e indican la
            // fuerza aplicada al terminal en cada eje.
            curX = event.values[0];
            curY = event.values[1];
            curZ = event.values[2];
            // determinamos la posicion que activara los diferentes eventos
            // iniciar, continuar, destruir o pasar una cancion.
            if(curX > 0f && curX < 1f && curY < 10f && curY > 9f && curZ > 0f && curZ < 1f){
                if (posicion == 0 )
                    iniciar();
                else continuar();
            }

            if (curX > -1f && curX < 1f && curY > -1f && curY < 1f && curZ > -11f && curZ < -9f){
                destruir();
            }

            if (curX >= 2f && curX <=3f && curY > 9f && curZ < 5f){

                pasarCancion();
            }
            if(curX > -1f && curX < 1f && curY < -1f && curY > 0f && curZ > 10f && curZ < 10.5f){

                pausar();
            }
            // Informaremos por pantalla de los valores que toma el acelerometro
            ((TextView) findViewById(R.id.txtAccX)).setText("X: " + curX);
            ((TextView) findViewById(R.id.txtAccY)).setText("Y: " + curY);
            ((TextView) findViewById(R.id.txtAccZ)).setText("Z: " + curZ);
        }

    }

    /**
     * Funcion para destruir el servicio mediaPlayer
     */
    public void destruir() {
        if (mp != null)
            mp.release();
    }

    /**
     * Funcion que inicia el servicio a partir de la interfaz grafica
     * @param v
     */
    public void iniciar(View v) {
        destruir();
        mp = MediaPlayer.create(this, mCancionesIds[numCan]);
        mp.start();
        String op = b1.getText().toString();
        mp.setLooping(true);
    }

    /**
     * Funcion encargada de pasar a otra cancion
     */
    public void pasarCancion(){
        destruir();

        numCan++;
        if (numCan >= numeroCanciones){
            numCan=0;
        }
        mp= MediaPlayer.create(this,mCancionesIds[numCan]);
        mp.start();
        mp.setLooping(true);

    }

    /**
     * Funcion que se encarga de iniciar la aplicacion
     */
    public void iniciar() {
        destruir();
        mp = MediaPlayer.create(this, mCancionesIds[numCan]);
        mp.start();
        String op = b1.getText().toString();
        mp.setLooping(true);
    }


    /**
     * Funcion que se encarga de pausar la aplicacion a partir de la
     * interfaz grafica
     * @param v
     */
    public void pausar(View v) {
        if (mp != null && mp.isPlaying()) {
            posicion = mp.getCurrentPosition();
            mp.pause();
        }
    }

    /**
     * Funcion que se encarga de pausar la aplicacion
     */
    public void pausar() {

        posicion = mp.getCurrentPosition();
        mp.pause();

    }

    /**
     * Funcion para continuar una cancion desde la posicion en que se
     * pauso
     * @param v
     */
    public void continuar(View v) {
        if (mp != null && mp.isPlaying() == false) {
            mp.seekTo(posicion);
            mp.start();
        }
    }

    /**
     * Funcion para continuar una cancion desde la posicion en que fue
     * pausada
     */
    public void continuar() {
        if (mp != null && mp.isPlaying() == false) {
            mp.seekTo(posicion);
            mp.start();
        }
    }




}
