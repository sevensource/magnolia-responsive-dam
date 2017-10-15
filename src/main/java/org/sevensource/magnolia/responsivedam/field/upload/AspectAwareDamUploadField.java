package org.sevensource.magnolia.responsivedam.field.upload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.ResponsiveDamModule;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils.InfoLabelStyle;
import org.sevensource.magnolia.responsivedam.field.focusareaselection.FocusAreaSelectionPresenter;
import org.sevensource.magnolia.responsivedam.field.validation.AspectAwareDamUploadFieldValidator;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

import info.magnolia.dam.app.ui.field.upload.DamUploadField;
import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.overlay.OverlayCloser;
import info.magnolia.ui.dialog.BaseDialogPresenter;
import info.magnolia.ui.dialog.BaseDialogViewImpl;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.dialog.actionarea.DialogActionExecutor;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

@StyleSheet("vaadin://responsive-dam/aspect-aware-dam-upload-field.css")
public class AspectAwareDamUploadField extends DamUploadField<AspectAwareAssetUploadReceiver> {
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadField.class);

	
	static final String editAspectsButtonCaption = "field.aspectUpload.caption";
	static final String aspectsIncompleteErrorTxt = "field.aspectUpload.error.incomplete";
	static final String aspectsEmptyWarnTxt = "field.aspectUpload.warn.empty";
	static final String aspectsSetOkTxt = "field.aspectUpload.note.valid";
	
	private final transient ResponsiveDamModule responsiveDamModule;
	private final transient AspectAwareDamUploadFieldDefinition definition;
	private final transient SimpleTranslator i18n;
	private final transient ComponentProvider componentProvider;
	private final transient I18nizer i18nizer;
	private final transient ActionbarPresenter actionbarPresenter;
	
	
	private Label infoLabel;
	
	
	public AspectAwareDamUploadField(ResponsiveDamModule responsiveDamModule, 
			ImageProvider imageProvider, UiContext uiContext,
			MediaEditorPresenterFactory mediaEditorFactory, ComponentProvider componentProvider,
			AspectAwareDamUploadFieldDefinition definition, SimpleTranslator i18n, I18nizer i18nizer,
			ActionbarPresenter actionbarPresenter) {
		super(imageProvider, uiContext, mediaEditorFactory, componentProvider, definition, i18n);
		
		this.responsiveDamModule = responsiveDamModule;
		
		this.definition = definition;
		this.componentProvider = componentProvider;
		this.i18n = i18n;
		this.i18nizer = i18nizer;
		this.actionbarPresenter = actionbarPresenter;
		
		addValueChangeListener(event -> updateInfoLabel());
		addStyleName("aspect-upload-image-field");
	}
	
	@Override
	protected Layout createCompletedActionLayout() {
		final Layout damLayout = super.createCompletedActionLayout();
		
        if (!isReadOnly() && getValue() != null && 
        		!getValue().isEmpty() && 
        		getValue().isImage() &&
        		(hasConfiguredVariationSet() || isShowExistingFocusAreas())) {
        	
            Button edit = createEditAspectsButton();
            this.infoLabel = new Label();
            this.infoLabel.addStyleName("aspects-info");
            this.infoLabel.addDetachListener(e -> this.infoLabel = null);
            updateInfoLabel();
            
            HorizontalLayout aspectsLayout = new HorizontalLayout();
            aspectsLayout.setSpacing(true);
            aspectsLayout.addComponent(edit);
            aspectsLayout.addComponent(infoLabel);
            
            VerticalLayout layout = new VerticalLayout();
            layout.setSpacing(true);
            layout.addComponent(damLayout);
            layout.addComponent(aspectsLayout);
            
            layout.addStyleName("buttons");
            damLayout.removeStyleName("buttons");
            
            return layout;
        } else {
        	return damLayout;
        }
	}
	
    private Button createEditAspectsButton() {
        Button editButton = new Button(i18n.translate(editAspectsButtonCaption), (event) -> {
            try {
            	openAspectsEditor();
            } catch (FileNotFoundException fnfe) {
                logger.warn("could not open EditAspectsEditor");
                uiContext.openAlert(MessageStyleTypeEnum.ERROR, "ERROR", "Could not open EditAspectsEditor",
                        "ok", null);
            } finally {
                event.getButton().setEnabled(true);
            }
        });
        editButton.setDisableOnClick(true);
        return editButton;
    }
	
	private void updateInfoLabel() {
		if(this.infoLabel != null) {
			if((getValue().getFocusAreas() == null || MapUtils.isEmpty(getValue().getFocusAreas().getAreas()))) {
				if(hasConfiguredVariationSet()) {
					AspectAwareUiUtils.updateInfoLabel(this.infoLabel, i18n.translate(aspectsEmptyWarnTxt), InfoLabelStyle.WARN);
				} else {
					AspectAwareUiUtils.updateInfoLabel(this.infoLabel, "", InfoLabelStyle.OK);			
				}
			} else if(! isFocusAreaSelectionComplete()) {
				AspectAwareUiUtils.updateInfoLabel(this.infoLabel, i18n.translate(aspectsIncompleteErrorTxt), InfoLabelStyle.ERROR);
			} else {
				AspectAwareUiUtils.updateInfoLabel(this.infoLabel, i18n.translate(aspectsSetOkTxt), InfoLabelStyle.OK);
			}
		}
	}
	
	
	private boolean isShowExistingFocusAreas() {
		return getValue().getFocusAreas() != null && 
				! MapUtils.isEmpty(getValue().getFocusAreas().getAreas()) &&
				definition.isUseExistingFocusAreas();
	}
	
	private boolean hasConfiguredVariationSet() {
		if(StringUtils.isEmpty(definition.getVariationSet())) {
			return false;
		}
		
    	if(responsiveDamModule.getConfiguredVariationSet(definition.getVariationSet()) == null) {
    		throw new IllegalArgumentException("Unknown variationset with name " + definition.getVariationSet());
    	}
    	
    	return true;
	}
	

	private boolean isFocusAreaSelectionComplete() {
		final AspectAwareDamUploadFieldValidator validator = getAspectValidator();
		if(validator == null) {
			return true;
		}
		return validator.isValid(getValue());
	}
	
	private AspectAwareDamUploadFieldValidator getAspectValidator() {
		return
				(AspectAwareDamUploadFieldValidator) getValidators()
					.stream()
					.filter(v -> v instanceof AspectAwareDamUploadFieldValidator)
					.findFirst()
					.orElse(null);
	}
    
    private void openAspectsEditor() throws FileNotFoundException {
    	if (logger.isInfoEnabled()) {
			logger.info("Setting aspects");
		}
    	
		final DialogActionExecutor actionExecutor = new DialogActionExecutor(componentProvider);
        final AppContext appContext = componentProvider.getComponent(AppContext.class);
        
        
        final DialogPresenter dialogPresenter = new BaseDialogPresenter(componentProvider, actionExecutor, new BaseDialogViewImpl(), this.i18nizer, i18n);
        
        final DialogActionExecutor imageAreaActionExecutor = new DialogActionExecutor(componentProvider);
				
		final FocusAreas val;
		if((getValue().getFocusAreas() == null || MapUtils.isEmpty(getValue().getFocusAreas().getAreas()))) {
			val = new FocusAreas();
		} else {
			val = FocusAreas.of(getValue().getFocusAreas());
		}
		
		final DamVariationSet damVariationSet;
		if(! StringUtils.isEmpty(definition.getVariationSet())) {
			damVariationSet = responsiveDamModule.getConfiguredVariationSet(definition.getVariationSet());
		} else if(definition.isUseExistingFocusAreas()) {
			damVariationSet = new DamVariationSet(null);
			
			for(String areaName : val.getAreas().keySet()) {
				final DamVariation variation = responsiveDamModule.getAnyConfiguredVariation(areaName);
				if(variation != null) {
					damVariationSet.addVariation(variation);
				}
			}
		} else {
			throw new IllegalArgumentException("Neither a variationSet is specified nor useExistingFocusAreas");
		}
		
		try(final InputStream inputStream = new FileInputStream(getValue().getFile())) {
			final FocusAreaSelectionPresenter presenter = new FocusAreaSelectionPresenter(actionbarPresenter, dialogPresenter, imageAreaActionExecutor, appContext, i18n);
	        final OverlayCloser overlayCloser = uiContext.openOverlay(presenter.start(inputStream, val, damVariationSet));
	        
	        presenter.setCompletedListener((isCanceled,focusAreas) -> {
	        	
				if(! isCanceled) {
					getValue().setFocusArea(focusAreas);
					getPropertyDataSource().setValue(getValue());
				}
	        	
	        	overlayCloser.close();
			});
		} catch (IOException e) {
			logger.error("error while closing inputStream", e);
		}
    }
}
