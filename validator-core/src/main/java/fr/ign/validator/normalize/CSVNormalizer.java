package fr.ign.validator.normalize;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Normalize CSV file according to FeatureType
 * 
 * @author MBorne
 *
 */
public class CSVNormalizer implements Closeable {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("CSVNormalizer") ;

	/**
	 * Context providing StringFixer
	 */
	private Context context;

	/**
	 * Target FeatureType
	 */
	private FeatureType featureType;

	/**
	 * Target CRS
	 */
	private CoordinateReferenceSystem targetCRS;
	
	/**
	 * CSV writer
	 */
	private CSVPrinter printer;
	

	public CSVNormalizer(Context context, FeatureType featureType, File targetFile) throws IOException {
		this.context = context;
		this.featureType = featureType;
		try {
			this.targetCRS = CRS.decode("CRS:84");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		/* init CSV with a given header */
		String[] outputHeader = featureType.getAttributeNames();
		BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
		printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180);
		printer.printRecord(outputHeader);
	}
	

	/**
	 * Append rows corresponding to a document file
	 * @param documentFile
	 * @throws Exception
	 */
	public void append(File csvFile) throws Exception {
		ProjectionTransform transform = new ProjectionTransform(
			context.getCoordinateReferenceSystem(), 
			targetCRS
		);

		TableReader reader = TableReader.createTableReaderPreferedCharset(
			csvFile, 
			context.getEncoding()
		);

		/*
		 * writing each feature
		 */
		String[] inputHeader = reader.getHeader();
		while (reader.hasNext()) {
			String[] inputRow = reader.next();
			String[] outputRow = new String[featureType.getAttributeCount()];
			for (int i = 0; i < inputRow.length; i++) {
				int position = featureType.indexOf(inputHeader[i]);
				if (position < 0) {
					continue;
				}
				// binding
				AttributeType<?> attribute = featureType.getAttribute(position);
				Object bindedValue = null;
				try {
					bindedValue = attribute.bind(inputRow[i]);
					if (bindedValue instanceof Geometry) {
						bindedValue = transform.transform((Geometry) bindedValue);
					}
				} catch (IllegalArgumentException e) {
					log.warn(MARKER, "{}.{} : {} transformé en valeur nulle (type non valide)", inputRow[i],
							featureType.getName(), attribute.getName());
				}
				// formatting
				String outputValue = attribute.formatObject(bindedValue);
				outputValue = context.getStringFixer().transform(outputValue);
				outputRow[position] = outputValue;
			}
			printer.printRecord(outputRow);
		}
	}

	@Override
	public void close() throws IOException {
		printer.close();
	}
	
	
	

}
