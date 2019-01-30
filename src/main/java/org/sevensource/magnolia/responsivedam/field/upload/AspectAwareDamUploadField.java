package org.sevensource.magnolia.responsivedam.field.upload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils.InfoLabelStyle;
import org.sevensource.magnolia.responsivedam.field.focusareaselection.FocusAreaSelectionPresenter;
import org.sevensource.magnolia.responsivedam.field.validation.AspectAwareDamUploadFieldValidator;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreaSet;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;

import info.magnolia.dam.app.ui.field.configuration.EditAssetAppConfiguration;
import info.magnolia.dam.app.ui.field.upload.ResurfaceDamUploadField;
import info.magnolia.i18nsystem.I18nizer;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.icons.MagnoliaIcons;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.AlertBuilder;
import info.magnolia.ui.actionbar.ActionbarPresenter;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.overlay.OverlayCloser;
import info.magnolia.ui.dialog.BaseDialogPresenter;
import info.magnolia.ui.dialog.DialogPresenter;
import info.magnolia.ui.dialog.ResurfaceDialogViewImpl;
import info.magnolia.ui.dialog.actionarea.DialogActionExecutor;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.mediaeditor.MediaEditorPresenterFactory;

public class AspectAwareDamUploadField extends ResurfaceDamUploadField<AspectAwareAssetUploadReceiver> {
	/**
	 *
	 */
	private static final long serialVersionUID = -4754770263066766072L;


	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadField.class);


	static final String editAspectsButtonCaption = "field.aspectUpload.caption";
	static final String aspectsIncompleteErrorTxt = "field.aspectUpload.error.incomplete";
	static final String aspectsEmptyWarnTxt = "field.aspectUpload.warn.empty";
	static final String aspectsSetOkTxt = "field.aspectUpload.note.valid";

	private final transient ResponsiveDamConfiguration responsiveDamConfiguration;
	private final transient AspectAwareDamUploadFieldDefinition definition;
	private final transient SimpleTranslator i18n;
	private final transient ComponentProvider componentProvider;
	private final transient I18nizer i18nizer;
	private final transient ActionbarPresenter actionbarPresenter;
	

	private Label infoLabel;


	public AspectAwareDamUploadField(
			ResponsiveDamConfiguration responsiveDamConfiguration,
			ImageProvider imageProvider,
			UiContext uiContext,
			MediaEditorPresenterFactory mediaEditorFactory,
			ComponentProvider componentProvider,
			AspectAwareDamUploadFieldDefinition definition,
			SimpleTranslator i18n,
			I18nizer i18nizer,
			ActionbarPresenter actionbarPresenter) {
		super(imageProvider, uiContext, mediaEditorFactory, componentProvider, definition, i18n);

		this.responsiveDamConfiguration = responsiveDamConfiguration;

		this.definition = definition;
		this.componentProvider = componentProvider;
		this.i18n = i18n;
		this.i18nizer = i18nizer;
		this.actionbarPresenter = actionbarPresenter;

		addValueChangeListener(event -> updateInfoLabel());
		addStyleName("aspect-upload-image-field");
	}
	
    @Override
    protected void addControlActions(CssLayout uploadZone) {
        super.addControlActions(uploadZone);
        
        final EditAssetAppConfiguration editAssetAppConfiguration = getValue().getEditAssetAppConfiguration();
        if (getValue() != null && !getValue().isEmpty() && editAssetAppConfiguration.hasEditConfig()) {
        	uploadZone.addComponent(createEditAspectsButton());
        }
    }
	
//	@Override
//	protected Layout createCompletedActionLayout() {
//		final Layout damLayout = super.createCompletedActionLayout();
//
//		if (!isReadOnly() && getValue() != null &&
//				!getValue().isEmpty() &&
//				getValue().isImage() &&
//				(hasConfiguredVariationSet() || isShowExistingFocusAreas())) {
//
//			Button edit = createEditAspectsButton();
//			this.infoLabel = new Label();
//			this.infoLabel.addStyleName("aspects-info");
//			this.infoLabel.addDetachListener(e -> this.infoLabel = null);
//			updateInfoLabel();
//
//			HorizontalLayout aspectsLayout = new HorizontalLayout();
//			aspectsLayout.setSpacing(true);
//			aspectsLayout.addComponent(edit);
//			aspectsLayout.addComponent(infoLabel);
//
//			VerticalLayout layout = new VerticalLayout();
//			layout.setSpacing(true);
//			layout.addComponent(damLayout);
//			layout.addComponent(aspectsLayout);
//			
//			layout.addStyleName("buttons");
//			damLayout.removeStyleName("buttons");
//
//			return layout;
//        } else {
//        	return damLayout;
//        }
//	}

    private Button createEditAspectsButton() {
        Button editAspectsButton = createControlButton(editAspectsButtonCaption, MagnoliaIcons.CROP_IMAGE);

        editAspectsButton.addClickListener((Button.ClickListener) event -> {
            try {
            	openAspectsEditor();
            } catch (FileNotFoundException e) {
            	logger.error("Cannot open EditAspectsEditor", e);
            	AlertBuilder
            		.alert("ERROR")
            		.withBody(getI18n().translate("dam.assets.uploadField.alert.couldNotOpenMediaEditor"))
            		.withLevel(Notification.Type.ERROR_MESSAGE)
            		.buildAndOpen();
			} finally {
                event.getButton().setEnabled(true);
            }
        });

        editAspectsButton.setDescription(getI18n().translate(editAspectsButtonCaption));
        editAspectsButton.setDisableOnClick(true);
        return editAspectsButton;
    }
    
    private void openAspectsEditor() throws FileNotFoundException {

		final DialogActionExecutor actionExecutor = new DialogActionExecutor(componentProvider);
        final AppContext appContext = componentProvider.getComponent(AppContext.class);


        
        final DialogPresenter dialogPresenter = new BaseDialogPresenter(componentProvider, actionExecutor, new ResurfaceDialogViewImpl(), this.i18nizer, i18n);

        final DialogActionExecutor imageAreaActionExecutor = new DialogActionExecutor(componentProvider);

		final FocusAreas val = getFocusAreas();
		final List<DamVariationSet> damVariationSets = buildShownDamVariationSets(val);


		try(final InputStream inputStream = new FileInputStream(getValue().getFile())) {
			final FocusAreaSelectionPresenter presenter = new FocusAreaSelectionPresenter(actionbarPresenter, dialogPresenter, imageAreaActionExecutor, appContext, i18n);
	        final OverlayCloser overlayCloser = uiContext.openOverlay(presenter.start(inputStream, val, damVariationSets));

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
    
    
    private FocusAreas getFocusAreas() {
		if(! hasFocusAreas(getValue().getFocusAreas())) {
			return new FocusAreas();
		} else {
			return FocusAreas.of(getValue().getFocusAreas());
		}
    }
    
    private List<DamVariationSet> buildShownDamVariationSets(FocusAreas focusAreas) {
    	final List<DamVariationSet> damVariationSets = new ArrayList<>();
    	
		if(! CollectionUtils.isEmpty(definition.getVariationSets())) {
			for(String variationSetName : definition.getVariationSets()) {
				/**
				 * filter invalid variationSets, throw exception if the definition is invalid
				 */
				final DamVariationSet variationSet = responsiveDamConfiguration.getVariationSet(variationSetName);
				if(variationSet == null) {
					throw new IllegalArgumentException("Unknown variationSet with name " + variationSetName);
				}
				damVariationSets.add(variationSet);
			}
		} else if(definition.isUseExistingFocusAreas()) {
			for(FocusAreaSet focusAreaSet : focusAreas.getFocusAreaSets()) {
				/**
				 * filter invalid variationSets, don't throw an exception - the variationSetName might
				 * be an old definition still in JCR
				 */
				final DamVariationSet variationSet = responsiveDamConfiguration.getVariationSet(focusAreaSet.getName());
				if(variationSet != null) {
					damVariationSets.add(variationSet);
				}
			}
		} else {
			throw new IllegalArgumentException("Neither a variationSet is specified nor useExistingFocusAreas");
		}
		
		return damVariationSets;
    }
    
    
    

	private void updateInfoLabel() {
		if(this.infoLabel != null) {

			if(! hasFocusAreas(getValue().getFocusAreas())) {
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
		return
				definition.isUseExistingFocusAreas() &&
				hasFocusAreas(getValue().getFocusAreas());
	}

	private boolean hasConfiguredVariationSet() {
		if(CollectionUtils.isEmpty(definition.getVariationSets())) {
			return false;
		}

		for(String variationSetName : definition.getVariationSets()) {
			if(responsiveDamConfiguration.getVariationSet(variationSetName) == null) {
				throw new IllegalArgumentException("Unknown variationset with name " + variationSetName);
			}
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


	private static boolean hasFocusAreas(FocusAreas focusAreas) {
		return focusAreas != null &&
				! CollectionUtils.isEmpty(focusAreas.getFocusAreaSets()) &&
				! CollectionUtils.isEmpty(focusAreas.getFocusAreaSets().get(0).getFocusAreas());
	}
}
