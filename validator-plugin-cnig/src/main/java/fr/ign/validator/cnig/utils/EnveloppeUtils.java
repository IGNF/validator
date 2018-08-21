package fr.ign.validator.cnig.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Manipulates Bounding Boxes (envelope or bbox)
 * 
 * @author MBorne
 *
 */
public class EnveloppeUtils {
	
	/**
	 * Reads bbox from a shapefile
	 * 
	 * @param shpFile
	 * @return
	 * @throws Exception
	 */
	public static Envelope getBoundingBox(File shpFile) {
		/*
		 * DataStore opening
		 */
		Envelope bbox = null;
		Map<String, URL> map = new HashMap<String, URL>();
		try {
			map.put("url", shpFile.toURI().toURL());
			DataStore dataStore = DataStoreFinder.getDataStore( map );
			SimpleFeatureSource featureSource = dataStore.getFeatureSource( dataStore.getTypeNames()[0] );    
			dataStore.dispose();
			bbox = featureSource.getFeatures().getBounds();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		if ( isNullEnvelope(bbox) ){
			return new Envelope();
		}else{
			return bbox;
		}
	}
	
	/**
	 * Formats bbox as string : "xmin,ymin,xmax,ymax"
	 * 
	 * @param env
	 * @return
	 */
	public static String format(Envelope env){
		if ( null == env || isNullEnvelope(env) ){
			return "";
		}
		return formatDouble(env.getMinX())
			+","+formatDouble(env.getMinY())
			+","+formatDouble(env.getMaxX())
			+","+formatDouble(env.getMaxY())
		;
	}
	
	/**
	 * Format double
	 * @param value
	 * @return
	 */
	private static String formatDouble(double value){
		return String.format(Locale.ROOT, "%.7f",value);
	}

	/**
	 * Indicates if bbox is null
	 * 
	 * @param env
	 * @return
	 */
	private static boolean isNullEnvelope(Envelope env){
		Envelope zero = new Envelope(0.0,0.0,0.0,0.0);
		if ( env.isNull() ){
			return true ;
		}else if (env.equals(zero) ){
			return true ;
		}else{
			return false;
		}
	}

	
}
