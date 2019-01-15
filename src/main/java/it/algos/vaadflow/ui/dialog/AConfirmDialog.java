/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package it.algos.vaadflow.ui.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;

import java.io.Serializable;
import java.util.function.Consumer;

import static it.algos.vaadflow.application.FlowCost.BOT_BACK;


/**
 * A generic dialog for confirming or cancelling an action.
 *
 * @param <T> The type of the action's subject
 */
public class AConfirmDialog<T extends Serializable> extends Dialog {

    private final H2 titleField = new H2();

    private final Div messageLabel = new Div();

    private final Div extraMessageLabel = new Div();

    private final Button confirmButton = new Button();

    //    private final Button cancelButton = new Button("Back", new Icon("lumo", "back"));
    private final Button cancelButton = new Button(BOT_BACK);

    private Registration registrationForConfirm;

    private Registration registrationForCancel;


    /**
     * Constructor.
     */
    public AConfirmDialog() {
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);

        getElement().getClassList().add("confirm-dialog");
        confirmButton.addClickListener(e -> close());
        confirmButton.getElement().setAttribute("theme", "primary");
        confirmButton.setAutofocus(true);
        cancelButton.addClickListener(e -> close());
        cancelButton.getElement().setAttribute("theme", "primary");

        HorizontalLayout buttonBar = new HorizontalLayout( cancelButton, confirmButton);
        buttonBar.setClassName("confirm-dialog-buttons");

        Div labels = new Div(messageLabel, extraMessageLabel);
        labels.setClassName("confirm-text");

        titleField.setClassName("confirm-dialog-heading");

        add(titleField, labels, buttonBar);

        this.addOpenedChangeListener(event -> {
            if (!this.isOpened()) {
                this.getElement().removeFromParent();
            }
        });
    }


    /**
     * Opens the confirmation dialog.
     * <p>
     * The dialog will display the given title and message(s), then call
     * <code>confirmHandler</code> if the Confirm button is clicked, or
     * <code>cancelHandler</code> if the Cancel button is clicked.
     *
     * @param title             The title text
     * @param message           Detail message (optional, may be empty)
     * @param additionalMessage Additional message (optional, may be empty)
     * @param actionName        The action name to be shown on the Confirm button
     * @param isDisruptive      True if the action is disruptive, such as deleting an item
     * @param item              The subject of the action
     * @param confirmHandler    The confirmation handler function
     * @param cancelHandler     The cancellation handler function
     */
    public void open(String title, String message, String additionalMessage,
                     String actionName, boolean isDisruptive, T item, Consumer<T> confirmHandler,
                     Runnable cancelHandler) {
        titleField.setText(title);
        messageLabel.setText(message);
        extraMessageLabel.setText(additionalMessage);
        confirmButton.setText(actionName);

        if (registrationForConfirm != null) {
            registrationForConfirm.remove();
        }
        registrationForConfirm = confirmButton
                .addClickListener(e -> confirmHandler.accept(item));
        if (registrationForCancel != null) {
            registrationForCancel.remove();
        }
        registrationForCancel = cancelButton
                .addClickListener(e -> cancelHandler.run());
        if (isDisruptive) {
            confirmButton.getElement().setAttribute("theme", "error");
        }
        open();
    }

}
