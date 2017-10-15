package org.sevensource.magnolia.responsivedam.field.focusareaselection.action;

import org.sevensource.magnolia.responsivedam.field.focusareaselection.FocusAreaSelectionPresenter;

import info.magnolia.ui.api.action.Action;
import info.magnolia.ui.api.action.ActionExecutionException;

public class SelectionAction implements Action  {
	
	private final FocusAreaSelectionPresenter presenter;
	private final SelectionActionDefinition definition;
	
	public SelectionAction(SelectionActionDefinition definition, FocusAreaSelectionPresenter presenter) {
		this.presenter = presenter;
		this.definition = definition;
	}
	
	@Override
	public void execute() throws ActionExecutionException {
		presenter.setActiveSelectDefinition(definition.getVariation());
	}
}
