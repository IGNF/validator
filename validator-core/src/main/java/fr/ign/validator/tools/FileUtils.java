package fr.ign.validator.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import fr.ign.validator.tools.filter.FileByExtensionAndDirectoryFilter;

/**
 * 
 * @see http://techblog.sharpmind.de/?p=228
 */
public class FileUtils {

	/**
	 * Finds files and directories within a given directory (and optionally its
	 * subdirectories). All files/dirs found are filtered by an IOFileFilter.
	 *
	 * @param directory  the directory to search in.
	 * @param fileFilter the filter to apply to files and directories.
	 * @param dirFilter  in which dirs the algorithm should traverse
	 * @return the list of found file objects
	 */
	public static Collection<File> listFilesAndDirs(File directory,
	                                         IOFileFilter fileFilter,
	                                         IOFileFilter dirFilter) {
	   Collection<File> files = new ArrayList<File>();

	   File[] found = directory.listFiles();
	   if (found != null) {
	      for (File file : found) {
	         if (fileFilter.accept(file)) {
	            files.add(file);
	         }

	         if (file.isDirectory() && dirFilter.accept(file)) {
	            files.addAll(listFilesAndDirs(file, fileFilter, dirFilter));
	         }
	      }
	   }

	   return files;
	}
	
	public static Collection<File> listFilesAndDirs(File directory, String[] allowedExtensions){
		return listFilesAndDirs(directory, 
			new FileByExtensionAndDirectoryFilter(allowedExtensions),
			TrueFileFilter.INSTANCE
		) ;
	}
	
}
