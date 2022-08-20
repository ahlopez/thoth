package com.f.thoth.ui.views.search;


import static com.f.thoth.ui.utils.Constant.PAGE_CONSULTA_LIBRE;
import static com.f.thoth.ui.utils.Constant.TITLE_CONSULTA_LIBRE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import com.f.thoth.backend.data.Role;
import com.f.thoth.ui.MainView;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Consulta documental libre
 */
@Route(value = PAGE_CONSULTA_LIBRE, layout = MainView.class)
@PageTitle(TITLE_CONSULTA_LIBRE)
@Secured(Role.ADMIN)
public class DocumentOpenSearchView extends VerticalLayout
{
   private FormLayout              topSection;
   private VerticalLayout          bottomSection;
   private H3                      title;
   private TextField               searchArgument;



   @Autowired
   public DocumentOpenSearchView()
   {
      addClassName("main-view");
      setWidthFull();
      
      
      topSection  = new FormLayout();
      topSection.addClassName  ("top-section");
      topSection.setWidthFull();
      topSection.setResponsiveSteps(
            new ResponsiveStep("30em", 1),
            new ResponsiveStep("80em", 2)
            );
      
      title = new H3("CONSULTA DOCUMENTAL");
      title.getElement().setAttribute("colspan", "2");
   //   title.getElement().getStyle().set("background",  "ivory");
      title.getElement().getStyle().set("color",       "blue");
      title.getElement().getStyle().set("font-weight", "bold");
      topSection.add(title);


      TextField  searchArgument    = new TextField("Buscar documentos que contengan");
      searchArgument.setRequired(true);
      searchArgument.setRequiredIndicatorVisible(true);
      searchArgument.setErrorMessage("El argumento de búsqueda es obligatorio y no puede estar en blanco");
      searchArgument.getElement().setAttribute("colspan", "2");
      topSection.add(searchArgument);

      bottomSection      = new VerticalLayout();
      bottomSection.addClassName ("bottom-section");
      bottomSection.setWidthFull();
      bottomSection.add(new H3 ("(((Resultado de consulta)))"));

      add(topSection, bottomSection);

   }//DocumentOpenSearchView

   protected String getBasePage() { return PAGE_CONSULTA_LIBRE; }

   /*
   private void closeEditor()
   {
      topSection.removeClassName     ("top-section");
      bottomSection .removeClassName ("bottom-section");
      removeClassName                ("main-view");

   }//closeEditor
   */

}//OpenDocumentSearchView
