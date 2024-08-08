package fr.ign.validator.geometry;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.model.Projection;

/**
 * Helper class to find supported projections embedded in resources.
 *
 * @see validator-core/src/main/resources/projection.json
 *
 * @author MBorne
 */
public class ProjectionList {

    private static ProjectionList instance = new ProjectionList();

    private List<Projection> projections = new ArrayList<>();

    private ProjectionList() {
        try {
            this.loadFromResources();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @return
     */
    public static ProjectionList getInstance() {
        return ProjectionList.instance;
    }

    /**
     * Get CRS:84 projection.
     *
     * @return
     */
    public static Projection getCRS84() {
        return getInstance().findByCode(Projection.CODE_CRS84);
    }

    /**
     * Find all projections
     *
     * @return
     */
    public List<Projection> findAll() {
        return this.projections;
    }

    /**
     * Find projection by code
     *
     * @param code
     * @return
     */
    public Projection findByCode(String code) {
        for (Projection projection : projections) {
            if (projection.getCode().equals(code)) {
                return projection;
            }
        }
        return null;
    }

    /**
     * Find projection by code
     *
     * @param code
     * @return
     */
    public Projection findByUri(String uri) {
        for (Projection projection : projections) {
            if (projection.getUri().equals(uri)) {
                return projection;
            }
        }
        return null;
    }

    /**
     * Load src/main/resources/projection.json
     *
     * @throws IOException
     */
    private void loadFromResources() throws IOException {
        InputStream is = getClass().getResourceAsStream("/projection.json");
        ObjectMapper mapper = new ObjectMapper();
        this.projections = mapper.readValue(is, new TypeReference<List<Projection>>() {
        });
    }

}
