package fr.ign.validator.cnig.info;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.model.Document;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Extraction de la géométrie d'un document (valable uniquement pour les
 * documents d'urbanisme)
 * 
 * @author MBorne
 *
 */
public class DocumentGeometryExtractor {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DOCUMENT_GEOMETRY_EXTRACTOR");

	/**
	 * 
	 * @param context
	 * @param document
	 * @return
	 * @throws Exception
	 */
	public static Geometry extractGeometry(Context context, Document document) {
		File dataDirectory = context.getDataDirectory();
		File zoneUrbaFile = new File(dataDirectory, "ZONE_URBA.csv");
		if (!zoneUrbaFile.exists()) {
			return null;
		}

		Geometry union = null;
		try {
			TableReader reader = TableReader.createTableReader(zoneUrbaFile, StandardCharsets.UTF_8);
			int indexGeometry = reader.findColumn("WKT");
			if (indexGeometry < 0) {
				return null;
			}
			WKTReader wktReader = new WKTReader();
			while (reader.hasNext()) {
				String[] row = reader.next();
				String wkt = row[indexGeometry];
				Geometry geom = wktReader.read(wkt);
				if (union == null) {
					union = geom;
				} else {
					union = geom.union(union);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return union;
	}

}
