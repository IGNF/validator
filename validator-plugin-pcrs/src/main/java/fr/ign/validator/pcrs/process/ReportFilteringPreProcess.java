package fr.ign.validator.pcrs.process;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.pcrs.report.CodeFilteredReportBuilder;

public class ReportFilteringPreProcess implements ValidatorListener {

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

        CodeFilteredReportBuilder reportBuilder = new CodeFilteredReportBuilder(context.getReportBuilder());
        context.setReportBuilder(reportBuilder);

        // PlanCorpsRueSimplifie n'apparait pas comme une table du multitable
        reportBuilder.addExpectedTable("PlanCorpsRueSimplifie");

        for (FileModel fileModel : document.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof MultiTableModel)) {
                continue;
            }
            for (EmbeddedTableModel tableModel : ((MultiTableModel) fileModel).getTableModels()) {
                reportBuilder.addExpectedTable(tableModel.getName());
            }
        }

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // TODO Auto-generated method stub

    }

}
