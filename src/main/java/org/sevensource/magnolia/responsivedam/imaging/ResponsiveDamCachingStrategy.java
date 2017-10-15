package org.sevensource.magnolia.responsivedam.imaging;

import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.sevensource.magnolia.responsivedam.ResponsiveDamNodeUtil;
import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;

import info.magnolia.imaging.ImageGenerator;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.caching.CachingStrategy;
import info.magnolia.jcr.util.NodeTypes;

/**
 * {@link CachingStrategy} for {@link AspectAwareParameter}.
 */
public class ResponsiveDamCachingStrategy implements CachingStrategy<AspectAwareParameter> {

    @Override
    public String getCachePath(ImageGenerator<ParameterProvider<AspectAwareParameter>> generator, ParameterProvider<AspectAwareParameter> parameterProvider) {
        return parameterProvider.getParameter().getCachePath();
    }

    @Override
    public boolean shouldRegenerate(Property cachedBinary, ParameterProvider<AspectAwareParameter> parameterProvider) throws RepositoryException {
        final Calendar cacheLastMod = NodeTypes.LastModified.getLastModified(cachedBinary.getParent().getParent());
        
        final Node contentNode = ResponsiveDamNodeUtil.getContentNode(parameterProvider.getParameter().getNode());
        final Node containerNode = ResponsiveDamNodeUtil.getContainerNode(parameterProvider.getParameter().getNode());
        
        final Calendar srcLastMod = NodeTypes.LastModified.getLastModified(contentNode);
        final Calendar srcLastMod2 = NodeTypes.LastModified.getLastModified(containerNode);
        return cacheLastMod.before(srcLastMod) && cacheLastMod.before(srcLastMod2);
    }
}
