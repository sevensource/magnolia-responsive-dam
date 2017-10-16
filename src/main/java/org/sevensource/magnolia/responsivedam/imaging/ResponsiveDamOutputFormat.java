package org.sevensource.magnolia.responsivedam.imaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import info.magnolia.imaging.OutputFormat;

public class ResponsiveDamOutputFormat {

	private static final OutputFormat OUTPUT_GIF = new OutputFormat("gif", false, 0, "lzw");
	private static final OutputFormat OUTPUT_JPEG = new OutputFormat("jpg", true, 90, null);
	private static final OutputFormat OUTPUT_PNG = new OutputFormat("png", false, 0, null);
	private static final OutputFormat OUTPUT_WEBP_LOSSY = new OutputFormat("webp", false, 80, "Lossy");
	private static final OutputFormat OUTPUT_WEBP_LOSSLESS = new OutputFormat("webp", false, 100, "Lossless");

	private static final List<OutputFormat> DEFAULT = Stream.of(OUTPUT_JPEG).collect(Collectors.toList());

	private static final Map<String, List<OutputFormat>> mimeType2OutputFormats = new HashMap<>();
	
	static {
		addFormat("image/gif", OUTPUT_GIF);
		addFormat("image/jpeg", OUTPUT_JPEG);
		addFormat("image/png", OUTPUT_PNG);
		addFormat("image/tiff", OUTPUT_JPEG);

		
		// if we have webp, add it additionally
		if(ImageIO.getImageReadersByMIMEType("image/webp").hasNext()) {
			addFormat("image/webp", OUTPUT_JPEG);
		}
	}
	
	private static void addFormat(String mimeType, OutputFormat primaryOutputFormat) {
		final List<OutputFormat> formats = new ArrayList<>();
		formats.add(primaryOutputFormat);
		
		if(ImageIO.getImageWritersByMIMEType("image/webp").hasNext()) {
			if(mimeType.equals("image/png")) {
				formats.add(OUTPUT_WEBP_LOSSLESS);
			} else {
				formats.add(OUTPUT_WEBP_LOSSY);
			}
		}
		
		mimeType2OutputFormats.put(mimeType, formats);
	}
	
	public static List<OutputFormat> getOutputFormatsByMimeType(String mimeType) {
		final List<OutputFormat> outputFormats = mimeType2OutputFormats.get(mimeType);
		return outputFormats == null ? DEFAULT : outputFormats;
	}
}
