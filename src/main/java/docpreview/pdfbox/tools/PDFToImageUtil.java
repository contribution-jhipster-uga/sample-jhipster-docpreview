/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package docpreview.pdfbox.tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Convert a PDF document to an image.
 */
@Service
public final class PDFToImageUtil {
	
	public static final String MIME_PDF = "application/pdf";
	public static final String EXTENSION_PDF = "pdf";

	private final Logger log = LoggerFactory.getLogger(PDFToImageUtil.class);

	/**
	 * convert the pages of the document into JPEG images files.
	 *
	 * @param pdfByte      the byte array of the document.
	 * @param outputPrefix the prefix name of the JPEG image files.
	 * @param dpi          the image resolution.
	 * @param quality      the image quality. (use 1.0f for default)
	 *
	 * @return the number of pages
	 * @throws IllegalArgumentException If there is an argument is illegal.
	 * @throws IOException              If there is an error parsing the document.
	 */
	public int converPDFToImage(byte[] pdfByte, String outputPrefix, int dpi, float quality)
			throws IllegalArgumentException, IOException {
		return converPDFToImage(pdfByte, outputPrefix, dpi, quality, 1, Integer.MAX_VALUE);
	}

	/**
	 * convert some pages of the document into JPEG images files.
	 *
	 * @param pdfByte      the byte array of the document.
	 * @param outputPrefix the prefix name of the JPEG image files.
	 * @param dpi          the image resolution.
	 * @param quality      the image quality. (use 1.0f for default)
	 * @param startPage    the start page number.
	 * @param endPage      the end page number.
	 *
	 * @throws IllegalArgumentException If there is an argument is illegal.
	 * @throws IOException              If there is an error parsing the document.
	 */
	public int converPDFToImage(byte[] pdfByte, String outputPrefix, int dpi, float quality, int startPage, int endPage)
			throws IllegalArgumentException, IOException {
		String password = "";
		PDDocument document = null;
		document = PDDocument.load(pdfByte, password);
		return converPDFToImage(document, outputPrefix, dpi, quality, startPage, endPage);
	}

	/**
	 * convert some pages of the document into JPEG images files.
	 *
	 * @param document     the PDDocument of the document.
	 * @param outputPrefix the prefix name of the JPEG image files.
	 * @param dpi          the image resolution.
	 * @param quality      the image quality. (use 1.0f for default)
	 *
	 * @throws IllegalArgumentException If there is an argument is illegal.
	 * @throws IOException              If there is an error parsing the document.
	 */
	public int converPDFToImage(PDDocument document, String outputPrefix, int dpi, float quality)
			throws IllegalArgumentException, IOException {
		return converPDFToImage(document, outputPrefix, dpi, quality, 1, Integer.MAX_VALUE);
	}

	/**
	 * convert some pages of the document into JPEG images files.
	 *
	 * @param document     the PDDocument of the document.
	 * @param outputPrefix the prefix name of the JPEG image files.
	 * @param dpi          the image resolution.
	 * @param quality      the image quality. (use 1.0f for default)
	 * @param startPage    the start page number.
	 * @param endPage      the end page number.
	 *
	 * @throws IllegalArgumentException If there is an argument is illegal.
	 * @throws IOException              If there is an error parsing the document.
	 */
	// @Override
	public int converPDFToImage(PDDocument document, String outputPrefix, int dpi, float quality, int startPage,
			int endPage) throws IllegalArgumentException, IOException {

		final String imageFormat = "jpg";
		final String color = "rgb";
		final float cropBoxLowerLeftX = 0;
		final float cropBoxLowerLeftY = 0;
		final float cropBoxUpperRightX = 0;
		final float cropBoxUpperRightY = 0;
		final boolean showTime = true;
		final boolean subsampling = false;

		int count = 0;

		PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
		if (acroForm != null && acroForm.getNeedAppearances()) {
			acroForm.refreshAppearances();
		}

		ImageType imageType = null;
		if ("bilevel".equalsIgnoreCase(color)) {
			imageType = ImageType.BINARY;
		} else if ("gray".equalsIgnoreCase(color)) {
			imageType = ImageType.GRAY;
		} else if ("rgb".equalsIgnoreCase(color)) {
			imageType = ImageType.RGB;
		} else if ("rgba".equalsIgnoreCase(color)) {
			imageType = ImageType.ARGB;
		}

		if (imageType == null) {
			log.error("Error: Invalid color.");
			throw new IllegalArgumentException("Invalid color.");
		}

		// if a CropBox has been specified, update the CropBox:
		// changeCropBoxes(PDDocument document,float a, float b, float c,float d)
		if (cropBoxLowerLeftX != 0 || cropBoxLowerLeftY != 0 || cropBoxUpperRightX != 0 || cropBoxUpperRightY != 0) {
			changeCropBox(document, cropBoxLowerLeftX, cropBoxLowerLeftY, cropBoxUpperRightX, cropBoxUpperRightY);
		}

		long startTime = System.nanoTime();

		// render the pages
		boolean success = true;
		endPage = Math.min(endPage, document.getNumberOfPages());
		PDFRenderer renderer = new PDFRenderer(document);
		renderer.setSubsamplingAllowed(subsampling);
		for (int i = startPage - 1; i < endPage; i++) {
			log.debug("renderImageWithDPI image={}", i);
			BufferedImage image = renderer.renderImageWithDPI(i, dpi, imageType);
			String fileName = outputPrefix + (i + 1) + "." + imageFormat;
			success &= ImageIOUtil.writeImage(image, fileName, dpi, quality);
			
			// TODO add SHA-1 file fileName + ".sha1"
		}

		// performance stats
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		count = 1 + endPage - startPage;
		if (showTime) {
			log.info("Rendered %d page%s in %dms\n", count, count == 1 ? "" : "s", duration / 1000000);
		}

		if (!success) {
			log.error("Error: no writer found for image format '" + imageFormat + "'");
			throw new IOException("No writer found for image format '" + imageFormat + "'");
		}

		return count;
	}

	/**
	 * convert some pages of the document into JPEG images.
	 *
	 * @param pddocument     the PDDocument of the document.
	 * @param outputPrefix the prefix name of the JPEG image files.
	 * @param dpi          the image resolution.
	 * @param quality      the image quality. (use 1.0f for default)
	 * @param startPage    the start page number.
	 * @param endPage      the end page number.
	 *
	 * @throws IllegalArgumentException If there is an argument is illegal.
	 * @throws IOException              If there is an error parsing the document.
	 */
	public List<byte[]> converPDFToImageByteArray(PDDocument pddocument, int dpi, float quality, int startPage,
			int endPage) throws IllegalArgumentException, IOException {

		final String imageFormat = "jpg";
		ImageType imageType = ImageType.RGB;
		final boolean showTime = true;
		final boolean subsampling = false;

		PDAcroForm acroForm = pddocument.getDocumentCatalog().getAcroForm();
		if (acroForm != null && acroForm.getNeedAppearances()) {
			acroForm.refreshAppearances();
		}

		// render the pages
		long startTime = System.nanoTime();
		boolean success = true;
		endPage = Math.min(endPage, pddocument.getNumberOfPages());
		PDFRenderer renderer = new PDFRenderer(pddocument);
		renderer.setSubsamplingAllowed(subsampling);
		List<byte[]> lba = new ArrayList<byte[]>();
		for (int i = startPage - 1; i < endPage; i++) {
			BufferedImage image = renderer.renderImageWithDPI(i, dpi, imageType);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				success &= ImageIOUtil.writeImage(image, imageFormat, baos, dpi, quality);
				lba.add(baos.toByteArray());
			} catch (IllegalArgumentException | IOException e) {
				log.error("Error while ImageIOUtil.writeImage", e);
				throw e;
			} finally {
				baos.close();
			}
		}

		// performance stats
		long endTime = System.nanoTime();
		long duration = endTime - startTime;
		int count = 1 + endPage - startPage;
		if (showTime) {
			log.info("Rendered %d page%s in %dms\n", count, count == 1 ? "" : "s", duration / 1000000);
		}

		if (!success) {
			log.error("Error: no writer found for image format '" + imageFormat + "'");
			throw new IOException("No writer found for image format '" + imageFormat + "'");
		}

		return lba;
	}

	private static void changeCropBox(PDDocument document, float a, float b, float c, float d) {
		for (PDPage page : document.getPages()) {
			PDRectangle rectangle = new PDRectangle();
			rectangle.setLowerLeftX(a);
			rectangle.setLowerLeftY(b);
			rectangle.setUpperRightX(c);
			rectangle.setUpperRightY(d);
			page.setCropBox(rectangle);
		}
	}
}
