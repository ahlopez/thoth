package com.f.thoth.ui.views.expediente;

import java.time.LocalDateTime;
import java.util.Optional;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.gdoc.expediente.Nature;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

public class ExpedienteGroupEditor extends VerticalLayout
{
  private ExpedienteGroupService      expedienteGroupService;
  private BaseExpedienteService       baseExpedienteService;
  private SchemaService               schemaService;

  private ExpedienteGroup             currentGroup;
  private Classification              classificationClass;
  private User                        currentUser;

  private BaseExpedienteEditor        baseExpedienteEditor;
  private Notifier notifier           = new Notifier();


  public ExpedienteGroupEditor( ExpedienteGroupService  expedienteGroupService,
                                BaseExpedienteService   baseExpedienteService,
                                SchemaService           schemaService,
                                Classification          classificationClass
                               )
  {
    this.expedienteGroupService  = expedienteGroupService;
    this.baseExpedienteService   = baseExpedienteService;
    this.schemaService           = schemaService;
    this.currentUser             = ThothSession.getCurrentUser();
    this.classificationClass     = classificationClass;
    this.currentGroup            = null;

    add(configureEditor());
    setVisible(false);

  }//ExpedienteGroupEditor



  private BaseExpedienteEditor configureEditor()
  {
    baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
    baseExpedienteEditor.addListener(BaseExpedienteEditor.SaveEvent.class,    this::saveExpediente );
    baseExpedienteEditor.addListener(BaseExpedienteEditor.CloseEvent.class,   e -> closeEditor());
    baseExpedienteEditor.addListener(BaseExpedienteEditor.DeleteEvent.class,  this::deleteExpediente);
    return baseExpedienteEditor;
  }//configureEditor


  public void addExpedienteGroup(BaseExpediente parentBase)
  {
     ExpedienteGroup parentGroup  = loadGroup( parentBase);
     editExpedienteGroup(createGroup(parentGroup));
  }//addExpedienteGroup


  private ExpedienteGroup loadGroup( BaseExpediente base)
  {
    ExpedienteGroup group = base == null? null : expedienteGroupService.findByCode(base.getCode());
    return group;
  }//loadGroup


  private  ExpedienteGroup   createGroup(ExpedienteGroup parentGroup)
  {
    ExpedienteGroup        newGroup = new ExpedienteGroup();
    LocalDateTime              now  = LocalDateTime.now();
    newGroup.setExpedienteCode      (null);
    newGroup.setPath                (null);
    newGroup.setName                (" ");
    newGroup.setObjectToProtect     (new ObjectToProtect());
    newGroup.setCreatedBy           (currentUser);
    newGroup.setClassificationClass (classificationClass);
    newGroup.setMetadataSchema      (null);
    newGroup.setMetadata            (null);
    newGroup.setDateOpened          (now);
    newGroup.setDateClosed          (now.plusYears(1000L));
    newGroup.setOwnerId             ( parentGroup == null? null : parentGroup.getExpediente().getId());
    newGroup.setOpen                (true);
    newGroup.setKeywords            ("keyword1, keyword2, keyword3");
    newGroup.setMac                 ("[mac]");
    return newGroup;

  }//createGroup


  public void editExpedienteGroup(ExpedienteGroup group)
  {
    if (group == null)
    {  closeEditor();
    } else
    {
       currentGroup = group;
       setVisible(true);
       baseExpedienteEditor.setVisible(true);
       BaseExpediente base = currentGroup.getExpediente();
       String   parentCode = getParentCode( base);
       baseExpedienteEditor.editExpediente(base, parentCode);
    }
  }//editExpedienteGroup


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
        boolean isNew = !expediente.isPersisted();
        int  duration = isNew? 6000 : 3000;
        if (currentGroup != null)
        {  currentGroup.setExpediente(expediente);
           expedienteGroupService.save(currentUser, currentGroup);
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
      if (!expedienteGroupService.hasChildren(currentGroup))
      {  expedienteGroupService.delete(currentUser, currentGroup);
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
    fireEvent(new CloseEvent(this, currentGroup));
    currentGroup = null;
  }//closeEditor


  // --------------------- Events -----------------------
  public static abstract class ExpedienteGroupEditorEvent extends ComponentEvent<ExpedienteGroupEditor>
  {
    private ExpedienteGroup expedienteGroup;

    protected ExpedienteGroupEditorEvent(ExpedienteGroupEditor source, ExpedienteGroup expedienteGroup)
    {  super(source, false);
       this.expedienteGroup = expedienteGroup;
    }//ExpedienteGroupEditorEvent

    public ExpedienteGroup getExpedienteGroup(){ return expedienteGroup;  }
    public BaseExpediente   getExpediente()      { return expedienteGroup == null? null: expedienteGroup.getExpediente();}

  }//ExpedienteGroupEditorEvent

  public static class SaveEvent extends ExpedienteGroupEditorEvent
  {
    SaveEvent(ExpedienteGroupEditor source, ExpedienteGroup expedienteGroup)
    {  super(source, expedienteGroup);
    }
  }//SaveEvent

  public static class DeleteEvent extends ExpedienteGroupEditorEvent
  {
    DeleteEvent(ExpedienteGroupEditor source, ExpedienteGroup expedienteGroup)
    {  super(source, expedienteGroup);
    }
  }//DeleteEvent

  public static class CloseEvent extends ExpedienteGroupEditorEvent
  {
    CloseEvent(ExpedienteGroupEditor source, ExpedienteGroup expedienteGroup)
    {  super(source, expedienteGroup);
    }
  }//CloseEvent

  public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener)
  {
    return getEventBus().addListener(eventType, listener);
  }//addListener

}//ExpedienteGroupEditor
