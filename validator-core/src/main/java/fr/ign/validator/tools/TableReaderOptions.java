package fr.ign.validator.tools;

import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Options to read table data.
 *
 * @author MBorne
 *
 */
public class TableReaderOptions {
    /**
     * Fallback charset if not provided throw the format.
     */
    private Charset sourceCharset;

    /**
     * An optional XSD schema to read GML data using GMLAS driver from GDAL.
     */
    private URL xsdSchema;

    public TableReaderOptions() {
        this.sourceCharset = StandardCharsets.UTF_8;
    }

    public TableReaderOptions(Charset sourceCharset) {
        this.sourceCharset = sourceCharset;
    }

    public Charset getSourceCharset() {
        return sourceCharset;
    }

    public void setSourceCharset(Charset sourceCharset) {
        this.sourceCharset = sourceCharset;
    }

    public boolean hasXsdSchema() {
        return xsdSchema != null;
    }

    public URL getXsdSchema() {
        return xsdSchema;
    }

    public void setXsdSchema(URL xsdSchema) {
        this.xsdSchema = xsdSchema;
    }

}
