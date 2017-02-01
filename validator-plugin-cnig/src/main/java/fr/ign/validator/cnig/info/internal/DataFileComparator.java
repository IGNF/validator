package fr.ign.validator.cnig.info.internal;

import java.util.Comparator;

import fr.ign.validator.cnig.info.DataFile;

public class DataFileComparator implements Comparator<DataFile> {

	@Override
	public int compare(DataFile o1, DataFile o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
