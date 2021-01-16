package es.artal_benedi.send;

import android.app.Activity;

/** 
 * Define la interfaz para las clases de la implementacion.
 * La interfaz no se tiene que corresponder directamente con la interfaz de la abstraccion.
 *  
 */
public interface SendImplementor {
	   
   /**  Actualiza la actividad desde la cual se abrira la actividad de envi�o de listas de compra */
   public void setSourceActivity(Activity source);

   /**  Recupera la actividad desde la cual se abrira la actividad de envio de listas de compra */
   public Activity getSourceActivity();

   /** Permite lanzar la actividad encargada de gestionar el envio de listas de compra */
   public void send(String subject, String body);

}
