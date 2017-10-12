package fr.ign.validator.metadata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.jackson.serializer.BoundingBoxSerializer;

/**
 * 
 * Implementation of gmd:EX_GeographicBoundingBox
 * 
 * Note that coordinates are stored as string to allow validation reporting
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_EX_GeographicBoundingBox.html">gmd:EX_GeographicBoundingBox</a>
 * 
 * @author MBorne
 *
 */
@JsonSerialize(using=BoundingBoxSerializer.class)
public class BoundingBox {

	private String westBoundLongitude ;
	
	private String eastBoundLongitude ;
	
	private String southBoundLatitude ;
	
	private String northBoundLatitude ;

	public String getWestBoundLongitude() {
		return westBoundLongitude;
	}

	public void setWestBoundLongitude(String westBoundLongitude) {
		this.westBoundLongitude = westBoundLongitude;
	}

	public String getEastBoundLongitude() {
		return eastBoundLongitude;
	}

	public void setEastBoundLongitude(String eastBoundLongitude) {
		this.eastBoundLongitude = eastBoundLongitude;
	}

	public String getSouthBoundLatitude() {
		return southBoundLatitude;
	}

	public void setSouthBoundLatitude(String southBoundLatitude) {
		this.southBoundLatitude = southBoundLatitude;
	}

	public String getNorthBoundLatitude() {
		return northBoundLatitude;
	}

	public void setNorthBoundLatitude(String northBoundLatitude) {
		this.northBoundLatitude = northBoundLatitude;
	}
	
	/**
	 * Test if bounding box is valid
	 * @return
	 */
	public boolean isValid() {
		return ! Double.isNaN(safeStringToDouble(westBoundLongitude))
			&& ! Double.isNaN(safeStringToDouble(eastBoundLongitude))
			&& ! Double.isNaN(safeStringToDouble(southBoundLatitude))
			&& ! Double.isNaN(safeStringToDouble(northBoundLatitude))
		;
	}
	

	@Override
	public String toString() {
		return westBoundLongitude+","+southBoundLatitude+","+eastBoundLongitude+","+northBoundLatitude;
	}

	/**
	 * Safe conversion to JTS Envelope
	 * @return
	 */
	public Envelope toEnvelope(){
		return new Envelope(
			safeStringToDouble(westBoundLongitude), safeStringToDouble(eastBoundLongitude), 
			safeStringToDouble(southBoundLatitude), safeStringToDouble(northBoundLatitude) 
		);
	}

	private double safeStringToDouble(String value){
		if ( value == null ){
			return Double.NaN;
		}
		try {
			return Double.valueOf(value);
		}catch(NumberFormatException e){
			return Double.NaN;
		}
	}


	
}

