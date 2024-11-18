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

package org.finos.legend.pure.m2.relational.serialization.grammar.v1.processor;

import org.finos.legend.pure.m2.relational.M2RelationalPaths;
import org.finos.legend.pure.m3.compiler.Context;
import org.finos.legend.pure.m3.compiler.postprocessing.PostProcessor;
import org.finos.legend.pure.m3.compiler.postprocessing.ProcessorState;
import org.finos.legend.pure.m3.compiler.postprocessing.processor.Processor;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.relation.RelationType;
import org.finos.legend.pure.m3.coreinstance.meta.relational.mapping.RelationExpressionInstanceSetImplementation;
import org.finos.legend.pure.m3.navigation.Instance;
import org.finos.legend.pure.m3.navigation.M3Properties;
import org.finos.legend.pure.m3.navigation.ProcessorSupport;
import org.finos.legend.pure.m3.navigation.importstub.ImportStub;
import org.finos.legend.pure.m3.tools.matcher.Matcher;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.m4.coreinstance.CoreInstance;

public class RelationExpressionInstanceSetImplementationProcessor extends Processor<RelationExpressionInstanceSetImplementation>
{
    @Override
    public void process(RelationExpressionInstanceSetImplementation instance, ProcessorState state, Matcher matcher, ModelRepository repository, Context context, ProcessorSupport processorSupport)
    {
        CoreInstance relationFunction = ImportStub.withImportStubByPass(instance._relationFunctionCoreInstance(), processorSupport);
        PostProcessor.processElement(matcher, relationFunction, state, processorSupport);
        instance._isRelationFunctionRouted(false);
        CoreInstance lastExpressionType = relationFunction.getValueForMetaPropertyToMany(M3Properties.expressionSequence).getLast().getValueForMetaPropertyToOne(M3Properties.genericType);
        RelationType<?> relationType;
        if (lastExpressionType.getValueForMetaPropertyToOne(M3Properties.rawType) instanceof ImportStub)
        {
            relationType = (RelationType<?>) Instance.getValueForMetaPropertyToOneResolved(lastExpressionType, M3Properties.rawType, M3Properties.typeArguments, M3Properties.rawType, processorSupport);
        }
        else
        {
            relationType = (RelationType<?>) Instance.getValueForMetaPropertyToOneResolved(lastExpressionType, M3Properties.typeArguments, M3Properties.rawType, processorSupport);
        }
        RelationExpressionPropertyMappingProcessor.process(instance, relationType, processorSupport, repository);
    }

    @Override
    public void populateReferenceUsages(RelationExpressionInstanceSetImplementation instance, ModelRepository repository, ProcessorSupport processorSupport)
    {
        addReferenceUsageForToOneProperty(instance, instance._relationFunctionCoreInstance(), "relationFunction", repository, processorSupport);
    }

    @Override
    public String getClassName()
    {
        return M2RelationalPaths.RelationExpressionInstanceSetImplementation;
    }
}
