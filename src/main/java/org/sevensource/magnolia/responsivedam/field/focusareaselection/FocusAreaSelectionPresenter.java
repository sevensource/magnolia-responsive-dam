package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.field.validation.AbstractAspectAwareFieldValidator;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreaSet;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.actionbar.ActionbarView;
import info.magnolia.ui.actionbar.definition.ActionbarDefinition;
import info.magnolia.ui.api.action.ActionDefinition;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.dialog.DialogView;
import info.magnolia.ui.dialog.actionarea.DialogActionExecutor;
import info.magnolia.ui.dialog.definition.ConfiguredDialogDefinition;
import info.magnolia.ui.mediaeditor.MediaEditorViewImpl;
import info.magnolia.ui.mediaeditor.action.ActionContext;
import info.magnolia.ui.mediaeditor.action.InternalMediaEditorActionDefinition;
import info.magnolia.ui.vaadin.gwt.shared.jcrop.SelectionArea;

public class FocusAreaSelectionPresenter implements FocusAreaSelectedListener {

	private static final Logger logger = LoggerFactory.getLogger(FocusAreaSelectionPresenter.class);

	private final ActionbarPresenter actionbarPresenter;
	private final DialogPresenter dialogPresenter;
	private final DialogActionExecutor actionExecutor;
	private final AppContext appContext;
	private MediaEditorViewImpl view = new MediaEditorViewImpl();
	private final SimpleTranslator i18n;

	private FocusAreas focusAreas;
	private List<DamVariationSet> damVariationSets;
	private FocusAreaSelectionCompletedListener completionListener;

	private FocusAreaSelectionField imageAreaSelectionField;
	private Label leftInfoToolbar;
	private Label rightInfoToolbar;

	private Component defaultAction;

	public FocusAreaSelectionPresenter(ActionbarPresenter actionbarPresenter, DialogPresenter dialogPresenter,
			DialogActionExecutor actionExecutor, AppContext appContext, SimpleTranslator i18n) {
		this.actionbarPresenter = actionbarPresenter;
		this.dialogPresenter = dialogPresenter;
		this.actionExecutor = actionExecutor;
		this.appContext = appContext;
		this.i18n = i18n;
	}

	public void setCompletedListener(FocusAreaSelectionCompletedListener callback) {
		this.completionListener = callback;
	}

	public View start(InputStream is, FocusAreas focusAreas, List<DamVariationSet> damVariationSets) {

		this.focusAreas = focusAreas;
		this.damVariationSets = damVariationSets;

		final Map<String, ActionDefinition> actionbarActions = FocusAreaSelectionUiHelper.buildActionbarActions(damVariationSets);
		final ActionbarDefinition actionbarDefinition = FocusAreaSelectionUiHelper.buildActionbarDefinition(damVariationSets);

		final ActionbarView actionbar = actionbarPresenter.start(actionbarDefinition, actionbarActions);
		actionbarPresenter.setListener(this::executeMediaEditorAction);
		view.setActionBar(actionbar);

		final DialogView dialogView = dialogPresenter.start(new ConfiguredDialogDefinition(), appContext);
		view.setDialog(dialogView);

		view.setToolbar(buildToolbar());

		this.defaultAction = FocusAreaSelectionUiHelper.renderEditorAction(view, getActionContextList());

		try {
			imageAreaSelectionField = new FocusAreaSelectionField(this);
			byte[] img = IOUtils.toByteArray(is);
			imageAreaSelectionField.setValue(img);
			view.setMediaContent(imageAreaSelectionField);
		} catch (IOException e) {
			throw new RuntimeException("IOException during image read", e);
		}

		final ConfiguredDialogDefinition dialogDefinition = new ConfiguredDialogDefinition();
		dialogDefinition.setActions(actionbarActions);
		actionExecutor.setDialogDefinition(dialogDefinition);

		view.asVaadinComponent()
				.addAttachListener(e -> executeMediaEditorAction(FocusAreaSelectionUiHelper.SCALE_TO_FIT));
		return view;
	}

	private void executeMediaEditorAction(String actionName) {
		try {
			actionExecutor.execute(actionName, this, view, focusAreas);
		} catch (ActionExecutionException e) {
			logger.error("Error", e);
		}
	}

	public void setActiveSelectDefinition(DamVariation variation) {
		if (variation != null) {

			FocusArea preSelected = focusAreas.getFocusArea(variation);
			imageAreaSelectionField.setAreaSelectOptions(variation, preSelected);
			leftInfoToolbar.setValue(i18n.translate("field.aspectSelection.info.editing", variation.getName()));
		}
	}

	@Override
	public void onAreaSelected(DamVariation variation, SelectionArea selectedArea) {
		if (variation != null && selectedArea != null) {
			final FocusArea area = new FocusArea(
					variation.getName(),
					selectedArea.getLeft(),
					selectedArea.getTop(),
					selectedArea.getWidth(),
					selectedArea.getHeight());

			final FocusAreaSet focusAreaSet = focusAreas.getOrCreateFocusAreaSet(variation.getVariationSet());
			focusAreaSet.addFocusArea(area);
		}

		updateToolbar();

		if(getMissingAspects().isEmpty()) {
			defaultAction.setEnabled(true);
		}
	}

	private void updateToolbar() {
		final List<String> missing = getMissingAspects()
				.stream()
				.map(i -> FocusAreaSelectionUiHelper.buildDamVariationLabel(i, true))
				.collect(Collectors.toList());

		if(missing.isEmpty()) {
			rightInfoToolbar.removeStyleName("warn");
			rightInfoToolbar.setValue(i18n.translate("field.aspectSelection.info.allAspectsSet"));

		} else {
			rightInfoToolbar.addStyleName("warn");

			if (missing.size() == 1) {
				rightInfoToolbar.setValue(i18n.translate("field.aspectSelection.warn.missing.one", missing.get(0)));
			} else {
				final String and = " " + i18n.translate("field.aspectSelection.warn.missing.many.and") + " ";
				final int last = missing.size() - 1;
				final String fields = String.join(and, String.join(", ", missing.subList(0, last)), missing.get(last));
				final String msg = i18n.translate("field.aspectSelection.warn.missing.many", fields);
				rightInfoToolbar.setValue(msg);
			}
		}
	}

	private void onCompleted(boolean canceled, String actionName) {
		if (logger.isInfoEnabled()) {
			logger.info("Dialog complete with {}", actionName);
		}

		completionListener.completed(canceled, canceled ? null : focusAreas);
	}

	protected List<ActionContext> getActionContextList() {
		List<ActionContext> result = new ArrayList<>();
		result.add(new ActionContext(
				new InternalMediaEditorActionDefinition("save",
						i18n.translate("ui-mediaeditor.internalAction.save.label"), false),
				(String actionName, Object... actionContextParams) -> onCompleted(false, actionName)));

		result.add(new ActionContext(
				new InternalMediaEditorActionDefinition("cancel",
						i18n.translate("ui-mediaeditor.internalAction.cancel.label"), true),
				(String actionName, Object... actionContextParams) -> onCompleted(true, actionName)));
		return result;
	}

	private Component buildToolbar() {
		leftInfoToolbar = new Label();
		leftInfoToolbar.setStyleName("toolbar-left");

		rightInfoToolbar = new Label();
		rightInfoToolbar.setStyleName("toolbar-right");

		HorizontalLayout toolbar = new HorizontalLayout();
		toolbar.setStyleName("focus-area-selection");
		toolbar.addComponent(leftInfoToolbar);
		toolbar.addComponent(rightInfoToolbar);

		toolbar.setSizeFull();
		toolbar.setSpacing(true);

		updateToolbar();
		return toolbar;
	}

	private List<DamVariation> getMissingAspects() {
		return AbstractAspectAwareFieldValidator.getMissingVariations(damVariationSets, focusAreas);
	}
}
