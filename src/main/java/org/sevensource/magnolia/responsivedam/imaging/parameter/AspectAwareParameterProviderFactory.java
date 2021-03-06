package org.sevensource.magnolia.responsivedam.imaging.parameter;

import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamCachingStrategy;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamRendition;

import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.ParameterProviderFactory;
import info.magnolia.imaging.caching.CachingStrategy;
import info.magnolia.jcr.node2bean.Node2BeanProcessor;
import info.magnolia.objectfactory.ComponentProvider;


public class AspectAwareParameterProviderFactory<T> implements ParameterProviderFactory<T, AspectAwareParameter> {

	private final Node2BeanProcessor node2BeanProcessor;
	private final ComponentProvider componentProvider;
	private final AtomicReference<ResponsiveDamConfiguration> responsiveDamReference = new AtomicReference<>(null);

	@Inject
	public AspectAwareParameterProviderFactory(ComponentProvider componentProvider, Node2BeanProcessor node2BeanProcessor) {
		this.componentProvider = componentProvider;
		this.node2BeanProcessor = node2BeanProcessor;
	}

	@Override
	public ParameterProvider<AspectAwareParameter> newParameterProviderFor(T request) {
		if(request instanceof HttpServletRequest) {
			return newParameterProviderForServletRequest((HttpServletRequest) request);
		} else if(request instanceof ResponsiveDamRendition) {
			return newParameterProviderForRendition((ResponsiveDamRendition) request);
		} else {
			throw new IllegalArgumentException("Don't know how to handle request of type " + request.getClass().getName());
		}
	}


	private ParameterProvider<AspectAwareParameter> newParameterProviderForServletRequest(HttpServletRequest request) {
		// remove generator name
		String pathInfo = request.getPathInfo();
		if(pathInfo.startsWith("/")) {
			pathInfo = pathInfo.substring(1, pathInfo.length());
		}
		final String uri = StringUtils.substringAfter(pathInfo, "/");

		try {
			return new AspectAwareParameterProvider(uri, getResponsiveDamConfiguration(), node2BeanProcessor);
		} catch (RepositoryException e) {
			throw new RuntimeException(String.format("Can't create a %s object for URI [%s]", AspectAwareParameterProvider.class.getName(), uri), e);
		}
	}

	private ParameterProvider<AspectAwareParameter> newParameterProviderForRendition(ResponsiveDamRendition rendition) {
		try {
			return new AspectAwareParameterProvider(rendition, getResponsiveDamConfiguration(), node2BeanProcessor);
		} catch (RepositoryException e) {
			throw new RuntimeException(String.format("Can't create a %s object for Rendition [%s]", AspectAwareParameterProvider.class.getName(), rendition), e);
		}
	}

	private ResponsiveDamConfiguration getResponsiveDamConfiguration() {
		ResponsiveDamConfiguration responsiveDamConfiguration = responsiveDamReference.get();
		if(responsiveDamConfiguration == null) {
			responsiveDamConfiguration = componentProvider.getComponent(ResponsiveDamConfiguration.class);
			responsiveDamReference.compareAndSet(null, responsiveDamConfiguration);
			return responsiveDamReference.get();
		} else {
			return responsiveDamConfiguration;
		}
	}

	@Override
	public CachingStrategy<AspectAwareParameter> getCachingStrategy() {
		return new ResponsiveDamCachingStrategy();
	}
}
