package org.sevensource.magnolia.responsivedam.field.upload;

import javax.inject.Inject;

import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.validation.AspectAwareDamUploadFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Field;

import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.form.field.transformer.Transformer;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;

public class AspectAwareDamUploadFieldFactory extends AbstractFieldFactory<AspectAwareDamUploadFieldDefinition, AspectAwareAssetUploadReceiver> {

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadFieldFactory.class);

	private final ResponsiveDamConfiguration responsiveDamConfiguration;
    private final MediaEditorPresenterFactory mediaEditorFactory;
    private final UiContext uiContext;
    private final ImageProvider imageProvider;
    private final ComponentProvider componentProvider;
    private final SimpleTranslator i18n;
    private final I18nizer i18nizer;
    private final ActionbarPresenter actionbarPresenter;

    @Inject
	public AspectAwareDamUploadFieldFactory(ResponsiveDamConfiguration responsiveDamConfiguration, AspectAwareDamUploadFieldDefinition definition,
			Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, ImageProvider imageProvider,
			MediaEditorPresenterFactory mediaEditorFactory, ComponentProvider componentProvider,
			SimpleTranslator i18n, I18nizer i18nizer, ActionbarPresenter actionbarPresenter) {
    	super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);

    	this.responsiveDamConfiguration = responsiveDamConfiguration;

        this.mediaEditorFactory = mediaEditorFactory;
        this.uiContext = uiContext;
        this.imageProvider = imageProvider;
        this.componentProvider = componentProvider;
        this.i18n = i18n;
        this.i18nizer = i18nizer;
        this.actionbarPresenter = actionbarPresenter;
	}


	@Override
	protected Field<AspectAwareAssetUploadReceiver> createFieldComponent() {
		AspectAwareDamUploadField field = new AspectAwareDamUploadField(responsiveDamConfiguration, imageProvider, uiContext, mediaEditorFactory, componentProvider, definition, i18n, i18nizer, actionbarPresenter);
		final String errorMessage = i18n.translate(AspectAwareDamUploadField.aspectsIncompleteErrorTxt);
		field.addValidator(new AspectAwareDamUploadFieldValidator(responsiveDamConfiguration, definition, errorMessage));
		return field;
	}

    @Override
    protected Transformer<?> initializeTransformer(Class<? extends Transformer<?>> transformerClass) {
        return this.componentProvider.newInstance(transformerClass, item, definition, AspectAwareAssetUploadReceiver.class);
    }
}
