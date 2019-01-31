package org.sevensource.magnolia.responsivedam.focusarea;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareAssetTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.context.MgnlContext;
import info.magnolia.jcr.node2bean.Node2BeanException;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.jcr.util.NodeUtil;

public class FocusAreasUtil {

	private static final Logger logger = LoggerFactory.getLogger(FocusAreasUtil.class);


	private FocusAreasUtil() {}

	public static FocusAreas readFocusAreas(Node parentNode, Node2BeanProcessor node2BeanProcessor) {
		if(parentNode == null) {
			return null;
		}

		try {
			final String nodePath = String.join("/", parentNode.getPath(), AspectAwareAssetTransformer.PROP_FOCUSAREAS);
			final Node node = getNodeFromPath(nodePath, parentNode.getSession().getWorkspace().getName());

			if(node == null) {
				return null;
			}

			final FocusAreas focusAreas = new FocusAreas();

			final Iterable<Node> focusAreaSetsNodes = NodeUtil.getNodes(node);
			for(Node focusAreaSetNode : focusAreaSetsNodes) {
				final String focusAreaSetName = focusAreaSetNode.getName();
				final FocusAreaSet focusAreaSet = new FocusAreaSet();
				focusAreaSet.setName(focusAreaSetName);
				focusAreas.addFocusAreaSet(focusAreaSet);

				final Iterable<Node> focusAreaNodes = NodeUtil.getNodes(focusAreaSetNode);

				for(Node focusAreaNode : focusAreaNodes) {
					final String focusAreaName = focusAreaNode.getName();
					final FocusArea focusArea = readFocusArea(focusAreaNode, node2BeanProcessor);


					if(focusArea.isValid()) {
						focusAreaSet.addFocusArea(focusArea);
					} else {
						if (logger.isInfoEnabled()) {
							logger.info("FocusArea at {}/{}/{} is invalid", parentNode.getPath(), focusAreaSetName, focusAreaName);
						}
					}
				}
			}

			if(! CollectionUtils.isEmpty(focusAreas.getFocusAreaSets()) &&
					! CollectionUtils.isEmpty(focusAreas.getFocusAreaSets().get(0).getFocusAreas())) {
				return focusAreas;
			} else {
				return null;
			}
		} catch(RepositoryException e) {
			logger.error("RepositoryException", e);
			throw new RuntimeException("RepositoryException", e);
		}
	}

	public static FocusArea readFocusArea(Node parentNode, String focusAreaSetName, String focusAreaName, Node2BeanProcessor node2BeanProcessor) {

		if(parentNode == null) {
			return null;
		}

		try {
			final String nodePath = String.join("/", parentNode.getPath(), AspectAwareAssetTransformer.PROP_FOCUSAREAS, focusAreaSetName, focusAreaName);
			final Node node = getNodeFromPath(nodePath, parentNode.getSession().getWorkspace().getName());

			if(node == null) {
				return null;
			}

			return readFocusArea(node, node2BeanProcessor);
		} catch(RepositoryException e) {
			logger.error("RepositoryException", e);
			throw new RuntimeException("RepositoryException", e);
		}
	}

	private static FocusArea readFocusArea(Node focusAreaNode, Node2BeanProcessor node2BeanProcessor) {
		try {
			final FocusArea focusArea = (FocusArea) node2BeanProcessor.toBean(focusAreaNode, FocusArea.class);
			focusArea.setName(focusAreaNode.getName());
			return focusArea;
		} catch(RepositoryException e) {
			logger.error("RepositoryException", e);
			throw new RuntimeException("RepositoryException", e);
		} catch(Node2BeanException n) {
			logger.error("Cannot transform bean", n);
			throw new RuntimeException("Transformation Error", n);
		}
	}


	private static Node getNodeFromPath(String linkPath, String workspace) {
		if(! StringUtils.isEmpty(linkPath)) {
			try {
				return MgnlContext.getJCRSession(workspace).getNode(linkPath);
			} catch(PathNotFoundException p) {
				// that's alright - the item does not have aspects set...
			} catch (RepositoryException e) {
				logger.error("Could not get item from path:", e);
			}
		}

		return null;
	}
}
