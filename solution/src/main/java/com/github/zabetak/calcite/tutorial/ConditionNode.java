package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.sql.SqlBinaryOperator;
import org.apache.calcite.sql.SqlFunction;

import java.util.ArrayList;
import java.util.List;

public  class ConditionNode {

    protected String m_conditionOperator;

    protected final List<ConditionNode> m_conditionOperands = new ArrayList<>();

    public void setOperator(String conditionOperator) {
        m_conditionOperator = conditionOperator;
    }

    public void addOperands(ConditionNode child) {
        m_conditionOperands.add(child);
    }


}
