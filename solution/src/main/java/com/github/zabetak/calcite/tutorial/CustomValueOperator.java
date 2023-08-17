package com.github.zabetak.calcite.tutorial;

public class CustomValueOperator extends AbstractNode{

    protected TupleNode m_Tuples;

    public void setTuples(TupleNode Tuples) {
        m_Tuples = Tuples;
    }
}
