package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.rex.RexNode;


public class ConstNameNode extends ConditionNode{

private final Object m_Value;

public ConstNameNode(Object value){
    m_Value = value;
}

}
