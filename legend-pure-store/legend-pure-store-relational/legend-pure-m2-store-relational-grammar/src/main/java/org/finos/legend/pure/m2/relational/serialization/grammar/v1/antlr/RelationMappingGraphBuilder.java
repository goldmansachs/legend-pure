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

package org.finos.legend.pure.m2.relational.serialization.grammar.v1.antlr;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.finos.legend.pure.m3.serialization.grammar.ParserLibrary;
import org.finos.legend.pure.m3.serialization.grammar.m3parser.antlr.ParsingUtils;
import org.finos.legend.pure.m4.serialization.grammar.antlr.AntlrSourceInformation;

import java.util.List;

public class RelationMappingGraphBuilder extends RelationMappingParserBaseVisitor<String>
{
    private final String importId;

    private final AntlrSourceInformation sourceInformation;

    private String dbImport;

    private final ParserLibrary parserLibrary;

    public RelationMappingGraphBuilder(String importId, AntlrSourceInformation sourceInformation, ParserLibrary parserLibrary)
    {
        this.importId = importId;
        this.sourceInformation = sourceInformation;
        this.parserLibrary = parserLibrary;
    }
    
    public String visitMapping(RelationMappingParser.MappingContext ctx, String id, String extendsId, String setSourceInfo, boolean root, String classPath, String classSourceInfo, String mappingPath)
    {
        Token startToken = ctx.qualifiedName().getStart();
        String idOrPath = ctx.qualifiedName().getText();
        String propertyMappings = "";

        if (ctx.singleRelationPropertyMapping() != null)
        {
            propertyMappings = visitRelationPropertyMappingsBlock(ctx.singleRelationPropertyMapping(), id);
        }
        return "^meta::relational::mapping::RelationExpressionInstanceSetImplementation" + setSourceInfo + "(" +
                (id == null ? "" : "id = '" + id + "',") +
                (extendsId == null ? "" : "superSetImplementationId = '" + extendsId + "',") +
                "root = " + root + "," +
                "class = ^meta::pure::metamodel::import::ImportStub " + classSourceInfo + " (importGroup=system::imports::" + importId + ", idOrPath='" + classPath + "')," +
                "parent = ^meta::pure::metamodel::import::ImportStub (importGroup=system::imports::" + importId + ", idOrPath='" + mappingPath + "')," +
                "relationFunction = ^meta::pure::metamodel::import::ImportStub " + this.sourceInformation.getPureSourceInformation(startToken, startToken,  ctx.qualifiedName().getStop()).toM4String() + " (importGroup=system::imports::" + this.importId + ", idOrPath='" + idOrPath + "')," +
                "propertyMappings=[" + propertyMappings + "]" +
                ")";
    }

    private String visitRelationPropertyMappingsBlock(List<RelationMappingParser.SingleRelationPropertyMappingContext> ctx, String id)
    {
        StringBuilder sb = new StringBuilder();
        for (RelationMappingParser.SingleRelationPropertyMappingContext rpmCtx : ctx)
        {
            sb.append(visitRelationPropertyMappingBlock(rpmCtx, id));
            sb.append(",");
        }
        ParsingUtils.removeLastCommaCharacterIfPresent(sb);
        return sb.toString();
    }

    private String visitRelationPropertyMappingBlock(RelationMappingParser.SingleRelationPropertyMappingContext ctx, String id)
    {
        if (ctx.localRelationMappingProperty() != null)
        {
            RelationMappingParser.LocalRelationMappingPropertyContext localCtx = ctx.localRelationMappingProperty();
            LocalMappingPropertyParseResult r = visitLocalMappingPropertyBlock(localCtx.localMappingProperty());
            return visitRelationPropertyMappingBlock(localCtx.relationPropertyMapping(), id, localCtx.identifier().getStart(), true, r.getLocalMappingPropertyType(), r.getLocalMappingPropertyFirstMul(), r.getLocalMappingPropertySecondMul());
        }
        RelationMappingParser.NonLocalRelationPropertyMappingContext nonLocalCtx = ctx.nonLocalRelationPropertyMapping();
        return visitRelationPropertyMappingBlock(nonLocalCtx.relationPropertyMapping(), id, nonLocalCtx.identifier().getStart(), false, null, null, null);
    }
    
    private String visitRelationPropertyMappingBlock(RelationMappingParser.RelationPropertyMappingContext ctx, String id, Token propertyName, boolean localMappingProperty, String localMappingPropertyType, Token localMappingPropertyFirstMul, Token localMappingPropertySecondMul)
    {
        String columnName = ctx.identifier().getText();
        
        return "^meta::relational::mapping::RelationExpressionPropertyMapping" + sourceInformation.getPureSourceInformation(propertyName).toM4String() + "(" +
            "        localMappingProperty = " + localMappingProperty + "," +
            (localMappingPropertyType == null ? "" : "        localMappingPropertyType = " + localMappingPropertyType + ",") +
            buildMul(localMappingPropertyFirstMul, localMappingPropertySecondMul) +
            "        property = '" + propertyName.getText() + "'," +
            (id == null ? "" : "        sourceSetImplementationId = '" + id + "', ") +
            "        relationalOperationElement=^meta::relational::metamodel::RelationExpressionColumn(name = '" + columnName + "', column = ^meta::pure::metamodel::relation::Column(name = '" + columnName + "', nameWildCard = false))" +
            ")";
    }

    private LocalMappingPropertyParseResult visitLocalMappingPropertyBlock(RelationMappingParser.LocalMappingPropertyContext ctx)
    {
        return new LocalMappingPropertyParseResult(
                ctx.qualifiedName().getText(), 
                ctx.localMappingPropertyFirstMul().INTEGER() != null ? ctx.localMappingPropertyFirstMul().INTEGER().getSymbol() : null, 
                ctx.localMappingPropertySecondMul() != null ? ctx.localMappingPropertySecondMul().INTEGER() != null ? ctx.localMappingPropertySecondMul().INTEGER().getSymbol() : null : null
        );
    }

    private String buildMul(Token localMappingPropertyFirstMul, Token localMappingPropertySecondMul)
    {
        Token secondOne = localMappingPropertySecondMul == null ? localMappingPropertyFirstMul : localMappingPropertySecondMul;
        return localMappingPropertyFirstMul == null && localMappingPropertySecondMul == null ?
                "" :
                "localMappingPropertyMultiplicity = ^meta::pure::metamodel::multiplicity::Multiplicity(" +
                        "   lowerBound=^meta::pure::metamodel::multiplicity::MultiplicityValue(value=" + (localMappingPropertySecondMul == null || localMappingPropertyFirstMul == null ? "0" : localMappingPropertyFirstMul.getText()) + ")," +
                        "   upperBound=^meta::pure::metamodel::multiplicity::MultiplicityValue(" + (secondOne.getText().equals("*") ? "" : "value=" + secondOne.getText()) + ")" +
                        "),";
    }
}
