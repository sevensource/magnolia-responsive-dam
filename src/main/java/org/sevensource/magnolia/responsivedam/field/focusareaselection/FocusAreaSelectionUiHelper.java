package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.field.focusareaselection.action.SelectionAction;
import org.sevensource.magnolia.responsivedam.field.focusareaselection.action.SelectionActionDefinition;

import com.vaadin.ui.Component;

import info.magnolia.ui.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.actionbar.definition.ActionbarItemDefinition;
import info.magnolia.ui.actionbar.definition.ConfiguredActionbarDefinition;
import info.magnolia.ui.actionbar.definition.ConfiguredActionbarGroupDefinition;
import info.magnolia.ui.actionbar.definition.ConfiguredActionbarItemDefinition;
import info.magnolia.ui.actionbar.definition.ConfiguredActionbarSectionDefinition;
import info.magnolia.ui.api.action.Action;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.action.ConfiguredActionDefinition;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.dialog.actionarea.renderer.ActionRenderer;
import info.magnolia.ui.dialog.actionarea.renderer.DefaultEditorActionRenderer;
import info.magnolia.ui.mediaeditor.MediaEditorView;
import info.magnolia.ui.mediaeditor.action.ActionContext;
import info.magnolia.ui.mediaeditor.action.definition.ScaleToActualSizeActionDefinition;
import info.magnolia.ui.mediaeditor.action.definition.ScaleToFitActionDefinition;
import info.magnolia.ui.mediaeditor.action.feature.ScaleToActualSizeAction;
import info.magnolia.ui.mediaeditor.action.feature.ScaleToFitAction;

public class FocusAreaSelectionUiHelper {
	
	private static final String SCALE_TO_ACTUAL = "scaleToActual";
	static final String SCALE_TO_FIT = "scaleToFit";
	private static final String SELECT_PREFIX = "select_";
	
	private FocusAreaSelectionUiHelper() {}
	
	
	static Map<String, ActionDefinition> buildActionbarActions(DamVariationSet damVariationSet) {
		Map<String, ActionDefinition> actions = new HashMap<>();
		
		actions.put(SCALE_TO_ACTUAL, buildActionDefinition("Zoom to actual size", "icon-view-in-actual-size", ScaleToActualSizeAction.class, new ScaleToActualSizeActionDefinition()));
		actions.put(SCALE_TO_FIT, buildActionDefinition("Zoom to fit", "icon-zoom-to-fit", ScaleToFitAction.class, new ScaleToFitActionDefinition()));
		
		for(DamVariation variation : damVariationSet.getVariations()) {
			final SelectionActionDefinition sad = new SelectionActionDefinition(variation);
			actions.put(SELECT_PREFIX + variation.getName(), 
					buildActionDefinition(variation.getName(), "icon-view", SelectionAction.class, sad));
		}
		
		return actions;
	}
	
	private static ActionDefinition buildActionDefinition(String name, String icon, Class<? extends Action> implementationClass, ConfiguredActionDefinition definition) {
		definition.setName(name);
		definition.setLabel(name);
		definition.setIcon(icon);
		definition.setImplementationClass(implementationClass);
		return definition;
	}
	
	
	///////////////
	
	static ActionbarDefinition buildActionbarDefinition(DamVariationSet damVariationSet) {
		ConfiguredActionbarDefinition definition = new ConfiguredActionbarDefinition();
		ConfiguredActionbarSectionDefinition section = new ConfiguredActionbarSectionDefinition();
		section.setName("imageAreaSelect");
		section.setLabel("Area of interests");
		definition.addSection(section);
		ConfiguredActionbarGroupDefinition aspectsGroup = new ConfiguredActionbarGroupDefinition();
		aspectsGroup.setName("aspects");
		ConfiguredActionbarGroupDefinition utilityGroup = new ConfiguredActionbarGroupDefinition();
		utilityGroup.setName("utility");
		section.addGroup(aspectsGroup);
		section.addGroup(utilityGroup);

		utilityGroup.addItem(buildActionbarItemDefinition(SCALE_TO_FIT));
		utilityGroup.addItem(buildActionbarItemDefinition(SCALE_TO_ACTUAL));
		
		for(DamVariation spec : damVariationSet.getVariations()) {
			aspectsGroup.addItem(buildActionbarItemDefinition(SELECT_PREFIX + spec.getName()));
		}
		
		return definition;
	}
	
	private static ActionbarItemDefinition buildActionbarItemDefinition(String name) {
		ConfiguredActionbarItemDefinition item = new ConfiguredActionbarItemDefinition();
		item.setName(name);
		return item;
	}
	
	////
	
	static Component renderEditorAction(MediaEditorView view, List<ActionContext> actionContexts) {
		Component defaultAction = null;
		
		Iterator<ActionContext> it = actionContexts.iterator();
		boolean defaultIsSet = false;
		while (it.hasNext()) {
			ActionContext action = it.next();
			ActionRenderer actionRenderer = new DefaultEditorActionRenderer();
			View actionView = actionRenderer.start(action.getDefinition(), action.getListener());
			view.getDialog().getActionAreaView().addPrimaryAction(actionView, action.getDefinition().getName());
			if (!defaultIsSet) {
				defaultAction = actionView.asVaadinComponent();
				defaultAction.addStyleName("default");
				defaultAction.setEnabled(false);
				defaultIsSet = true;
			}
		}
		
		return defaultAction;
	}
	

}
