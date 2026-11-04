package fr.ign.validator.info;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.info.model.TableStats;

/**
 * Stats of the normalized CSV files (feature count and bounding box) computed
 * while they are written (avoid to read them again to build
 * document-info.json).
 *
 * The stats are only returned if the file is unchanged (size, last modification
 * time and file key), so that the files rewritten by post-processes (ex :
 * DocUrbaFilter) are read again.
 *
 * @author FCerizay
 *
 */
public class TableStatsCache {

    private static class Entry {
        private TableStats stats;
        private long size;
        private FileTime lastModifiedTime;
        private Object fileKey;
    }

    private Map<File, Entry> entries = new HashMap<>();

    /**
     * Save the stats of a file in its current state
     *
     * @param file
     * @param stats
     */
    public void put(File file, TableStats stats) {
        File key = file.getAbsoluteFile();
        try {
            BasicFileAttributes attributes = Files.readAttributes(key.toPath(), BasicFileAttributes.class);
            Entry entry = new Entry();
            entry.stats = copy(stats);
            entry.size = attributes.size();
            entry.lastModifiedTime = attributes.lastModifiedTime();
            entry.fileKey = attributes.fileKey();
            entries.put(key, entry);
        } catch (IOException e) {
            entries.remove(key);
        }
    }

    /**
     * Get a copy of the stats if the file is unchanged
     *
     * @param file
     * @return null if the stats are not available
     */
    public TableStats get(File file) {
        File key = file.getAbsoluteFile();
        Entry entry = entries.get(key);
        if (entry == null) {
            return null;
        }
        try {
            BasicFileAttributes attributes = Files.readAttributes(key.toPath(), BasicFileAttributes.class);
            if (attributes.size() == entry.size
                && attributes.lastModifiedTime().equals(entry.lastModifiedTime)
                && Objects.equals(attributes.fileKey(), entry.fileKey)) {
                return copy(entry.stats);
            }
        } catch (IOException e) {
            // file removed,...
        }
        entries.remove(key);
        return null;
    }

    private static TableStats copy(TableStats stats) {
        TableStats result = new TableStats();
        result.setTotalFeatures(stats.getTotalFeatures());
        result.setBoundingBox(new Envelope(stats.getBoundingBox()));
        return result;
    }

}
