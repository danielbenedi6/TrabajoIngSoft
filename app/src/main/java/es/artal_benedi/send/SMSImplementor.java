package es.artal_benedi.send;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by usuario on 15/11/15.
 */
public class SMSImplementor implements SendImplementor {

    /** actividad desde la cual se abrirá la actividad de gestión de correo */
    private Activity sourceActivity;

    /** Constructor
     * @param source actividad desde la cual se abrirá la actividad de gesti�n de correo
     */
    public SMSImplementor(Activity source){
        setSourceActivity(source);
    }

    /**  Actualiza la actividad desde la cual se abrirá la actividad de gestión de correo */
    public void setSourceActivity(Activity source) {
        sourceActivity = source;
    }

    /**  Recupera la actividad desde la cual se abrirá la actividad de gestión de correo */
    public Activity getSourceActivity(){
        return sourceActivity;
    }

    /**
     * Implementación del método send utilizando la aplicación de gestión de correo de Android
     * Solo se copia el asunto y el cuerpo
     * @param subject asunto
     * @param body cuerpo del mensaje
     */
    public void send (String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(" sms_body ", subject +": "+ body);
        intent.setType("vnd.android -dir /mms -sms ");
        getSourceActivity().startActivity(intent);
   }

}
