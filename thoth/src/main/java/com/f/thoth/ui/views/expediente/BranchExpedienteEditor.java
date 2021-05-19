package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BranchExpedienteService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.backend.service.SchemaValuesService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class BranchExpedienteEditor extends VerticalLayout
{
  private BranchExpedienteService     branchExpedienteService;
  private SchemaService               schemaService;
  private SchemaValuesService         schemaValuesService;

  private BranchExpediente            currentBranch;            // Branch that is presented on right panel
  private BranchExpediente            parentBranch;             // Branch that is parent of currentBranch in expediente hierarchy
  private Classification              classificationClass;
  private User                        currentUser;
  
  private BaseExpedienteForm          baseExpedienteForm;
  private Registration                saveListener;          
  private Registration                deleteListener;
  private Registration                closeListener;
  private Notifier notifier     = new Notifier();


  public BranchExpedienteEditor( BranchExpedienteService branchExpedienteService, 
		                         SchemaService schemaService, 
		                         SchemaValuesService schemaValuesService,
		                         Classification classificationClass)
  {
    this.branchExpedienteService = branchExpedienteService;
    this.schemaService           = schemaService;
    this.schemaValuesService     = schemaValuesService;
    this.currentUser             = ThothSession.getCurrentUser();
    this.classificationClass     = classificationClass;
    this.currentBranch           = null;
    this.parentBranch            = null;

    add(configureEditor());
    setVisible(false);

  }//BranchExpedienteEditor


  public void resetEditor()
  {
    this.currentBranch           = null;
  }//resetEditor


  private BaseExpedienteForm configureEditor()
  {
    baseExpedienteForm = new BaseExpedienteForm(schemaService);
    return baseExpedienteForm;
  }//configureEditor

  private void registerListeners()
  {
	  saveListener   = baseExpedienteForm.addListener(BaseExpedienteForm.SaveEvent.class,    this::saveExpediente );
	  closeListener  = baseExpedienteForm.addListener(BaseExpedienteForm.CloseEvent.class,   e -> closeEditor());
	  deleteListener = baseExpedienteForm.addListener(BaseExpedienteForm.DeleteEvent.class,  this::deleteExpediente);
  }//registerListeners
  
  private void removeListeners()
  {
	if( saveListener != null)
	{  saveListener.remove();
	   saveListener = null;
	}
	if( closeListener != null)
	{ closeListener.remove();
	  closeListener = null;
	}
	if( deleteListener != null)
	{  deleteListener.remove();
	   deleteListener = null;
	}
  }//removeListeners

  
  public void addBranchExpediente(BranchExpediente parentBranch)
  {
    this.parentBranch  = parentBranch;
    this.currentBranch = createBranch();
    registerListeners();
    editBranchExpediente(currentBranch);
  }//addBranchExpediente



  private  BranchExpediente   createBranch()
  {
    BranchExpediente newBranch = new BranchExpediente();
    LocalDateTime         now  = LocalDateTime.now();
    newBranch.setExpedienteCode      (null);
    newBranch.setPath                (null);
    newBranch.setName                (" ");
    newBranch.setObjectToProtect     (new ObjectToProtect());
    newBranch.setCreatedBy           (currentUser);
    newBranch.setClassificationClass (classificationClass);
    newBranch.setMetadataSchema      (null);
    newBranch.setMetadata            (SchemaValues.EMPTY);
    newBranch.setDateOpened          (now);
    newBranch.setDateClosed          (now.plusYears(1000L));
    newBranch.setOwnerPath           (null);
    newBranch.setOpen                (true);
    newBranch.setKeywords            ("keyword1, keyword2, keyword3");
    newBranch.setMac                 ("[mac]");
    if (parentBranch != null)
    {
      currentBranch.setOwnerPath(parentBranch.getPath());
    }
    return newBranch;

  }//createBranch


  public void editBranchExpediente(BranchExpediente branch)
  {
    if (branch == null)
    {  closeEditor();
    }  else
    {
      if ( branch.isPersisted())
      {  branch = branchExpedienteService.load(branch.getId());
      }
      registerListeners();
      setVisible(true);
      baseExpedienteForm.setVisible(true);
      baseExpedienteForm.addClassName("selected-item-form");
      baseExpedienteForm.setExpediente(branch.getExpediente());
    }
  }//editBranchExpediente


  private void saveExpediente(BaseExpedienteForm.SaveEvent event)
  {
	  BaseExpediente expediente = event.getBaseExpediente();
	  if ( expediente.isOfType(Nature.GRUPO))
	  {  
		  boolean isNew = !expediente.isPersisted();
		  schemaValuesService.save(currentUser, expediente.getMetadata());
		  if (currentBranch != null)
		  {  branchExpedienteService.save(currentUser, currentBranch);
		     if (isNew)
		     {  notifier.show("Grupo de expedientes creado con código "+ expediente.formatCode(),  "notifier-accept",  6000,  Notification.Position.BOTTOM_CENTER);
	         }else
		     {  notifier.show("Grupo de expedientes "+ expediente.formatCode()+ " actualizado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
		     }
		  }   
	  }
	  closeEditor();
  }//saveExpediente
  
  
  private void deleteExpediente(BaseExpedienteForm.DeleteEvent event)
  {
	  BaseExpediente expediente = event.getBaseExpediente();
	  if (expediente.isOfType(Nature.GRUPO) && expediente.isPersisted())
	  {  
	    if (!branchExpedienteService.hasChildren(currentBranch))
	    {  branchExpedienteService.delete(currentUser, currentBranch);
	       notifier.show("Grupo de expedientes "+ expediente.formatCode()+ " eliminado",    "notifier-accept",  3000,  Notification.Position.BOTTOM_CENTER);
	    }else
	    {  notifier.error("Grupo de expedientes no puede ser eliminado pues tiene expedientes hijos");
	    }
	  }
	  closeEditor();
  }//deleteExpediente


  public void closeEditor()
  {
    baseExpedienteForm.setExpediente(null);
    baseExpedienteForm.setVisible(false);
    baseExpedienteForm.removeClassName("selected-item-form");
    removeListeners();
    currentBranch = null;
  }//closeEditor

}//BranchExpedienteEditor
