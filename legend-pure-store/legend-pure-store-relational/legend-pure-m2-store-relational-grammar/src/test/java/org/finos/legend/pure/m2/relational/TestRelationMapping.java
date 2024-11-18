// Copyright 2020 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.pure.m2.relational;

import org.eclipse.collections.api.RichIterable;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.Mapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.PropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.SetImplementation;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.FunctionDefinition;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.Column;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.RelationType;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Type;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionInstanceSetImplementation;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionPropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpression;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpressionColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAliasColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.datatype.Integer;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.datatype.Varchar;
import org.junit.Assert;
import org.junit.Test;

import static org.finos.legend.pure.m2.relational.RelationMappingShared.RELATION_MAPPING_CLASS_SOURCE;
import static org.finos.legend.pure.m2.relational.RelationMappingShared.RELATION_MAPPING_DB_SOURCE;
import static org.finos.legend.pure.m2.relational.RelationMappingShared.RELATION_MAPPING_FUNCTION_SOURCE;

public class TestRelationMapping extends AbstractPureRelationalTestWithCoreCompiled
{
    @Test
    public void testRelationMapping()
    {
        String mappingSource = "###Mapping\n" +
                "Mapping my::testMapping\n" +
                "(\n" +
                "  *my::Person[person]: Relation\n" +
                "  {\n" +
                "    ~func my::personFunction__Relation_1_\n" +
                "    firstName: FIRSTNAME,\n" +
                "    +age: Integer[0..1]: AGE\n" +
                "  }\n" +
                ")\n";
        
        compileTestSource("fromString.pure", RELATION_MAPPING_CLASS_SOURCE + RELATION_MAPPING_DB_SOURCE + RELATION_MAPPING_FUNCTION_SOURCE + mappingSource);

        RichIterable<? extends SetImplementation> setImpls = ((Mapping) runtime.getCoreInstance("my::testMapping"))._classMappings();
        Assert.assertEquals(1, setImpls.size());
        
        Assert.assertTrue(setImpls.getOnly() instanceof RelationExpressionInstanceSetImplementation);
        RelationExpressionInstanceSetImplementation relationSetImpl = (RelationExpressionInstanceSetImplementation) setImpls.getOnly();
        Assert.assertEquals("person", relationSetImpl._id());
        Assert.assertTrue(relationSetImpl._root());
        Assert.assertFalse(relationSetImpl._isRelationFunctionRouted());
        
        FunctionDefinition<?> relationFunction = relationSetImpl._relationFunction();
        Assert.assertEquals("personFunction", relationFunction._functionName());
        Type lastExpressionType = relationFunction._expressionSequence().getOnly()._genericType()._typeArguments().getOnly()._rawType();
        Assert.assertTrue(lastExpressionType instanceof RelationType);
        
        RichIterable<? extends PropertyMapping> propertyMappings = relationSetImpl._propertyMappings();
        Assert.assertEquals(2, propertyMappings.size());
        propertyMappings.each(r -> Assert.assertTrue(r instanceof RelationExpressionPropertyMapping));
        
        RelationExpressionPropertyMapping propertyMapping1 = (RelationExpressionPropertyMapping) propertyMappings.toList().get(0);
        Assert.assertTrue(propertyMapping1._relationalOperationElement() instanceof TableAliasColumn);
        TableAliasColumn tac1 = (TableAliasColumn) propertyMapping1._relationalOperationElement();
        Assert.assertTrue(tac1._column() instanceof RelationExpressionColumn);
        RelationExpressionColumn column1 = (RelationExpressionColumn) tac1._column();
        Assert.assertEquals("FIRSTNAME", column1._column()._name());
        Assert.assertTrue(column1._type() instanceof Varchar);
        Assert.assertTrue(tac1._alias()._relationalElement() instanceof RelationExpression);
        Assert.assertEquals("person", tac1._alias()._name());
        
        RelationExpressionPropertyMapping propertyMapping2 = (RelationExpressionPropertyMapping) propertyMappings.toList().get(1);
        Assert.assertTrue(propertyMapping2._relationalOperationElement() instanceof TableAliasColumn);
        Assert.assertTrue(propertyMapping2._localMappingProperty());
        TableAliasColumn tac2 = (TableAliasColumn) propertyMapping2._relationalOperationElement();
        Assert.assertTrue(tac2._column() instanceof RelationExpressionColumn);
        RelationExpressionColumn column2 = (RelationExpressionColumn) tac2._column();
        Assert.assertEquals("AGE", column2._column()._name());
        Assert.assertTrue(column2._type() instanceof Integer);
        Assert.assertTrue(tac2._alias()._relationalElement() instanceof RelationExpression);
        Assert.assertEquals("person", tac2._alias()._name());
    }
}
