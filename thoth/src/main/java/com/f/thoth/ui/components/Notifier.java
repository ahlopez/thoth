package com.f.thoth.ui.components;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;

public class Notifier
{
   private Notification notification;
   
   public Notifier()
   { 
	   notification = new Notification();
   }//Notifier constructor
   
   
   public void show( String text, String style, int duration, Notification.Position position)
   {
      H2 label = new H2(text);
      label.addClassName(style);
      notification.removeAll();
      notification.add(label);
      notification.setDuration(duration);
      notification.setPosition(position);
      notification.open();
   }//show
   
   public void error( String text)
   {
      show(text, "notifier-error", 6000, Notification.Position.BOTTOM_CENTER);
   }//error
   
   public void accept( String text)
   {
      show(text,  "notifier-accept",  4000,  Notification.Position.BOTTOM_CENTER);
   }//accept
   
   
   
}//Notifier
