package org.sevensource.magnolia.responsivedam.field.validation;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.ResponsiveDamModule;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.field.FocusArea;
import org.sevensource.magnolia.responsivedam.field.FocusAreas;
import org.sevensource.magnolia.responsivedam.field.link.AspectAwareDamLinkFieldDefinition;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareAssetTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.jcr.node2bean.Node2BeanException;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.ui.form.field.converter.IdentifierToPathConverter;

public class AspectAwareDamLinkFieldValidator extends AbstractAspectAwareFieldValidator<String> {
	
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamLinkFieldValidator.class);
	
	private final IdentifierToPathConverter identifierToPathConverter;
	private final transient Node2BeanProcessor node2BeanProcessor;
	private String workspace;
	
	public AspectAwareDamLinkFieldValidator(ResponsiveDamModule responsiveDamModule, AspectAwareDamLinkFieldDefinition fieldDefinition, Node2BeanProcessor node2BeanProcessor, String errorMessage) {
		super(errorMessage);
		final DamVariationSet damVariationSet = responsiveDamModule.getConfiguredVariation(fieldDefinition.getVariation());
		setDamVariationSet(damVariationSet);
		
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
		
		final FocusAreas focusAreas = readFocusAreas(node);
		return isValidFocusAreas(focusAreas);
	}
	
	private Node getNodeFromIdentifier(String value) {
		final String fieldValue = identifierToPathConverter.convertToPresentation(value, String.class, null);
		return getNodeFromPath(fieldValue);
	}
	
	private FocusAreas readFocusAreas(Node parentNode) {
		if(parentNode == null) {
			return null;
		}
		
		try {
			final String nodePath = String.join("/", parentNode.getPath(), AspectAwareAssetTransformer.PROP_ASPECTS);
			final Node node = getNodeFromPath(nodePath);
			
			if(node == null) {
				return null;
			}
			
			final FocusAreas focusAreas = new FocusAreas();
			
			Iterable<Node> aspectNodes = NodeUtil.getNodes(node);
			for(Node aspectNode : aspectNodes) {
				final FocusArea area = (FocusArea) node2BeanProcessor.toBean(aspectNode, FocusArea.class);
				final String focusName = aspectNode.getName();
				
				if(area != null && area.isValid()) {
					focusAreas.addArea(focusName, area);					
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("FocusArea at {}/{} is invalid", parentNode.getPath(), focusName);
					}
				}
			}
			
			if(focusAreas.getAreas() != null && focusAreas.getAreas().size() > 0) {
				return focusAreas;
			} else {
				return null;
			}
		} catch(RepositoryException e) {
			logger.error("RepositoryException", e);
			throw new RuntimeException("RepositoryException", e);
		} catch(Node2BeanException n) {
			logger.error("Cannot transform bean", n);
			throw new RuntimeException("Transformation Error", n);
		}
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
