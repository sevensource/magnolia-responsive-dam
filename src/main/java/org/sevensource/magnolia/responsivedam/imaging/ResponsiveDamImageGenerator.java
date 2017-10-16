package org.sevensource.magnolia.responsivedam.imaging;

import org.sevensource.magnolia.responsivedam.imaging.operation.AspectAwareCrop;
import org.sevensource.magnolia.responsivedam.imaging.operation.AspectAwareResample;
import org.sevensource.magnolia.responsivedam.imaging.operation.FromAspectAwareBinaryNode;
import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.imaging.OutputFormat;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperationChain;

public class ResponsiveDamImageGenerator extends ImageOperationChain<ParameterProvider<AspectAwareParameter>> {

	private static final Logger logger = LoggerFactory.getLogger(ResponsiveDamImageGenerator.class);
	

    public static final String GENERATOR_NAME = "rd";
    
    public ResponsiveDamImageGenerator() {
    	super();
    	getOperations().add(new FromAspectAwareBinaryNode());
    	getOperations().add(new AspectAwareCrop());
    	getOperations().add(new AspectAwareResample());
    	setName(GENERATOR_NAME);
	}

    @Override
    public OutputFormat getOutputFormat(ParameterProvider<AspectAwareParameter> params) {
    	return params.getParameter().getOutputFormat();
    }
}
