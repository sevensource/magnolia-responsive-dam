package org.sevensource.magnolia.responsivedam.field.link;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.validation.AspectAwareDamLinkFieldValidator;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Field;

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.app.AppController;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.dialog.registry.DialogDefinitionRegistry;
import info.magnolia.ui.form.field.LinkField;
import info.magnolia.ui.form.field.factory.LinkFieldFactory;

public class AspectAwareDamLinkFieldFactory extends LinkFieldFactory<AspectAwareDamLinkFieldDefinition> {

	private final FormDialogPresenterFactory formDialogPresenterFactory;
	private final UiContext uiContext;
	private final DialogDefinitionRegistry dialogDefinitionRegistry;
	private final Node2BeanProcessor node2BeanProcessor;
	private final SimpleTranslator i18n;

	private final ResponsiveDamConfiguration responsiveDamConfiguration;
	private final AspectAwareDamLinkFieldDefinition fieldDefinition;

	@Inject
	public AspectAwareDamLinkFieldFactory(ResponsiveDamConfiguration responsiveDamConfiguration,
			AspectAwareDamLinkFieldDefinition definition, Item relatedFieldItem, UiContext uiContext,
			I18NAuthoringSupport i18nAuthoringSupport, AppController appController, ComponentProvider componentProvider,
			FormDialogPresenterFactory formDialogPresenterFactory,
			final DialogDefinitionRegistry dialogDefinitionRegistry, SimpleTranslator i18n,
			Node2BeanProcessor node2BeanProcessor) {

		super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, appController, componentProvider);

		this.responsiveDamConfiguration = responsiveDamConfiguration;
		this.fieldDefinition = definition;

		this.formDialogPresenterFactory = formDialogPresenterFactory;
		this.uiContext = uiContext;
		this.dialogDefinitionRegistry = dialogDefinitionRegistry;
		this.node2BeanProcessor = node2BeanProcessor;
		this.i18n = i18n;
	}

	@Override
	protected Field<String> createFieldComponent() {
		final LinkField linkField = (LinkField) super.createFieldComponent();

		final List<DamVariationSet> variationSets = new ArrayList<>();
		for (String variationSetName : definition.getVariationSets()) {
			final DamVariationSet variationSet = responsiveDamConfiguration.getVariationSet(variationSetName);
			variationSets.add(variationSet);
		}

		final AspectAwareDamLinkField field = new AspectAwareDamLinkField(linkField, formDialogPresenterFactory,
				dialogDefinitionRegistry, uiContext, i18n);

		field.setWorkspace(fieldDefinition.getTargetWorkspace());
		field.setAspectsAppName(fieldDefinition.getAspectsAppName());
		field.setVariationSets(variationSets);

		final String errorMessage = i18n.translate(AspectAwareDamLinkField.aspectsIncompleteErrorTxt);
		AspectAwareDamLinkFieldValidator validator = new AspectAwareDamLinkFieldValidator(responsiveDamConfiguration,
				definition, node2BeanProcessor, errorMessage);
		validator.setWorkspace(fieldDefinition.getTargetWorkspace());

		field.addValidator(validator);

		return field;
	}
}
