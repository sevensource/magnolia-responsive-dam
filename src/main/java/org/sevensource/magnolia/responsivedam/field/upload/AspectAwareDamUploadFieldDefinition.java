package org.sevensource.magnolia.responsivedam.field.upload;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.sevensource.magnolia.responsivedam.ResponsiveDamModule;
import org.sevensource.magnolia.responsivedam.field.model.ConfiguredAspectDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.dam.app.ui.field.definition.DamUploadFieldDefinition;
import info.magnolia.ui.form.field.transformer.Transformer;

public class AspectAwareDamUploadFieldDefinition extends DamUploadFieldDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadFieldDefinition.class);
	
	//private Map<String, ConfiguredAspectDefinition> aspects;
	private String variation;
	
	public AspectAwareDamUploadFieldDefinition() {
		super();
		setTransformerClass((Class<? extends Transformer<?>>) (Object) AspectAwareAssetTransformer.class);
	}
	
	public String getVariation() {
		return variation;
	}
	
	public void setVariation(String variation) {
		this.variation = variation;
	}
	
//	public Map<String, ConfiguredAspectDefinition> getAspects() {
//		return aspects;
//	}
//	
//	public void setAspects(Map<String, ConfiguredAspectDefinition> aspects) {
//		this.aspects = aspects;
//	}
//	
//	public Map<String, ConfiguredAspectDefinition> getRequiredAspects() {
//		if(aspects == null || aspects.isEmpty()) {
//			return Collections.emptyMap();
//		} else {
//			return aspects.entrySet()
//					.stream()
//					.filter(e -> e.getValue().isRequired())
//					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));			
//		}
//	}

}
