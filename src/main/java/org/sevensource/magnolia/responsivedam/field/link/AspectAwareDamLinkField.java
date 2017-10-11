package org.sevensource.magnolia.responsivedam.field.link;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils;
import org.sevensource.magnolia.responsivedam.field.AspectAwareUiUtils.InfoLabelStyle;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition;
import org.sevensource.magnolia.responsivedam.field.validation.AspectAwareDamLinkFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.StyleSheet;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import info.magnolia.config.registry.DefinitionProvider;
import info.magnolia.config.registry.Registry.InvalidDefinitionException;
import info.magnolia.config.registry.Registry.NoSuchDefinitionException;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.api.PathAwareAssetProvider.PathNotFoundException;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.DialogView;
import info.magnolia.ui.dialog.definition.DialogDefinition;
import info.magnolia.ui.dialog.definition.FormDialogDefinition;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenter;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.dialog.registry.DialogDefinitionRegistry;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.form.definition.TabDefinition;
import info.magnolia.ui.form.field.LinkField;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

@StyleSheet("vaadin://responsive-dam/aspect-aware-dam-link-field.css")
public class AspectAwareDamLinkField extends CustomField<String> {

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamLinkField.class);

	private String workspace;
	private String aspectsAppName;
	
	
	static final String editAspectsButtonCaption = "field.aspectLink.caption";
	static final String aspectsIncompleteErrorTxt = "field.aspectLink.error.incomplete";
	static final String aspectsSetOkTxt = "field.aspectLink.note.valid";
	
	
	private final LinkField linkField;
	private final transient FormDialogPresenterFactory formDialogPresenterFactory;
	private final transient UiContext uiContext;
	private final transient DialogDefinitionRegistry dialogDefinitionRegistry;
	private final transient SimpleTranslator i18n;
	
	private transient DamVariationSet variationSet;

	private DialogView damDialog = null;
	
	private HorizontalLayout infoLayout = null;
	private Label infoLabel = null;
	
	public AspectAwareDamLinkField(LinkField linkField, FormDialogPresenterFactory formDialogPresenterFactory, DialogDefinitionRegistry dialogDefinitionRegistry, UiContext uiContext, SimpleTranslator i18n) {
		this.linkField = linkField;
		this.formDialogPresenterFactory = formDialogPresenterFactory;
		this.uiContext = uiContext;
		this.dialogDefinitionRegistry = dialogDefinitionRegistry;
		this.i18n = i18n;
		
		linkField.addValueChangeListener(event -> onValueChange()); 
		
		addStyleName("aspectaware-linkfield");
	}
	
	@Override
	public void setPropertyDataSource(Property newDataSource) {
		super.setPropertyDataSource(newDataSource);
		linkField.setPropertyDataSource(newDataSource);
	}
	
	private void onValueChange() {
		
		if(StringUtils.isEmpty(getValue()) && infoLayout != null) {
			infoLayout.setVisible(false);
			return;
		} else if(infoLayout != null) {
			infoLayout.setVisible(true);
		}
		
		final AspectAwareDamLinkFieldValidator validator = (AspectAwareDamLinkFieldValidator)
				getValidators()
			.stream()
			.filter(v -> v instanceof AspectAwareDamLinkFieldValidator)
			.findFirst()
			.orElse(null);
		
		if(validator != null) {
			if(! validator.isImage(getValue())) {
				infoLayout.setVisible(false);
			} else if(validator.isValid(getValue())) {
				AspectAwareUiUtils.updateInfoLabel(this.infoLabel, i18n.translate(aspectsSetOkTxt), InfoLabelStyle.OK);
			} else {
				AspectAwareUiUtils.updateInfoLabel(this.infoLabel, i18n.translate(aspectsIncompleteErrorTxt), InfoLabelStyle.ERROR);
			}
		}
	}
	
	
	private void closeDialog() {
		if(damDialog != null) {
			damDialog.close();
			damDialog = null;
		}
		
		onValueChange();
	}
	
	
	@Override
	protected Component initContent() {

		this.infoLayout = new HorizontalLayout();
		infoLayout.setSpacing(true);
		
		final Button editButton = createEditAspectsButton();
		infoLayout.addComponent(editButton);
		
		
        this.infoLabel = new Label();
        this.infoLabel.addStyleName("aspects-info");
        this.infoLabel.addDetachListener(e -> this.infoLabel = null);
        infoLayout.addComponent(this.infoLabel);
        onValueChange();
        
		
		final VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.addComponent(linkField);
		layout.addComponent(infoLayout);
		
		return layout;
	}
	
    private Button createEditAspectsButton() {
        Button editButton = new Button(i18n.translate(editAspectsButtonCaption), event -> {
            try {
            	openEditor();
            } finally {
                event.getButton().setEnabled(true);
            }
        });
        editButton.setDisableOnClick(true);
        return editButton;
    }
	
	private void openEditor() {
		final String value = linkField.getTextField().getValue();
		final Node node = getNodeFromPath(value);
		if(node == null) {
			throw new IllegalArgumentException("Cannot resolve node with value " + value);
		}
		
		final JcrItemAdapter item = new JcrNodeAdapter(node);

		
		final EditorCallback callback = new EditorCallback() {
			@Override
			public void onSuccess(String actionName) {
				closeDialog();
			}
			
			@Override
			public void onCancel() {
				closeDialog();
			}
		};
		
		
		FormDialogDefinition dialogDefinition;
        try {
            DefinitionProvider<DialogDefinition> dialogDefinitionProvider = dialogDefinitionRegistry.getProvider(getAspectsAppName());
            dialogDefinition = (FormDialogDefinition) dialogDefinitionProvider.get();
        } catch (NoSuchDefinitionException | InvalidDefinitionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

		final FormDialogPresenter formDialogPresenter = 
				formDialogPresenterFactory.createFormDialogPresenter(getAspectsAppName());

		
        for(TabDefinition tabDefinition : dialogDefinition.getForm().getTabs()) {
        	for(FieldDefinition fieldDefinition : tabDefinition.getFields()) {
        		if(fieldDefinition instanceof AspectAwareDamUploadFieldDefinition) {
        			((AspectAwareDamUploadFieldDefinition)fieldDefinition).setVariation(variationSet.getName());
        		}
        	}
        }
		
		this.damDialog = formDialogPresenter.start(item, dialogDefinition, uiContext, callback);
	}
	
	private Node getNodeFromPath(String linkPath) {
		if(! StringUtils.isEmpty(linkPath)) {
			try {
				return MgnlContext.getJCRSession(getWorkspace()).getNode(linkPath);
			} catch(PathNotFoundException p) {
				if (logger.isDebugEnabled()) {
					logger.debug("No node found at {}", linkPath);
				}
			} catch (RepositoryException e) {
				logger.error("Could not get item from path:", e);
			}
		}
		
		return null;
	}

	
	public void setVariationSet(DamVariationSet variationSet) {
		this.variationSet = variationSet;
	}
	
	public String getWorkspace() {
		return workspace;
	}
	
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}
	
	public String getAspectsAppName() {
		return aspectsAppName;
	}
	
	public void setAspectsAppName(String aspectsAppName) {
		this.aspectsAppName = aspectsAppName;
	}
	
	@Override
	public Class<? extends String> getType() {
		return String.class;
	}
}
