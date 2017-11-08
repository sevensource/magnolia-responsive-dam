package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.link.AspectAwareDamLinkFieldDefinition;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreasUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.field.converter.IdentifierToPathConverter;

public class AspectAwareDamLinkFieldValidator extends AbstractAspectAwareFieldValidator<String> {

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamLinkFieldValidator.class);

	private final IdentifierToPathConverter identifierToPathConverter;
	private final transient Node2BeanProcessor node2BeanProcessor;
	private String workspace;

	public AspectAwareDamLinkFieldValidator(ResponsiveDamConfiguration responsiveDamConfiguration, AspectAwareDamLinkFieldDefinition fieldDefinition, Node2BeanProcessor node2BeanProcessor, String errorMessage) {
		super(errorMessage);

		final List<DamVariationSet> damVariationSets = transformDamVariationSets(responsiveDamConfiguration, fieldDefinition.getVariationSets());
		setDamVariationSets(damVariationSets);

		this.identifierToPathConverter = fieldDefinition.getIdentifierToPathConverter();
		this.node2BeanProcessor = node2BeanProcessor;
	}


	public boolean isImage(String value) {
		if(value == null) {
			return true;
		}

		final Node node = getNodeFromIdentifier(value);
		return isImage(node);
	}

	private boolean isImage(Node node) {
		final String mimeType = PropertyUtil.getString(AssetNodeTypes.AssetResource.getResourceNodeFromAsset(node), AssetNodeTypes.AssetResource.MIMETYPE, "");
		return StringUtils.startsWith(mimeType, "image/");
	}

	@Override
	protected boolean isValidValue(String value) {
		if(value == null) {
			return true;
		}

		final Node node = getNodeFromIdentifier(value);

		if(! isImage(node)) {
			return true;
		}

		final FocusAreas focusAreas = FocusAreasUtil.readFocusAreas(node, node2BeanProcessor);
		return isValidFocusAreas(focusAreas);
	}

	private Node getNodeFromIdentifier(String value) {
		final String fieldValue = identifierToPathConverter.convertToPresentation(value, String.class, null);
		return getNodeFromPath(fieldValue);
	}

	private Node getNodeFromPath(String linkPath) {
		if(! StringUtils.isEmpty(linkPath)) {
			try {
				return MgnlContext.getJCRSession(getWorkspace()).getNode(linkPath);
			} catch(PathNotFoundException p) {
				// that's alright - the item does not have aspects set...
			} catch (RepositoryException e) {
				logger.error("Could not get item from path:", e);
			}
		}

		return null;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getWorkspace() {
		return workspace;
	}


	@Override
	public Class<String> getType() {
		return String.class;
	}
}
