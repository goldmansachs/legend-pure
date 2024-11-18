parser grammar RelationMappingParser;

import M3CoreParser;

options
{
    tokenVocab = RelationMappingLexer;
}

mapping:                                    RELATION_FUNCTION qualifiedName
                                            (singleRelationPropertyMapping (COMMA singleRelationPropertyMapping)*)?
                                            EOF
;

singleRelationPropertyMapping:              nonLocalRelationPropertyMapping | localRelationMappingProperty
;

localRelationMappingProperty:               PLUS identifier localMappingProperty relationPropertyMapping
;

localMappingProperty:                       COLON qualifiedName BRACKET_OPEN localMappingPropertyFirstMul (DOTDOT localMappingPropertySecondMul)? BRACKET_CLOSE
;

localMappingPropertyFirstMul:               INTEGER
;

localMappingPropertySecondMul:              INTEGER
;

nonLocalRelationPropertyMapping:            identifier relationPropertyMapping
;

relationPropertyMapping:                    COLON identifier
;


