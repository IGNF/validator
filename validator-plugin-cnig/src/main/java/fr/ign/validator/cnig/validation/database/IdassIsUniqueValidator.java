package fr.ign.validator.cnig.validation.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.sup.DatabaseSUP;
import fr.ign.validator.database.internal.DuplicatedValuesFinder.DuplicatedValue;
import fr.ign.validator.validation.Validator;

/**
 * Ensure that IDASS is unique in ASSIETTE_SUP_P/L/S tables.
 * 
 * @author MBorne
 *
 */
public class IdassIsUniqueValidator implements Validator<DatabaseSUP> {

    @Override
    public void validate(Context context, DatabaseSUP database) {
        try {
            doValidate(context, database);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doValidate(Context context, DatabaseSUP database) throws SQLException, IOException {
        List<DuplicatedValue> duplicatedValues = database.findDuplicatedValuesForIDASS();
        for (DuplicatedValue duplicatedValue : duplicatedValues) {
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_SUP_IDASS_NOT_UNIQUE
                ).setMessageParam("IDASS", duplicatedValue.value)
            );
        }
    }

}
