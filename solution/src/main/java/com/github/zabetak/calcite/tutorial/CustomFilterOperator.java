package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.rex.RexNode;
import org.apache.calcite.sql.SqlBinaryOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;

public class CustomFilterOperator extends AbstractNode {

    protected ConditionNode m_condition;

    public void setCondition(ConditionNode conditionNode) {
        m_condition = conditionNode;
    }
}






