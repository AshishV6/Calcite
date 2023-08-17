package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.rex.RexNode;

public class ColNameNode extends ConditionNode{

    private final String m_ColName;
    public ColNameNode(String colName){
        super();
        m_ColName = colName;
    }


}
