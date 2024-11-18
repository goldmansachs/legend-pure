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

package org.finos.legend.pure.m2.relational.serialization.grammar.v1.validator;

import org.eclipse.collections.api.RichIterable;
import org.finos.legend.pure.m2.relational.M2RelationalPaths;
import org.finos.legend.pure.m3.compiler.Context;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.PropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.RelationType;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionInstanceSetImplementation;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionPropertyMapping;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.TableAliasColumn;
import org.finos.legend.pure.m3.coreinstance.meta.relational.metamodel.RelationExpressionColumn;
import org.finos.legend.pure.m3.navigation.Instance;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m3.navigation.importstub.ImportStub;
import org.finos.legend.pure.m3.navigation.relation._RelationType;
import org.finos.legend.pure.m3.tools.matcher.MatchRunner;
import org.finos.legend.pure.m3.tools.matcher.Matcher;
import org.finos.legend.pure.m3.tools.matcher.MatcherState;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;
import org.finos.legend.pure.m4.exception.PureCompilationException;

public class RelationExpressionInstanceSetImplementationValidator implements MatchRunner<RelationExpressionInstanceSetImplementation>
{
    @Override
    public void run(RelationExpressionInstanceSetImplementation instance, MatcherState state, Matcher matcher, ModelRepository modelRepository, Context context) throws PureCompilationException
    {
        ProcessorSupport processorSupport = state.getProcessorSupport();
        CoreInstance lastExpression = ImportStub.withImportStubByPass(instance._relationFunctionCoreInstance(), processorSupport).getValueForMetaPropertyToMany(M3Properties.expressionSequence).getLast();
        RelationType<?> returnType = (RelationType<?>) Instance.getValueForMetaPropertyToOneResolved(lastExpression, M3Properties.genericType, M3Properties.typeArguments, M3Properties.rawType, processorSupport);
        validatePropertyMappings(instance._propertyMappings(), returnType, processorSupport);
    }
    
    private void validatePropertyMappings(RichIterable<? extends PropertyMapping> propertyMappings, RelationType<?> relationType, ProcessorSupport processorSupport)
    {
        for (PropertyMapping propertyMapping: propertyMappings)
        {
            if (relationType != null)
            {
                RelationExpressionPropertyMapping RelationExpressionPropertyMapping = (RelationExpressionPropertyMapping) propertyMapping;
                TableAliasColumn tableAliasColumn = (TableAliasColumn)RelationExpressionPropertyMapping._relationalOperationElement();
                RelationExpressionColumn column = (RelationExpressionColumn) tableAliasColumn._column();
                _RelationType.findColumn(relationType, column._column()._name(), null, processorSupport);
            }
        }
    }

    @Override
    public String getClassName()
    {
        return M2RelationalPaths.RelationExpressionInstanceSetImplementation;
    }
}
