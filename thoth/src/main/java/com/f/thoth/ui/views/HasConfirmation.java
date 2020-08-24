package com.f.thoth.ui.views;

import com.vaadin.flow.component.confirmdialog.ConfirmDialog;

public interface HasConfirmation 
{
   void setConfirmDialog(ConfirmDialog confirmDialog);
   
   ConfirmDialog getConfirmDialog();
   
}//HasConfirmation
