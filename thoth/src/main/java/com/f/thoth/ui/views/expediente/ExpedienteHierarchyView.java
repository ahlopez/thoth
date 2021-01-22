package com.f.thoth.ui.views.expediente;

import static com.f.thoth.ui.utils.Constant.PAGE_JERARQUIA_EXPEDIENTES;
import static com.f.thoth.ui.utils.Constant.TITLE_JERARQUIA_EXPEDIENTES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.service.ClassificationService;
import com.f.thoth.backend.service.ExpedienteService;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = PAGE_JERARQUIA_EXPEDIENTES, layout = MainView.class)
@PageTitle(TITLE_JERARQUIA_EXPEDIENTES)
@Secured(Role.ADMIN)
class ExpedienteHierarchyView extends VerticalLayout implements HasUrlParameter<String>
{
	   private ClassificationService classificationService;
	   private ExpedienteService     expedienteService;
	   private User                  currentUser;
	   private String                classCode;
	   
	   private VerticalLayout        content;
	   
	   @Autowired
	   public ExpedienteHierarchyView(ClassificationService classificationService, ExpedienteService expedienteService)
	   {
		  this.classificationService = classificationService;
	      this.expedienteService     = expedienteService;
	      this.currentUser           = ThothSession.getCurrentUser();
	      
	      addClassName("main-view");
	      setSizeFull();
	      
	      content      = new VerticalLayout();
	      content.addClassName ("selector");
	      content.add(new H3 ("Expedientes de la clase"));
	      //content.add( configureClassSelector());
	      
	     // updateSelector();
	     // closeEditor();

	      content.setSizeFull();
	      add( content);
	   //   Notification.show("LLequé a jerarquía de expedientes");
	    
	        
	/*
	      leftSection  = new VerticalLayout();
	      leftSection.addClassName  ("left-section");
	      leftSection.add(new H3 ("Clasificación del expediente"));
	      leftSection.add( configureClassSelector());

	      rightSection = new VerticalLayout();
	      rightSection.addClassName ("right-section");

	      content      = new VerticalLayout();
	      content.addClassName ("content");
	      content.setSizeFull();
	      content.add(new H3("Expedientes, Subexpedientes y Volúmenes"));
	      //content.add(configureExpedienteSelector(), configureExpedienteActions());

	      //rightSection.add(configureForm(expedienteForm ));
	      updateSelector();
	      closeEditor();

	      HorizontalLayout panel=  new HorizontalLayout( content, rightSection);
	      panel.setSizeFull();
	      add( panel);
	*/
	   }//ClassificationView

	@Override
	public void setParameter(BeforeEvent event, String parameter) 
	{
		Notification.show("Voy a navegar con parámetro["+ parameter+ "]");
	}
	

}//ExpedienteHierarchyView
