package fr.ign.validator.process;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.Projection;

/**
 *
 * Provides informations messages about project
 *
 * @author MBorne
 *
 */
public class ProjectionPreProcess implements ValidatorListener {

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        Projection projection = context.getProjection();

        context.report(
            context.createError(CoreErrorCodes.VALIDATOR_PROJECTION_INFO)
                .setMessageParam("CODE_PROJECTION", projection.getCode())
                .setMessageParam("URI_PROJECTION", projection.getUri())
        );
        if (projection.getCode().equals("EPSG:4326")) {
            context.report(context.createError(CoreErrorCodes.VALIDATOR_PROJECTION_LATLON));
        }
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {

    }

}
