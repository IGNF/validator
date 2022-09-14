package fr.ign.validator.model.constraint;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.exception.InvalidModelException;

public class ForeignKeyConstraintTest {

    @Test
    public void testParseOk() {
        String constraintString = "(VALUE, SUB_VALUE) REFERENCES MY_REFERENCE(TYPE, SUB_TYPE)";
        ForeignKeyConstraint constraint = ForeignKeyConstraint.parseForeignKey(constraintString);

        Assert.assertEquals("(VALUE,SUB_VALUE) REFERENCES MY_REFERENCE(TYPE,SUB_TYPE)", constraint.toString());
    }

    @Test
    public void testParseFormat2() {
        String constraintString = " (  VALUE ,  SUB_VALUE  )  REFERENCES   MY_REFERENCE ( TYPE,  SUB_TYPE ) ";
        ForeignKeyConstraint constraint = ForeignKeyConstraint.parseForeignKey(constraintString);

        Assert.assertEquals("(VALUE,SUB_VALUE) REFERENCES MY_REFERENCE(TYPE,SUB_TYPE)", constraint.toString());
    }

    @Test(expected = InvalidModelException.class)
    public void testParseErrorCommmaExpected() {
        String constraintString = "(VALUE SUB_VALUE) REFERENCES MY_REFERENCE(TYPE SUB_TYPE)";
        ForeignKeyConstraint.parseForeignKey(constraintString);
    }

    @Test(expected = InvalidModelException.class)
    public void testParseErrorReferenceExpected() {
        String constraintString = "(VALUE, SUB_VALUE) RFRCNC MY_REFERENCE(TYPE, SUB_TYPE)";
        ForeignKeyConstraint.parseForeignKey(constraintString);
    }

    @Test(expected = InvalidModelException.class)
    public void testParseErrorTableExpected() {
        String constraintString = "(VALUE, SUB_VALUE) REFERENCES (TYPE, SUB_TYPE)";
        ForeignKeyConstraint.parseForeignKey(constraintString);
    }

    @Test(expected = InvalidModelException.class)
    public void testParseErrorParenthesisExpected() {
        String constraintString = "VALUE, SUB_VALUE REFERENCES MY_REFERENCE TYPE, SUB_TYPE";
        ForeignKeyConstraint.parseForeignKey(constraintString);
    }

}
