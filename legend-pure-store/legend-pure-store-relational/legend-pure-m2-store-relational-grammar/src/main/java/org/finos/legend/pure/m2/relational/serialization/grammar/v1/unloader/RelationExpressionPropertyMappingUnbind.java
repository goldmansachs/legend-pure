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

package org.finos.legend.pure.m2.relational.serialization.grammar.v1.unloader;

import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.PropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionInstanceSetImplementation;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionPropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpression;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAlias;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAliasColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpressionColumn;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.m4.exception.PureCompilationException;

public class RelationExpressionPropertyMappingUnbind
{
    public static void cleanPropertyMappings(RelationExpressionInstanceSetImplementation setImplementation, ModelRepository repository, ProcessorSupport processorSupport) throws PureCompilationException
    {
        for (PropertyMapping propertyMapping: setImplementation._propertyMappings())
        {
            RelationExpressionPropertyMapping RelationExpressionPropertyMapping = (RelationExpressionPropertyMapping) propertyMapping;
            TableAliasColumn tableAliasColumn = (TableAliasColumn) RelationExpressionPropertyMapping._relationalOperationElement();
            TableAlias tableAlias = tableAliasColumn._alias();
            RelationExpression relationExpression = (RelationExpression) tableAlias._relationalElement();
            relationExpression._ownerRemove();
            tableAlias._nameRemove();
            tableAlias._relationalElementRemove();
            RelationExpressionColumn column = (RelationExpressionColumn) tableAliasColumn._column();
            RelationExpressionPropertyMapping._relationalOperationElement(column);
            tableAliasColumn._aliasRemove();
            tableAliasColumn._columnRemove();
        }
    }
}
