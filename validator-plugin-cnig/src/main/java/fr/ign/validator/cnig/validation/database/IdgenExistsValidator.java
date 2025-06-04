package fr.ign.validator.cnig.validation.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.sup.DatabaseSUP;
import fr.ign.validator.cnig.sup.DatabaseSUP.AssietteSup;
import fr.ign.validator.validation.Validator;

/**
 * Ensure that IDGEN in ASSIETTE_SUP_P/L/S tables exists in GENERATEUR_SUP_P/L/S
 * tables.
 *
 * @author MBorne
 *
 */
public class IdgenExistsValidator implements Validator<DatabaseSUP> {

    public static final int LIMIT = 10;

    @Override
    public void validate(Context context, DatabaseSUP database) {
        try {
            doValidate(context, database);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doValidate(Context context, DatabaseSUP database) throws SQLException, IOException {
        List<AssietteSup> assiettes = database.findAssiettesWithInvalidIDGEN(LIMIT);
        for (AssietteSup assiette : assiettes) {
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_SUP_IDGEN_NOT_FOUND
                ).setMessageParam(
                    "IDASS", assiette.idass
                ).setMessageParam(
                    "IDGEN", assiette.idgen
                )
            );
        }
    }

}
