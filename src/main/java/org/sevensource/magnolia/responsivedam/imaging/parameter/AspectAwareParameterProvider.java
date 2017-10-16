package org.sevensource.magnolia.responsivedam.imaging.parameter;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.ResponsiveDamNodeUtil;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreasUtil;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamRendition;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamVariation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.imaging.OutputFormat;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.util.PathSplitter;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;


public class AspectAwareParameterProvider implements ParameterProvider<AspectAwareParameter> {
    
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareParameterProvider.class);
    
    
    private String requestedVariationSet;
    private String requestedVariation;
    private String requestedSize;
    private String requestedWorkspace;
    private String requestedPath;
    private String requestedOutputFormat;


	private final Node contentNode;
	private final Node containerNode;
    
	private final AspectAwareParameter parameter;
    
	
	public AspectAwareParameterProvider(ResponsiveDamRendition rendition, ResponsiveDamConfiguration responsiveDamConfiguration, Node2BeanProcessor node2BeanProcessor) throws RepositoryException {
		
		this.contentNode = ResponsiveDamNodeUtil.getContentNode(rendition.getNode());
		this.containerNode = ResponsiveDamNodeUtil.getContainerNode(contentNode);
		
        final FocusArea focusArea = FocusAreasUtil.readFocusArea(containerNode, rendition.getVariation(), node2BeanProcessor);
        if(focusArea == null) {
        	final String msg = String.format("No FocusArea with name %s defined for asset [%s/%s]", rendition.getVariation(), containerNode.getSession().getWorkspace().getName(), containerNode.getPath());
        	throw new IllegalArgumentException(msg);
        }
        
        final DamVariation variation = responsiveDamConfiguration.getVariation(rendition.getVariationSet(), rendition.getVariation());
        if(variation == null) {
        	final String msg = String.format("No damVariation with name [%s] found for variationSet [%s]", requestedVariation, requestedVariationSet);
        	throw new IllegalArgumentException(msg);
        }
        
		parameter = new AspectAwareParameter(
				rendition.getNode(),
				variation,
				focusArea, 
				rendition.getSize(), 
				rendition.getOutputFormat());
	}
	
    public AspectAwareParameterProvider(String uri, ResponsiveDamConfiguration responsiveDamConfiguration, Node2BeanProcessor node2BeanProcessor) throws RepositoryException {

    	parseRequestedParametersFromUri(uri);
        
        
        this.contentNode = ResponsiveDamNodeUtil.getContentNode(requestedWorkspace, requestedPath);
        this.containerNode = ResponsiveDamNodeUtil.getContainerNode(contentNode);
        

        final DamVariation variation = responsiveDamConfiguration.getVariation(requestedVariationSet, requestedVariation);
        if(variation == null) {
        	final String msg = String.format("No damVariation with name [%s] found for variationSet [%s]", requestedVariation, requestedVariationSet);
        	throw new IllegalArgumentException(msg);
        }

        
        final ResponsiveDamVariation responsiveDamVariation = new ResponsiveDamVariation(contentNode, variation, responsiveDamConfiguration);
        
        final SizeSpecification requestedSizeSpecification = SizeSpecification.of(requestedSize);
        if(! responsiveDamVariation.getSizes().contains(requestedSizeSpecification)) {
        	final String msg = String.format("Requested size [%s] is not in the list of available sizes", requestedSizeSpecification.toString());
        	throw new IllegalArgumentException(msg);
        }
        
        OutputFormat outputFormat = null;
        final List<OutputFormat> outputFormats = responsiveDamVariation.getOutputFormats();
        for(OutputFormat op : outputFormats) {
        	if(op.getFormatName().equals(requestedOutputFormat)) {
        		outputFormat = op;
        		break;
        	}
        }
        
        if(outputFormat == null) {
        	final String msg = String.format("OutputFormat %s is not a valid outputformat", requestedOutputFormat);
        	throw new IllegalArgumentException(msg);
        }
        
        
        final FocusArea focusArea = FocusAreasUtil.readFocusArea(containerNode, requestedVariation, node2BeanProcessor);
        if(focusArea == null) {
        	final String msg = String.format("No FocusArea with name %s defined for asset [%s/%s]", requestedVariation, requestedWorkspace, requestedPath);
        	throw new IllegalArgumentException(msg);
        }
        
        
        parameter = new AspectAwareParameter(contentNode, variation, focusArea, requestedSizeSpecification, outputFormat);
    }
    
    
    private void parseRequestedParametersFromUri(String uri) {
        final PathSplitter splitter = new PathSplitter(uri, false);
        this.requestedVariationSet = splitter.next();
        this.requestedVariation = splitter.next();
        this.requestedSize = splitter.next();
        this.requestedWorkspace = splitter.next();

        String path = splitter.remaining();
        this.requestedOutputFormat = StringUtils.substringAfterLast(
        		StringUtils.substringAfterLast(path, "/"), ".");
        

        this.requestedPath = StringUtils.substringBeforeLast(path, "/");
        
        if(StringUtils.isAnyEmpty(requestedVariationSet, requestedVariation, requestedSize, requestedWorkspace, path, requestedOutputFormat)) {
        	final String msg = String.format("Cannot create %s due to an empty requestedPath part", AspectAwareParameter.class.getName());
        	throw new RuntimeException(msg);
        }
    }
    

    @Override
    public AspectAwareParameter getParameter() {
        return parameter;
    }
}
