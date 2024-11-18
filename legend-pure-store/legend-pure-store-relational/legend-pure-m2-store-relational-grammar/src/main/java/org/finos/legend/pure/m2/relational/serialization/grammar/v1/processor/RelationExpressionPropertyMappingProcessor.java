// Copyright 2021 Goldman Sachs
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

package org.finos.legend.pure.m2.relational.serialization.grammar.v1.processor;

import org.finos.legend.pure.m2.relational.M2RelationalPaths;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.PropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.Column;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.RelationType;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Type;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionInstanceSetImplementation;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionPropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpression;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationalOperationElement;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAlias;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAliasColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpressionColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.datatype.DataType;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.datatype.Varchar;
import org.finos.legend.pure.m3.navigation.M3Paths;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m3.navigation.relation._RelationType;
import org.finos.legend.pure.m4.ModelRepository;

public class RelationExpressionPropertyMappingProcessor
{
    public static void process(RelationExpressionInstanceSetImplementation classMapping, RelationType<?> relationType, ProcessorSupport processorSupport, ModelRepository repository)
    {
        for (PropertyMapping propertyMapping : classMapping._propertyMappings())
        {
            RelationExpressionPropertyMapping relationExpressionPropertyMapping = (RelationExpressionPropertyMapping) propertyMapping;
            RelationalOperationElement relationalOperationElement = relationExpressionPropertyMapping._relationalOperationElement();
            if (relationalOperationElement instanceof RelationExpressionColumn)
            {
                RelationExpressionColumn relationExpressionColumn = (RelationExpressionColumn) relationalOperationElement;
                TableAliasColumn tableAliasColumn = (TableAliasColumn) repository.newEphemeralAnonymousCoreInstance(null, processorSupport.package_getByUserPath(M2RelationalPaths.TableAliasColumn));
                if (relationType != null)
                {
                    Column<?, ?> column = (Column<?, ?>) _RelationType.findColumn(relationType, relationExpressionColumn._column()._name(), classMapping.getSourceInformation(), processorSupport);
                    Type type = (Type) column.getValueForMetaPropertyToOne(M3Properties.classifierGenericType).getValueForMetaPropertyToMany(M3Properties.typeArguments).get(1).getValueForMetaPropertyToOne(M3Properties.rawType);
                    relationExpressionColumn._type(convertType(type, processorSupport));
                }
                tableAliasColumn._column(relationExpressionColumn);
                TableAlias tableAlias = (TableAlias) repository.newEphemeralAnonymousCoreInstance(null, processorSupport.package_getByUserPath(M2RelationalPaths.TableAlias));
                RelationExpression relationExpression = (RelationExpression) repository.newEphemeralAnonymousCoreInstance(null, processorSupport.package_getByUserPath(M2RelationalPaths.RelationExpression));
                relationExpression._owner(classMapping);
                tableAlias._relationalElement(relationExpression);
                tableAlias._name(classMapping._id());
                tableAliasColumn._alias(tableAlias);
                relationExpressionPropertyMapping._relationalOperationElement(tableAliasColumn);
            }
        }
    }

    private static DataType convertType(Type type, ProcessorSupport processorSupport)
    {
        String relationalType;
        switch (type._name())
        {
            case M3Paths.Integer:
                relationalType = M2RelationalPaths.Integer;
                break;
            case M3Paths.String:
                relationalType = M2RelationalPaths.Varchar;
                break;
            case M3Paths.Float:
            case M3Paths.Number:
                relationalType = M2RelationalPaths.Float;
                break;
            case M3Paths.Date:
            case M3Paths.DateTime:
                relationalType = M2RelationalPaths.Timestamp;
                break;
            case M3Paths.StrictDate:
                relationalType = M2RelationalPaths.Date;
                break;
            case M3Paths.Boolean:
                relationalType = M2RelationalPaths.Bit;
                break;
            default:
                throw new RuntimeException("Implement support for '" + type._name() + "'");
        }
        
        DataType datatype = (DataType) processorSupport.newEphemeralAnonymousCoreInstance(relationalType);
        if (datatype instanceof Varchar)
        {
            ((Varchar) datatype)._size(8192);
        }
        return datatype;
    }
}
