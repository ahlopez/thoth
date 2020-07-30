package com.f.thoth.ui.crud;

import java.util.function.Consumer;

import com.f.thoth.app.security.CurrentUser;
import com.f.thoth.backend.data.entity.AbstractEntity;
import com.f.thoth.backend.data.entity.util.EntityUtil;
import com.f.thoth.backend.service.FilterableCrudService;
import com.f.thoth.ui.components.SearchBar;
import com.f.thoth.ui.utils.TemplateUtil;
import com.f.thoth.ui.views.HasNotifications;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;

public abstract class AbstractBakeryCrudView<E extends AbstractEntity> extends Crud<E>
        implements HasUrlParameter<Long>, HasNotifications {

    private static final String DISCARD_MESSAGE = "Hay modificaciones sin guardar a %s. Descartar los cambios?";
    private static final String DELETE_MESSAGE = "Esta seguro de eliminar el %s seleccionado? Esta accion no puede deshacerse.";

    private final Grid<E> grid;

    private final CrudEntityPresenter<E> entityPresenter;

    protected abstract String getBasePage();

    protected abstract void setupGrid(Grid<E> grid);

    public AbstractBakeryCrudView(Class<E> beanType, FilterableCrudService<E> service,
                                  Grid<E> grid, CrudEditor<E> editor, CurrentUser currentUser) {
        super(beanType, grid, editor);
        this.grid = grid;
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        CrudI18n crudI18n = CrudI18n.createDefault();
        String entityName = EntityUtil.getName(beanType);
        crudI18n.setNewItem("Nuevo " + entityName);
        crudI18n.setEditItem("Edite " + entityName);
        crudI18n.setEditLabel("Edite " + entityName);
        crudI18n.getConfirm().getCancel().setContent(String.format(DISCARD_MESSAGE, entityName));
        crudI18n.getConfirm().getDelete().setContent(String.format(DELETE_MESSAGE, entityName));
        crudI18n.setDeleteItem("Eliminar");
        setI18n(crudI18n);

        CrudEntityDataProvider<E> dataProvider = new CrudEntityDataProvider<>(service);
        grid.setDataProvider(dataProvider);
        setupGrid(this.grid);
        Crud.addEditColumn(grid);

        entityPresenter = new CrudEntityPresenter<>(service, currentUser, this);

        SearchBar searchBar = new SearchBar();
        searchBar.setActionText("Nuevo " + entityName);
        searchBar.setPlaceHolder("Buscar");
        searchBar.addFilterChangeListener(e -> dataProvider.setFilter(searchBar.getFilter()));
        searchBar.getActionButton().getElement().setAttribute("new-button", true);

        setToolbar(searchBar);
        setupCrudEventListeners(entityPresenter);
    }

    private void setupCrudEventListeners(CrudEntityPresenter<E> entityPresenter) {
        Consumer<E> onSuccess = entity -> navigateToEntity(null);
        Consumer<E> onFail = entity -> {
            throw new RuntimeException("La operacion no pudo ser ejecutada.");
        };

        addEditListener(e ->
                entityPresenter.loadEntity(e.getItem().getId(),
                        entity -> navigateToEntity(entity.getId().toString())));

        addCancelListener(e -> navigateToEntity(null));

        addSaveListener(e ->
                entityPresenter.save(e.getItem(), onSuccess, onFail));

        addDeleteListener(e ->
                entityPresenter.delete(e.getItem(), onSuccess, onFail));
    }

    protected void navigateToEntity(String id) {
        getUI().ifPresent(ui -> ui.navigate(TemplateUtil.generateLocation(getBasePage(), id)));
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter Long id) {
        if (id != null) {
            E item = getEditor().getItem();
            if (item != null && id.equals(item.getId())) {
                return;
            }
            entityPresenter.loadEntity(id, entity -> edit(entity, EditMode.EXISTING_ITEM));
        } else {
            setOpened(false);
        }
    }


}
