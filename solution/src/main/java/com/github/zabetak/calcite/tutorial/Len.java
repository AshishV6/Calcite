package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.type.ReturnTypes;

import static org.apache.calcite.sql.SqlFunctionCategory.*;

public class Len extends SqlFunction {
    public Len() {
        super("Len", SqlKind.OTHER_FUNCTION, ReturnTypes.INTEGER_NULLABLE, null, OperandTypesPlus.STRING_INTEGER, NUMERIC);
    }


    void lol(){
        //some magic
        // that rearrages operands
    }



}
