package org.sevensource.magnolia.responsivedam.imaging;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import info.magnolia.imaging.OutputFormat;

public class ResponsiveDamOutputFormat {

	private static final OutputFormat OUTPUT_GIF = new OutputFormat("gif", false, 0, "lzw");
	private static final OutputFormat OUTPUT_JPEG = new OutputFormat("jpg", true, 90, null);
	private static final OutputFormat OUTPUT_PNG = new OutputFormat("png", false, 0, null);
	private static final OutputFormat OUTPUT_WEBP_LOSSY = new OutputFormat("webp", false, 80, "Lossy");
	private static final OutputFormat OUTPUT_WEBP_LOSSLESS = new OutputFormat("webp", false, 100, "Lossless");

	private static final List<OutputFormat> DEFAULT = Arrays.asList(OUTPUT_JPEG);

	private static final Map<String, List<OutputFormat>> mimeType2OutputFormats = new HashMap<>();
	
	static {
		mimeType2OutputFormats.put("image/gif", Arrays.asList(OUTPUT_GIF));
		mimeType2OutputFormats.put("image/jpeg", Arrays.asList(OUTPUT_JPEG));
		mimeType2OutputFormats.put("image/png", Arrays.asList(OUTPUT_PNG));
		mimeType2OutputFormats.put("image/tiff", Arrays.asList(OUTPUT_JPEG));
		
		// if we have webp, add it additionally
		if(ImageIO.getImageReadersByMIMEType("image/webp").hasNext()) {
			mimeType2OutputFormats.put("image/webp", Arrays.asList(OUTPUT_JPEG));
			
			if(ImageIO.getImageWritersByMIMEType("image/webp").hasNext()) {
				for(Entry<String, List<OutputFormat>> entry : mimeType2OutputFormats.entrySet()) {
					if(entry.getKey().equals("image/png")) {
						entry.getValue().add(OUTPUT_WEBP_LOSSLESS);
					} else {
						entry.getValue().add(OUTPUT_WEBP_LOSSY);
					}
				}
			}
		}
	}
	
	public static List<OutputFormat> getOutputFormatsByMimeType(String mimeType) {
		final List<OutputFormat> outputFormats = mimeType2OutputFormats.get(mimeType);
		return outputFormats == null ? DEFAULT : outputFormats;
	}
}
