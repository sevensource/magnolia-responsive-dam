package org.sevensource.magnolia.responsivedam;

import java.util.regex.Pattern;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.JcrConstants;

import info.magnolia.context.MgnlContext;

public class ResponsiveDamNodeUtil {
	private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}", Pattern.CASE_INSENSITIVE);

	private ResponsiveDamNodeUtil() {}

	public static Node getContentNode(String workspace, String path) throws RepositoryException {
		final Node node;
		if(IDENTIFIER_PATTERN.matcher(path).matches()) {
			node = MgnlContext.getJCRSession(workspace).getNodeByIdentifier(path);
		} else {
			node = MgnlContext.getJCRSession(workspace).getNode("/" + path);
		}

		return getContentNode(node);
	}

	public static Node getContentNode(Node node) throws RepositoryException {
		if(node.getName().equals(JcrConstants.JCR_CONTENT)) {
			return node;
		} else {
			NodeIterator it = node.getNodes(JcrConstants.JCR_CONTENT);
			if(! it.hasNext()) {
				final String msg = String.format("Node %s has no child named %s", node.getPath(), JcrConstants.JCR_CONTENT);
				throw new IllegalArgumentException(msg);
			}
			return it.nextNode();
		}
	}

	public static Node getContainerNode(Node node) throws RepositoryException {
		if(node.getName().equals(JcrConstants.JCR_CONTENT)) {
			return node.getParent();
		} else {
			return node;
		}
	}
}
