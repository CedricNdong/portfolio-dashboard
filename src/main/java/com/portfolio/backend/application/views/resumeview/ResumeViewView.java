package com.portfolio.backend.application.views.resumeview;

import com.portfolio.backend.application.data.SampleResume;
import com.portfolio.backend.application.services.SampleResumeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.RolesAllowed;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@PageTitle("ResumeView")
@Menu(icon = "line-awesome/svg/book-solid.svg", order = 3)
@Route(value = "ResumeView/:sampleResumeID?/:action?(edit)")
@RolesAllowed("ADMIN")
public class ResumeViewView extends Div implements BeforeEnterObserver {

    private final String SAMPLERESUME_ID = "sampleResumeID";
    private final String SAMPLERESUME_EDIT_ROUTE_TEMPLATE = "ResumeView/%s/edit";

    private final Grid<SampleResume> grid = new Grid<>(SampleResume.class, false);

    private TextField occupation;
    private DatePicker occupationStart;
    private DatePicker occupationEnd;
    private TextField occupationPlace;
    private TextField city;
    private TextField country;
    private TextField description;
    private TextField specialisation;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<SampleResume> binder;

    private SampleResume sampleResume;

    private final SampleResumeService sampleResumeService;

    public ResumeViewView(SampleResumeService sampleResumeService) {
        this.sampleResumeService = sampleResumeService;
        addClassNames("resume-view-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("occupation").setAutoWidth(true);
        grid.addColumn("occupationStart").setAutoWidth(true);
        grid.addColumn("occupationEnd").setAutoWidth(true);
        grid.addColumn("occupationPlace").setAutoWidth(true);
        grid.addColumn("city").setAutoWidth(true);
        grid.addColumn("country").setAutoWidth(true);
        grid.addColumn("description").setAutoWidth(true);
        grid.addColumn("specialisation").setAutoWidth(true);
        grid.setItems(query -> sampleResumeService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(SAMPLERESUME_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ResumeViewView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SampleResume.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.sampleResume == null) {
                    this.sampleResume = new SampleResume();
                }
                binder.writeBean(this.sampleResume);
                sampleResumeService.update(this.sampleResume);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
                UI.getCurrent().navigate(ResumeViewView.class);
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> sampleResumeId = event.getRouteParameters().get(SAMPLERESUME_ID).map(Long::parseLong);
        if (sampleResumeId.isPresent()) {
            Optional<SampleResume> sampleResumeFromBackend = sampleResumeService.get(sampleResumeId.get());
            if (sampleResumeFromBackend.isPresent()) {
                populateForm(sampleResumeFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested sampleResume was not found, ID = %s", sampleResumeId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ResumeViewView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        occupation = new TextField("Occupation");
        occupationStart = new DatePicker("Occupation Start");
        occupationEnd = new DatePicker("Occupation End");
        occupationPlace = new TextField("Occupation Place");
        city = new TextField("City");
        country = new TextField("Country");
        description = new TextField("Description");
        specialisation = new TextField("Specialisation");
        formLayout.add(occupation, occupationStart, occupationEnd, occupationPlace, city, country, description,
                specialisation);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SampleResume value) {
        this.sampleResume = value;
        binder.readBean(this.sampleResume);

    }
}
