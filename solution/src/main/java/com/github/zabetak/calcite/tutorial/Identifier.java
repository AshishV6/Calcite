package com.github.zabetak.calcite.tutorial;

import java.util.HashMap;

public class Identifier {


    public static HashMap<String, String> OPERATOR_MAP = new HashMap<>();
    public static void populateOperatorMap() {

        HashMap<String, String> operatorMap = OPERATOR_MAP;

        operatorMap.put("ADD", "ADDITION");
        operatorMap.put("SUM", "ADDITION");
        operatorMap.put("PLUS", "ADDITION");
        operatorMap.put("MINUS", "SUBTRACTION");
        operatorMap.put("SUBTRACT", "SUBTRACTION");

    }





}

