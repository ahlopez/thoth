package com.f.thoth.ui.views.expediente;

import static com.f.thoth.Parm.CURRENT_USER;

import java.time.LocalDateTime;
import java.util.Optional;

import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.expediente.BaseExpediente;
import com.f.thoth.backend.data.gdoc.expediente.ExpedienteGroup;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.service.BaseExpedienteService;
import com.f.thoth.backend.service.ExpedienteGroupService;
import com.f.thoth.backend.service.SchemaService;
import com.f.thoth.ui.components.Notifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
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

  private Button             save;
  private Button             delete;
  private Button             close;
  private Component          buttons;


  public ExpedienteGroupEditor( ExpedienteGroupService  expedienteGroupService,
                                BaseExpedienteService   baseExpedienteService,
                                SchemaService           schemaService,
                                Classification          classificationClass
                               )
  {
    this.expedienteGroupService  = expedienteGroupService;
    this.baseExpedienteService   = baseExpedienteService;
    this.schemaService           = schemaService;
    this.currentUser             = (User)VaadinSession.getCurrent().getAttribute(CURRENT_USER);
    this.classificationClass     = classificationClass;
    this.currentGroup            = null;


    add(configureEditor());
    buttons = configureActions();
    add(buttons);

    addClassName("field-form");
    setVisible(false);

  }//ExpedienteGroupEditor



  private BaseExpedienteEditor configureEditor()
  {
    baseExpedienteEditor = new BaseExpedienteEditor(schemaService);
    baseExpedienteEditor.addListener(BaseExpedienteEditor.ValidationEvent.class, e -> save.setEnabled(e.getValidationResult()));
    return baseExpedienteEditor;
  }//configureEditor


  private Component configureActions()
  {
    save = new Button("Guardar");
    save.addClickShortcut (Key.ENTER);
    save.addThemeVariants  (ButtonVariant.LUMO_PRIMARY);
    save.getElement().getStyle().set("margin-left", "auto");
    save.setWidth("20%");
    save.addClickListener  (click -> saveGroup(currentGroup));

    delete = new Button("Eliminar");
    delete.addClickShortcut (Key.DELETE);
    delete.addThemeVariants  (ButtonVariant.LUMO_CONTRAST);
    delete.getElement().getStyle().set("margin-left", "auto");
    delete.setWidth("20%");
    delete.addClickListener  (click -> deleteGroup(currentGroup));

    close= new Button("Cancelar");
    close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);
    close.addClickShortcut(Key.ESCAPE);
    close. setWidth("20%");
    close.addClickListener (click ->
    { baseExpedienteEditor.close();
      closeEditor();
    });

    HorizontalLayout buttons = new HorizontalLayout(close, delete, save);
    buttons.getElement().setAttribute("colspan", "4");
    buttons.setWidthFull();
    return buttons;
  }//configureActions


  public void addExpedienteGroup(BaseExpediente parentBase)
  {
     ExpedienteGroup newGroup  = createGroup(parentBase);
     editExpedienteGroup(newGroup);
  }//addExpedienteGroup


  private  ExpedienteGroup   createGroup(BaseExpediente parentBase)
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
    newGroup.setOwnerId             ( parentBase == null? null : parentBase.getId());
    newGroup.setOpen                (true);
    newGroup.setKeywords            ("keyword1, keyword2, keyword3");
    newGroup.setMac                 ("[mac]");

    return newGroup;

  }//createGroup


  private void setVisibility( boolean visibility)
  {
     baseExpedienteEditor.setVisible(visibility);
     buttons             .setVisible(visibility);
     setVisible(visibility);
  }//setVisibility



  public void editExpedienteGroup(ExpedienteGroup group)
  {
    if (group == null)
    {  closeEditor();
    } else
    {
       currentGroup = group;
       setVisibility(true);
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


  private void saveGroup(ExpedienteGroup group)
  {
     if ( group != null && baseExpedienteEditor.saveBaseExpediente())
     {
        boolean isNew = !group.isPersisted();
        expedienteGroupService.save(currentUser, group);
        //TODO: *** Guardar el grupo en el repositorio
        notifier.accept("Grupo de expedientes "+ group.formatCode()+ (isNew? " creado" : " actualizado"));
     }
     closeEditor();

  }//saveGroup


  private void deleteGroup(ExpedienteGroup group)
  {
    if (group != null && group.isPersisted())
    {
      if (!expedienteGroupService.hasChildren(group))
      {  expedienteGroupService.delete(currentUser, group);
         //TODO: *** Eliminar grupo de expedientes del repositorio
         notifier.accept("Grupo de expedientes "+ group.formatCode()+ " eliminado");
      }else
      {  notifier.error("Grupo de expedientes no puede ser eliminado pues contiene subgrupos");
      }
    }
    closeEditor();

  }//deleteGroup


  public void closeEditor()
  {
    setVisibility(false);
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
