package org.sevensource.magnolia.responsivedam.configuration;

import java.util.ArrayList;
import java.util.List;

import info.magnolia.imaging.OutputFormat;

public class OutputFormatMapping {
	private final String name;
	private final String sourceMimeType;
	private final List<OutputFormat> outputFormats = new ArrayList<>(2);
	
	public OutputFormatMapping(String name, String sourceMimeType) {
		this.name = name;
		this.sourceMimeType = sourceMimeType;
	}
	
	public List<OutputFormat> getOutputFormats() {
		return outputFormats;
	}
	
	public void addOutputFormat(OutputFormat outputFormat) {
		this.outputFormats.add(outputFormat);
	}
	
	public String getSourceMimeType() {
		return sourceMimeType;
	}
	
	public String getName() {
		return name;
	}
}
