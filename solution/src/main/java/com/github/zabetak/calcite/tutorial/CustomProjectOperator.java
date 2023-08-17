package com.github.zabetak.calcite.tutorial;

import java.util.List;

public class CustomProjectOperator extends AbstractNode {

    protected List<ConditionNode> m_expression;

    public void setExpressions(List<ConditionNode> expressionNode) {
        m_expression = expressionNode;
    }
}
