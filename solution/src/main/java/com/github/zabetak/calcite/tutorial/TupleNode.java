package com.github.zabetak.calcite.tutorial;



import com.google.common.collect.ImmutableList;
import org.apache.calcite.rex.RexLiteral;

import java.util.ArrayList;
import java.util.List;

public  class TupleNode {

    protected final List<List<Object>> m_TupleList = new ArrayList<>();

    public void addTupleList(List<Object> m_sTupleList) {
        m_TupleList.add(m_sTupleList);
    }



}
