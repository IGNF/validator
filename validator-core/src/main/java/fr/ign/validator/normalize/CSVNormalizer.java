package fr.ign.validator.normalize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
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
import fr.ign.validator.exception.InvalidCharsetException;
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
public class CSVNormalizer {
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

	private CoordinateReferenceSystem targetCRS;

	public CSVNormalizer(Context context, FeatureType featureType) {
		this.context = context;
		this.featureType = featureType;
		try {
			this.targetCRS = CRS.decode("CRS:84");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Normalize sourceFile with a given sourceCharset and sourceCRS to an UTF-8 encoded targetFile with "CRS:84" coordinates
	 * @param sourceFile
	 * @param sourceCharset
	 * @param sourceCRS
	 * @param targetFile
	 * @throws Exception
	 */
	public void normalize(
		File sourceFile, 
		Charset sourceCharset, 
		CoordinateReferenceSystem sourceCRS,
		File targetFile
	) throws Exception {
		ProjectionTransform transform = new ProjectionTransform(sourceCRS, targetCRS);

		/*
		 * Creating a temp file containing normalized csv {{destFile}}.csv
		 */
		TableReader reader = null;
		try {
			reader = TableReader.createTableReader(sourceFile, sourceCharset);
		} catch (InvalidCharsetException e1) {
			log.error(MARKER, "Détection de la charset pour la normalisation de {}", sourceFile);
			reader = TableReader.createTableReaderDetectCharset(sourceFile);
		}

		String[] inputHeader = reader.getHeader();

		String[] outputHeader = featureType.getAttributeNames();
		BufferedWriter fileWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
		CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180);
		printer.printRecord(outputHeader);

		/*
		 * writing each feature
		 */
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

		printer.close();
	}

}
