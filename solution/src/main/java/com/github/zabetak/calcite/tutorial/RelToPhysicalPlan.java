package com.github.zabetak.calcite.tutorial;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.logical.*;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.rex.RexCall;
import org.apache.calcite.rex.RexInputRef;
import org.apache.calcite.rex.RexLiteral;
import org.apache.calcite.rex.RexNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelToPhysicalPlan {


    static AbstractNode getAbstractNode(RelNode relNode, AbstractNode parent) {

        // write some logic to convert rel node to abstract node

        if (relNode instanceof LogicalSort) {

            CustomSortOperator customSortOperator = new CustomSortOperator();

            AbstractNode.E6Field[] fields = getE6Fields(relNode);

            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);

            customSortOperator.setRowOut(rowOut);


            List<RelNode> relNodes = relNode.getInputs();

            customSortOperator.setParent(parent);

            for (RelNode childRelNode : relNodes) {
                customSortOperator.addChild(getAbstractNode(childRelNode, customSortOperator));
            }

            customSortOperator.setRowIn(customSortOperator.getChildren().get(0).getRowOut());

            return customSortOperator;

        } else if (relNode instanceof LogicalJoin) {

            CustomJoinOperator customJoinOperator = new CustomJoinOperator();

            AbstractNode.E6Field[] fields = getE6Fields(relNode);
            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);
            customJoinOperator.setRowOut(rowOut);

            List<RelNode> relNodes = relNode.getInputs();

            customJoinOperator.setParent(parent);


            for (RelNode childRelNode : relNodes) {
                customJoinOperator.addChild(getAbstractNode(childRelNode, customJoinOperator));
            }
            AbstractNode.RowPlus left = customJoinOperator.getChildren().get(0).getRowOut();
            AbstractNode.RowPlus right = customJoinOperator.getChildren().get(1).getRowOut();

            AbstractNode.E6Field[] joinfields = new AbstractNode.E6Field[left.getFieldCount() + right.getFieldCount()];


            for (int i = 0; i < left.getFieldCount(); i++) {
                joinfields[i] = left.getFieldAtIndex(i);
            }

            for (int i = left.getFieldCount(); i < left.getFieldCount() + right.getFieldCount(); i++) {
                joinfields[i] = right.getFieldAtIndex(i - left.getFieldCount());
            }

            customJoinOperator.setRowIn(new AbstractNode.RowPlus(joinfields));

            return customJoinOperator;


        } else if (relNode instanceof LogicalTableScan) {

            CustomTableOperator customTableOperator = new CustomTableOperator();


            AbstractNode.E6Field[] fields = getE6Fields(relNode);

            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);

            customTableOperator.setRowOut(rowOut);
            customTableOperator.setRowIn(rowOut);

            List<RelNode> relNodes = relNode.getInputs();

            customTableOperator.setParent(parent);


            for (RelNode childRelNode : relNodes) {
                customTableOperator.addChild(getAbstractNode(childRelNode, customTableOperator));
            }

            return customTableOperator;
        } else if (relNode instanceof LogicalProject) {

            CustomProjectOperator customProjectOperator = new CustomProjectOperator();


            AbstractNode.E6Field[] fields = getE6Fields(relNode);
            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);
            customProjectOperator.setRowOut(rowOut);


            List<RexNode> expressions = ((LogicalProject) relNode).getProjects();

            List<ConditionNode> conditionNodeList = new ArrayList<>();

            for (RexNode rexNode : expressions) {
                ConditionNode conditionNode = build(rexNode);
                conditionNodeList.add(conditionNode);
            }

            customProjectOperator.setExpressions(conditionNodeList);


            List<RelNode> relNodes = relNode.getInputs();

            customProjectOperator.setParent(parent);

            for (RelNode childRelNode : relNodes) {
                customProjectOperator.addChild(getAbstractNode(childRelNode, customProjectOperator));
            }

            customProjectOperator.setRowIn(customProjectOperator.getChildren().get(0).getRowOut());


            return customProjectOperator;
        } else if (relNode instanceof LogicalFilter) {

            CustomFilterOperator customFilterOperator = new CustomFilterOperator();

            AbstractNode.E6Field[] fields = getE6Fields(relNode);
            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);
            customFilterOperator.setRowOut(rowOut);


            RexNode condition = ((LogicalFilter) relNode).getCondition();

            ConditionNode conditionNode = build(condition);

            customFilterOperator.setCondition(conditionNode);


            List<RelNode> relNodes = relNode.getInputs();

            customFilterOperator.setParent(parent);

            for (RelNode childRelNode : relNodes) {
                customFilterOperator.addChild(getAbstractNode(childRelNode, customFilterOperator));
            }

            customFilterOperator.setRowIn(customFilterOperator.getChildren().get(0).getRowOut());

            return customFilterOperator;
        } else if (relNode instanceof LogicalValues) {
            CustomValueOperator customValueOperator = new CustomValueOperator();

            AbstractNode.E6Field[] fields = getE6Fields(relNode);
            AbstractNode.RowPlus rowOut = new AbstractNode.RowPlus(fields);
            customValueOperator.setRowOut(rowOut);


            ImmutableList<ImmutableList<RexLiteral>> tuples = ((LogicalValues) relNode).getTuples();

            TupleNode constTuples = buildTuples(tuples);

            customValueOperator.setTuples(constTuples);


            List<RelNode> relNodes = relNode.getInputs();

            customValueOperator.setParent(parent);


            for (RelNode childRelNode : relNodes) {
                customValueOperator.addChild(getAbstractNode(childRelNode, customValueOperator));
            }


            return customValueOperator;

        } else {
            throw new UnsupportedOperationException();
        }

    }


//    private String mapOperator(RexNode rexNode) {
//        String operatorName = (((RexCall) rexNode).getOperator()).getName();
//        HashMap<String, String> operatorMap = OperatorMap.OPERATOR_MAP;
//
//        if(operatorMap.containsKey(operatorName)){
//            return operatorMap.get(operatorName);
//        }
//        else {
//            return operatorName;
//        }
//    }


    public static ConditionNode build(RexNode rexNode) {

        if (rexNode instanceof RexCall) {

            ConditionNode conditionNode = new ConditionNode();

            String operatorName = (((RexCall) rexNode).getOperator()).getName();


            Identifier.populateOperatorMap();

            HashMap<String, String> operatorMap = Identifier.OPERATOR_MAP;

            if(operatorMap.containsKey(operatorName)){
                operatorName = operatorMap.get(operatorName);
            }


            conditionNode.setOperator(operatorName);

            List<RexNode> rexNodes = ((RexCall) rexNode).getOperands();

            for (RexNode ChildRexNodes : rexNodes) {
                conditionNode.addOperands(build(ChildRexNodes));
            }

            return conditionNode;

        } else if (rexNode instanceof RexInputRef) {

            String colName = ((RexInputRef) rexNode).getName();

            return new ColNameNode(colName);

        } else if (rexNode instanceof RexLiteral) {

            Object value = ((RexLiteral) rexNode).getValue();

            return new ConstNameNode(value);

        } else {
            throw new UnsupportedOperationException();
        }

    }


    public static TupleNode buildTuples(ImmutableList<ImmutableList<RexLiteral>> tuples) {

        TupleNode tupleNode = new TupleNode();

        for (List<RexLiteral> rexTupleList : tuples) {
            List<Object> row = new ArrayList<>();
            for (RexLiteral rexLiteral : rexTupleList) {
                row.add(rexLiteral.getValue());
            }
            tupleNode.addTupleList(row);
        }

        return tupleNode;

    }

    private static AbstractNode.E6Field[] getE6Fields(RelNode relNode) {
        List<RelDataTypeField> fieldlist = relNode.getRowType().getFieldList();
        AbstractNode.E6Field[] fields = new AbstractNode.E6Field[fieldlist.size()];
        for (int i = 0; i < fieldlist.size(); i++) {
            RelDataTypeField relField = fieldlist.get(i);
            fields[i] = new AbstractNode.E6Field(relField.getName(), relField.getType().getFullTypeString());
        }
        return fields;
    }

}
