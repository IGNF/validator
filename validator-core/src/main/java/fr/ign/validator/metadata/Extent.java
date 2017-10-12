package fr.ign.validator.metadata;

/**
 * Partial implementation of gmd:EX_Extent restricted to a gmd:geographicElement defined as a GeographicBoundingBox 
 * 
 * TODO add temporal extend (fromDate, toDate)
 * 
 * @author MBorne
 *
 */
public class Extent {
	
	private BoundingBox boundingBox ;

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}


}
