package org.sevensource.magnolia.responsivedam.imaging.operation;

import javax.jcr.Binary;
import javax.jcr.RepositoryException;

import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;

import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.load.AbstractFromNode;

public class FromAspectAwareBinaryNode extends AbstractFromNode<AspectAwareParameter> {

	@Override
	protected Binary getBinaryFromNode(ParameterProvider<AspectAwareParameter> param) throws RepositoryException {
		final AspectAwareParameter aspectAwareParameter = param.getParameter();
		return aspectAwareParameter.getBinary();
	}
}