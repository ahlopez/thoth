package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.Optional;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.BranchExpediente;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.BranchExpedienteService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class BranchExpedienteEditor extends VerticalLayout
{
  private BranchExpedienteService     branchExpedienteService;
  private BaseExpedienteService       baseExpedienteService;
  private SchemaService               schemaService;

  private BranchExpediente            currentBranch;
  private Classification              classificationClass;
  private User                        currentUser;

  private BaseExpedienteEditor        baseExpedienteEditor;
  private Notifier notifier           = new Notifier();


  public BranchExpedienteEditor( BranchExpedienteService branchExpedienteService,
                                 BaseExpedienteService   baseExpedienteService,
                                 SchemaService           schemaService,
                                 Classification          classificationClass
                               )
  {
    this.branchExpedienteService = branchExpedienteService;
    this.baseExpedienteService   = baseExpedienteService;
    this.schemaService           = schemaService;
    this.currentUser             = ThothSession.getCurrentUser();
    this.classificationClass     = classificationClass;
    this.currentBranch           = null;

    add(configureEditor());
    setVisible(false);

  }//BranchExpedienteEditor



  private BaseExpedienteEditor configureEditor()
  {
    baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
    baseExpedienteEditor.addListener(BaseExpedienteEditor.SaveEvent.class,    this::saveExpediente );
    baseExpedienteEditor.addListener(BaseExpedienteEditor.CloseEvent.class,   e -> closeEditor());
    baseExpedienteEditor.addListener(BaseExpedienteEditor.DeleteEvent.class,  this::deleteExpediente);
    return baseExpedienteEditor;
  }//configureEditor


  public void addBranchExpediente(BaseExpediente parentBase)
  {
     BranchExpediente parentBranch  = loadBranch( parentBase);
     editBranchExpediente(createBranch(parentBranch));
  }//addBranchExpediente


  private BranchExpediente loadBranch( BaseExpediente base)
  {
    BranchExpediente branch = base == null? null : branchExpedienteService.findByCode(base.getCode());
    return branch;
  }//loadBranch


  private  BranchExpediente   createBranch(BranchExpediente parentBranch)
  {
    BranchExpediente       newBranch = new BranchExpediente();
    LocalDateTime               now  = LocalDateTime.now();
    newBranch.setExpedienteCode      (null);
    newBranch.setPath                (null);
    newBranch.setName                (" ");
    newBranch.setObjectToProtect     (new ObjectToProtect());
    newBranch.setCreatedBy           (currentUser);
    newBranch.setClassificationClass (classificationClass);
    newBranch.setMetadataSchema      (null);
    newBranch.setMetadata            (null);
    newBranch.setDateOpened          (now);
    newBranch.setDateClosed          (now.plusYears(1000L));
    newBranch.setOwnerId             ( parentBranch == null? null : parentBranch.getExpediente().getId());
    newBranch.setOpen                (true);
    newBranch.setKeywords            ("keyword1, keyword2, keyword3");
    newBranch.setMac                 ("[mac]");
    return newBranch;

  }//createBranch


  public void editBranchExpediente(BranchExpediente branch)
  {
    if (branch == null)
    {  closeEditor();
    } else
    {
       currentBranch = branch;
       setVisible(true);
       baseExpedienteEditor.setVisible(true);
       BaseExpediente base = currentBranch.getExpediente();
       String   parentCode = getParentCode( base);
       baseExpedienteEditor.editExpediente(base, parentCode);
    }
  }//editBranchExpediente


  private String getParentCode( BaseExpediente base)
  {
     if (base != null)
     {
        Long parentId = base.getOwnerId();
        if (parentId != null)
        {
           Optional<BaseExpediente> parent = baseExpedienteService.findById( parentId);
           if(parent.isPresent())
           { return parent.get().formatCode();
           }
       }
       return base.getClassificationClass().formatCode();
     }
     return null;
   }//getParentCode


  private void saveExpediente(BaseExpedienteEditor.SaveEvent event)
  {
     BaseExpediente expediente = event.getBaseExpediente();
     if ( expediente != null)
     {
        /*
        SchemaValues metadata = expediente.getMetadata();
        if ( !metadata.isPersisted())
        {
           schemaValuesService.save(currentUser, expediente.getMetadata());
           expediente.setMetadata(metadata);
        }
        */
        boolean isNew = !expediente.isPersisted();
        int  duration = isNew? 6000 : 3000;
        if (currentBranch != null)
        {  currentBranch.setExpediente(expediente);
           branchExpedienteService.save(currentUser, currentBranch);
           String businessCode = expediente.formatCode();
           String msg          = isNew? "Grupo de expedientes creado con código "+ businessCode: "Grupo de expedientes "+ businessCode+ " actualizado";
           notifier.show(msg, "notifier-accept", duration, Notification.Position.BOTTOM_CENTER);
        }
        closeEditor();
     }
  }//saveExpediente


  private void deleteExpediente(BaseExpedienteEditor.DeleteEvent event)
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
    baseExpedienteEditor.setVisible(false);
//    removeClassName("selected-item-form");
    fireEvent(new CloseEvent(this, currentBranch));
    currentBranch = null;
  }//closeEditor


  // --------------------- Events -----------------------
  public static abstract class BranchExpedienteEditorEvent extends ComponentEvent<BranchExpedienteEditor>
  {
    private BranchExpediente branchExpediente;

    protected BranchExpedienteEditorEvent(BranchExpedienteEditor source, BranchExpediente branchExpediente)
    {  super(source, false);
       this.branchExpediente = branchExpediente;
    }//BranchExpedienteEditorEvent

    public BranchExpediente getBranchExpediente(){ return branchExpediente;  }
    public BaseExpediente   getExpediente()      { return branchExpediente == null? null: branchExpediente.getExpediente();}

  }//BranchExpedienteEditorEvent

  public static class SaveEvent extends BranchExpedienteEditorEvent
  {
    SaveEvent(BranchExpedienteEditor source, BranchExpediente branchExpediente)
    {  super(source, branchExpediente);
    }
  }//SaveEvent

  public static class DeleteEvent extends BranchExpedienteEditorEvent
  {
    DeleteEvent(BranchExpedienteEditor source, BranchExpediente branchExpediente)
    {  super(source, branchExpediente);
    }
  }//DeleteEvent

  public static class CloseEvent extends BranchExpedienteEditorEvent
  {
    CloseEvent(BranchExpedienteEditor source, BranchExpediente branchExpediente)
    {  super(source, branchExpediente);
    }
  }//CloseEvent

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
  {
    return getEventBus().addListener(eventType, listener);
  }//addListener

}//BranchExpedienteEditor
