/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.zabetak.calcite.tutorial;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.java.JavaTypeFactory;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.config.CalciteConnectionConfigImpl;
import org.apache.calcite.config.CalciteConnectionProperty;
import org.apache.calcite.jdbc.CalciteSchema;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.QueryProvider;
import org.apache.calcite.plan.ConventionTraitDef;
import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelOptPlanner;
import org.apache.calcite.plan.RelOptTable;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.prepare.CalciteCatalogReader;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.apache.calcite.rex.RexNode;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlExplainFormat;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorUtil;
import org.apache.calcite.sql2rel.SqlToRelConverter;
import org.apache.calcite.sql2rel.StandardConvertletTable;

import com.github.zabetak.calcite.tutorial.indexer.DatasetIndexer;
import com.github.zabetak.calcite.tutorial.indexer.TpchTable;
import com.github.zabetak.calcite.tutorial.operators.LuceneRel;

import java.util.*;

/**
 * Query processor for running TPC-H queries over Apache Lucene.
 */
public class LuceneQueryProcessor {

    /**
     * The type of query processor.
     */
    public enum Type {
        /**
         * Simple query processor using only one convention.
         * <p>
         * The processor relies on {@link org.apache.calcite.adapter.enumerable.EnumerableConvention}
         * and the {@link org.apache.calcite.schema.ScannableTable} interface to execute queries over
         * Lucene.
         */
        SIMPLE,
        /**
         * Advanced query processor using two conventions.
         * <p>
         * The processor relies on {@link org.apache.calcite.adapter.enumerable.EnumerableConvention}
         * and {@link LuceneRel#LUCENE} to execute queries over Lucene.
         */
        ADVANCED,
        /**
         * Advanced query processor using two conventions and extra rules capable of pushing basic
         * conditions in Lucene.
         * <p>
         * The processor relies on {@link org.apache.calcite.adapter.enumerable.EnumerableConvention}
         * and {@link LuceneRel#LUCENE} to execute queries over Lucene.
         */
        PUSHDOWN
    }

    public static void main(String[] args) throws Exception {
//    if (args.length != 2) {
//      System.out.println("Usage: runner [SIMPLE|ADVANCED] SQL_FILE");'America/Los_Angeles'
//      System.exit(-1);
//    }


//        SELECT c_name, o_orderkey, o_orderdate FROM customer
//        INNER JOIN orders ON c_custkey = o_custkey
//        WHERE c_custkey < 3
//        ORDER BY c_name, o_orderkey



//        SELECT DATETIME(timestamp '2008-9-23 01:23:45','America/Los_Angeles')
//
//        SELECT CONVERT_TIMEZONE('America/Los_Angeles',timestamp '2008-9-23 01:23:45')
//
//
//
//        SELECT CONVERT_TIMEZONE('America/Los_Angeles',timestamp '2008-9-23 01:23:45'), abs(1.8), 1, 'a'





        String sqlQuery = "SELECT ADD(2,4), PLUS(3,5)";
        long start = System.currentTimeMillis();
        Enumerable<Objects> rows = execute(sqlQuery, Type.SIMPLE);
        if (rows != null) {
            for (Object row : rows) {
                if (row instanceof Object[]) {
                    System.out.println(Arrays.toString((Object[]) row));
                } else {
                    System.out.println(row);
                }
            }
        }
        long finish = System.currentTimeMillis();
        System.out.println("Elapsed time " + (finish - start) + "ms");
    }

    /**
     * Plans and executes an SQL query.
     *
     * @param sqlQuery - a string with the SQL query for execution
     * @return an Enumerable with the results of the execution of the query
     * @throws SqlParseException if there is a problem when parsing the query
     */
    public static <T, Rexnode> Enumerable<T> execute(String sqlQuery, Type processorType)
            throws SqlParseException {
        System.out.println("[Input query]");
        System.out.println(sqlQuery);

// my code starts








// my code ends


        // Create the schema and table data types
        CalciteSchema schema = CalciteSchema.createRootSchema(true);
        RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
        for (TpchTable table : TpchTable.values()) {
            RelDataTypeFactory.Builder builder = new RelDataTypeFactory.Builder(typeFactory);
            for (TpchTable.Column column : table.columns) {
                RelDataType type = typeFactory.createJavaType(column.type);
                builder.add(column.name, type.getSqlTypeName()).nullable(true);
            }
            String indexPath = DatasetIndexer.INDEX_LOCATION + "/tpch/" + table.name();
            schema.add(table.name(), new LuceneTable(indexPath, builder.build()));
        }

        // Create an SQL parser
        SqlParser parser = SqlParser.create(sqlQuery);
        // Parse the query into an AST
        SqlNode sqlNode = parser.parseQuery();
        System.out.println("[Parsed query]");
        System.out.println(sqlNode.toString());


        // Configure and instantiate validator
        Properties props = new Properties();
        props.setProperty(CalciteConnectionProperty.CASE_SENSITIVE.camelName(), "false");
        CalciteConnectionConfig config = new CalciteConnectionConfigImpl(props);
        CalciteCatalogReader catalogReader = new CalciteCatalogReader(schema,
                Collections.singletonList("bs"),
                typeFactory, config);

        SqlValidator validator = SqlValidatorUtil.newValidator(SqlStdOperatorTablePlus.instance(),
                catalogReader, typeFactory,
                SqlValidator.Config.DEFAULT);

        // Validate the initial AST
        SqlNode validNode = validator.validate(sqlNode);

        // Configure and instantiate the converter of the AST to Logical plan (requires opt cluster)
        RelOptCluster cluster = newCluster(typeFactory);
        SqlToRelConverter relConverter = new SqlToRelConverter(
                NOOP_EXPANDER,
                validator,
                catalogReader,
                cluster,
                StandardConvertletTable.INSTANCE,
                SqlToRelConverter.config());

        // Convert the valid AST into a logical plan
        RelNode logPlan = relConverter.convertQuery(validNode, false, true).rel;

        logPlan.explain();
        // Display the logical plan
        System.out.println(
                RelOptUtil.dumpPlan("[Logical plan]", logPlan, SqlExplainFormat.TEXT,
                        SqlExplainLevel.NON_COST_ATTRIBUTES));


        AbstractNode physicalPlan = RelToPhysicalPlan.getAbstractNode(logPlan, null);

        System.out.println("[Physical plan]");
        System.out.println(physicalPlan);





        // Initialize optimizer/planner with the necessary rules

        // Run the executable plan using a context simply providing access to the schema



        return null;



    }

    private static RelOptCluster newCluster(RelDataTypeFactory factory) {
        RelOptPlanner planner = new VolcanoPlanner();
        planner.addRelTraitDef(ConventionTraitDef.INSTANCE);
        return RelOptCluster.create(planner, new RexBuilder(factory));
    }

    private static final RelOptTable.ViewExpander NOOP_EXPANDER = (type, query, schema, path) -> null;

    /**
     * A simple data context only with schema information.
     */
    private static final class SchemaOnlyDataContext implements DataContext {
        private final SchemaPlus schema;

        SchemaOnlyDataContext(CalciteSchema calciteSchema) {
            this.schema = calciteSchema.plus();
        }

        @Override
        public SchemaPlus getRootSchema() {
            return schema;
        }

        @Override
        public JavaTypeFactory getTypeFactory() {
            return new JavaTypeFactoryImpl();
        }

        @Override
        public QueryProvider getQueryProvider() {
            return null;
        }

        @Override
        public Object get(final String name) {
            return null;
        }
    }
}
