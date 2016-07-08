package fr.ign.validator.tools.filter;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class FileByExtensionAndDirectoryFilter implements IOFileFilter {

	private IOFileFilter filter ;
	
	public FileByExtensionAndDirectoryFilter( String[] allowedExtensions ){
		if ( allowedExtensions == null) {
            filter = TrueFileFilter.INSTANCE;
        } else {
            String[] suffixes = toSuffixes(allowedExtensions);
            filter = new SuffixFileFilter(suffixes);
        }
	}
	
	@Override
	public boolean accept(File file) {
		if ( file.isDirectory() ){
			return true ;
		}
		return filter.accept(file);
	}

	@Override
	public boolean accept(File dir, String name) {
		if ( name.equals("validation") || name.equals("archive")){
			return false ;
		}else{
			return true ;
		}
	}

	
	/**
     * Converts an array of file extensions to suffixes for use
     * with IOFileFilters.
     *
     * @param extensions  an array of extensions. Format: {"java", "xml"}
     * @return an array of suffixes. Format: {".java", ".xml"}
     */
    private static String[] toSuffixes(String[] extensions) {
        String[] suffixes = new String[extensions.length];
        for (int i = 0; i < extensions.length; i++) {
            suffixes[i] = "." + extensions[i];
        }
        return suffixes;
    }
}
