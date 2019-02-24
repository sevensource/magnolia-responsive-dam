package org.sevensource.magnolia.responsivedam.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.magnolia.imaging.OutputFormat;


public class OutputFormatMapping {
	private final String name;
	private final String sourceMimeType;
	private final List<OutputFormat> outputFormats = new ArrayList<>(2);
	
	private static final List<String> PREFERRED_FORMAT_ORDER = Arrays.asList("webp");
	

	public OutputFormatMapping(String name, String sourceMimeType) {
		this.name = name;
		this.sourceMimeType = sourceMimeType;
	}

	public List<OutputFormat> getOutputFormats() {
		return outputFormats;
	}
	
	private static int getOrderForOutputFormat(OutputFormat outputFormat) {
		int index = PREFERRED_FORMAT_ORDER.indexOf(outputFormat.getFormatName().toLowerCase());
		return index < 0 ? Integer.MAX_VALUE : index;
	}

	public void addOutputFormat(OutputFormat outputFormat) {
		this.outputFormats.add(outputFormat);
		this.outputFormats.sort( (a,b) -> {
			
			int orderA = getOrderForOutputFormat(a);
			int orderB = getOrderForOutputFormat(b);
			
			if(orderA == orderB) {
				return a.getFormatName().compareToIgnoreCase(b.getFormatName());
			} else {
				return Integer.compare(orderA, orderB);
			}
		});
	}

	public String getSourceMimeType() {
		return sourceMimeType;
	}

	public String getName() {
		return name;
	}
}
