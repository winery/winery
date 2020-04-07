/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

grammar ChefDSL;

@parser::members
{
public String defaultString = "default";
public String nodeString = "node";
}

@lexer::members 
{ 
public int nesting = 0; 
}

// Lexer Rules
program : term* compstmt+ EOF;

compstmt : stmt (term expr)*? term+ #CompstmtA
        ;
        
inner_comptstmt : stmt (term expr)*? term*?;
        
stmt    :   call doStatement (stmt term)+ END #StmtDo2
          | call doStatement ('|' block_var '|')? term? (inner_comptstmt) END #StmtDo
          | UNDEF fname #StmtUndef
          | ALIAS fname fname #StmtAlias
          | stmt IF expr #StmtIf
          | stmt UNLESS expr #StmtUnless
          | stmt WHILE expr #StmtWhile
          | stmt UNTIL expr #StmtUntil
          | BEGIN LBRACE inner_comptstmt RBRACE #Stmt8
          | {!(_input.LT(1).getText().startsWith(defaultString)) && !(_input.LT(3).getText().startsWith(nodeString))}? lhs ASSIGN (command|function) ( DO ( '|' block_var? '|')*? term? inner_comptstmt END)? #Stmt10
          | expr #StmtExpr
          ;
              
expr :      mlhs ASSIGN mrhs        #Expr1
            | RETURN call_args #Expr3
            | YIELD call_args #Expr4
            | expr AND expr #ExprAnd // true or false
            | expr OR expr #ExprOr // true or false
            | NOT expr #ExprNot // true or false
            | command #ExprCommand
            | '!' command #ExprNotCommand
            | arg #ExprArg
            ;
            
call        : function
            | command
            ;

command :    {(!(_input.LT(1).getText().startsWith(nodeString)))}? operation call_args #OperationCallArgs
            | primary DOT operation call_args # OperationPrimary
            | primary COLON2 operation call_args #Command3
            | SUPER call_args   #Command4
            ;

function    : operation ( LRBRACKET call_args? RRBRACKET )?
            | primary DOT operation '(' (call_args)? ')' 
            | primary COLON2 operation '(' (call_args)? ')'
            | primary DOT operation
            | primary COLON2 operation
            | SUPER LRBRACKET (call_args)? RRBRACKET
            | SUPER
            ;
            
function_op : operation ( LRBRACKET call_args? RRBRACKET )?
            ;
                     
arg		: lhs ASSIGN arg                    #ArgAssign
		| lhs OP_ASGN arg                   #Arg5
		| arg DOT2 arg                      #Arg6
		| arg DOT3 arg                      #Arg7
		| arg PLUS arg                      #Arg8
		| arg MINUS arg                     #Arg9
		| arg MUL arg                       #Arg10
		| arg DIV arg                       #Arg11
		| arg MOD arg                       #Arg12
		| arg EXP arg                       #Arg13
		| PLUS arg                          #Arg14
		| MINUS arg                         #Arg15
		| arg BIT_OR arg                    #Arg16
		| arg BIT_XOR arg                   #Arg17
		| arg BIT_AND arg                   #Arg18
		| arg '<=>' arg                     #Arg19
		| arg GREATER arg                   #ArgGreater // true or false
		| arg GREATER_EQUAL arg             #ArgGreaterEqual // true or false
		| arg LESS arg                      #ArgLess    //true or false
		| arg LESS_EQUAL arg                #ArgLessEqual // true or false
		| arg EQUAL arg                     #ArgEqual //true or false
		| arg '===' arg                     #ArgSubsumption // true or false
		| arg '!=' arg                      #ArgNotEqual //true or false
		| arg '=~' arg                      #ArgIndexOf  // integer or nil
		| arg '!~' arg                      #Arg28
		| '!' arg                           #ArgNot
		| BIT_NOT arg                       #Arg30
		| arg BIT_SHL arg                   #Arg31
		| arg BIT_SHR arg                   #Arg32
		| arg AND arg                       #ArgAnd
		| arg OR arg                        #ArgOr
		| arg '?' arg COLON arg             #ArgTernary
		| DEFINED arg                       #Arg36
		| identifier symbol primary         #ArgResourceproperty
		| primary                           #ArgPrimary
		;
		
primary : LRBRACKET inner_comptstmt RRBRACKET                                         #PrimCompstmtInBrackets
        | literal                                                                       #PrimLiteral
        | INT                                                                           #PrimInt                       // integer
        | FLOAT                                                                         #PrimFloat // float
        | variable                                                                      #PrimVar
        | collection                                                                    #PrimCollection              // add
        | (TRUE|FALSE)                                                                  #PrimBoolean // true or false
        | ohaiArg LRBRACKET literal (',' literal)* RRBRACKET                            #PrimOhaiFunc  // true or false
        | primary COLON2 identifier                                                     #Prim9
        | COLON2 identifier                                                             #Prim10
        | primary LSBRACKET args? RSBRACKET                                             #Prim11
        | LSBRACKET (args (',')? )? RSBRACKET                                           #Prim12
        | '[]'                                                                          #PrimEmptyCollection
        | '{' (args|assocs (',')? )? '}'                                                #Prim14
        | RETURN ( '(' call_args? ')' )?                                                #Prim15
        | YIELD ( '(' call_args? ')' )?                                                 #Prim16
        | DEFINED '(' arg ')'                                                           #Prim17
        | primary DOT function                                                          #PrimFuncCall
        | primary DOT function '{' ('|' (block_var)? '|')? (inner_comptstmt) '}'                 #PrimFuncDef
        | function_op                                                                   #Prim20
        | if_statement                                                                  #IfStatement
        | unless_statement                                                              #UnlessStatement
        | WHILE expr doStatement inner_comptstmt END                                           #WhileStatement
        | UNTIL expr doStatement inner_comptstmt END                                           #UntilStatement            
        | CASE inner_comptstmt (WHEN when_args then inner_comptstmt)+ (ELSE then? inner_comptstmt)? END      #CaseStatement
        | def_statement                                              #Prim24
        ;
                   
if_statement : IF expr then inner_comptstmt+ (ELSIF expr then inner_comptstmt+)* (ELSE then? inner_comptstmt+)? END 
             ;
           
unless_statement: UNLESS expr then inner_comptstmt ( ELSE inner_comptstmt)? END
                ; 
        
def_statement: DEF fname argdecl term inner_comptstmt END 
            | DEF singleton ('.'|'::') fname argdecl inner_comptstmt  END
            ; 
        
when_args   : args (',' '*' arg)?
            | '*' arg
            ;
                 
then		: term
		    | THEN
		    | term THEN
		    ;

doStatement		: DO
                | DO term
		        ;            
                    
block_var   : lhs 
            | mlhs
            ;            
            
mlhs        : mlhs_item ',' (mlhs_item (',' mlhs_item)*)? ('*' (lhs)?)?
            | '*' lhs 
            ;            

mlhs_item   : lhs 
            | '(' mlhs ')'
            ;
            
lhs         : variable
            | primary LSBRACKET (args)? RSBRACKET
            | primary DOT identifier
            ;          
            
mrhs        : args (',' '*' arg)?
            | '*' arg
            ;            
      
call_args   : args                                              #CallargsArgs
            | args ( ',' assocs)? (',' '*' arg)? (',' '&' arg)? #Callargs2
            | assocs (',' '*' arg)? (',' '&' arg)?              #Callargs3
            | '*' arg (',' '&' arg)?                            #Callargs4
            | '&' arg                                           #Callargs5
            | command                                           #Callargs6
            ;
            
args        : arg (',' arg)*
            | arg  arg*
            ;

argdecl     : '(' arglist ')'
            | arglist term
            ;
            
arglist		: identifier (','identifier)* (',''*'(identifier)?)? (',''&'identifier)?
		    | '*'identifier (',''&'identifier)?
		    | ('&'identifier)?
		    ;            
            
singleton : variable
            | '(' expr ')'
            ;

assocs : assoc (',' assoc)*;

assoc : arg '=>' arg;

symbol  : COLON fname
        | COLON varname
        ;
        
fname   : FNAME
        | identifier
        |BIT_XOR | BIT_AND | BIT_OR | DOT2
        | EQUAL
        | GREATER | GREATER_EQUAL | LESS | LESS_EQUAL
        | PLUS | MINUS | MUL | DIV | MOD | EXP
        | BIT_SHL | BIT_SHR | BIT_NOT
        ;

ohaiArg : PLATFORM_FAMILY
          | PLATFORM;

operation : identifier
            | IDENTIFIER_FUNCTION
            | identifier NOT
            ;
            
collection : '%w' '('  ((term|arg)*)  ')' #WArray 
           | '[' mlhs ']' #Array;
                        
varname		: IDENTIFIER_GLOBAL
            | '@'IDENTIFIER
            | identifier
            ;
variable : varname
         | NIL
         | SELF
         ;
 
literal     : NUMERIC       #LitNumeric
            | symbol        #LitSymbol
            | LITERAL       #String
            | HERE_DOC      #LitHeredoc
            | REGEXP        #LitRegexp
            ;
             
identifier : IDENTIFIER;
       
term : SEMICOLON
     | CRLF
     ;           

fragment ESCAPED_QUOTE : '\\"';

LITERAL : '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"'
        | '\'' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '\''
        | '`' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '`';
     
END : 'end';
BEGIN : 'begin';

RETURN : 'return';
PIR : 'pir';
UNDEF : 'undef';
ALIAS : 'alias';

CASE : 'case';
IF: 'if';
ELSIF : 'elsif';
ELSE : 'else';
UNLESS : 'unless';
UNTIL : 'until';
WHILE : 'while';
RETRY : 'retry';
BREAK : 'break';
FOR : 'for';
YIELD : 'yield';
NUMERIC : 'numeric';
THEN : 'then';
DO : 'do';
DEFINED : 'defined?';
WHEN : 'when';
IN : 'in';
SUPER :'super';
DEF : 'def';

NIL : 'nil';
SELF : 'self';
TRUE : 'true';
FALSE: 'false';

PLATFORM_FAMILY : 'platform_family?';
PLATFORM : 'platform?';  

REGEXP : '/' (~('\n'|'\r'))* '/' ('i'|'o'|'p')?
        | '%' 'r' . (~('\n'|'\r'))* .
        ;
        
OP_ASGN		: '+=' | '-=' | '*=' | '/=' | '%=' | '**='
		| '&=' | '|=' | '^=' | '<<=' | '>>='
		| '&&=' | '||='
		;

PLUS : '+';
MINUS : '-';
MUL : '*';
DIV : '/';
MOD : '%';
EXP : '**';

EQUAL : '==';
GREATER : '>';
LESS : '<';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';

AND : 'and' | '&&';
OR : 'or' | '||';
NOT : 'not' | '!';

BIT_AND : '&';
BIT_OR : '|';
BIT_XOR : '^';
BIT_NOT : '~';
BIT_SHL : '<<';
BIT_SHR : '>>';

DOT : '.';
DOT2 : '..';
DOT3 : '...';

COLON : ':';
COLON2 :'::';
         
LRBRACKET : '(' {nesting++;};
RRBRACKET : ')' {nesting--;};
LSBRACKET : '[' {nesting++;};
RSBRACKET : ']' {nesting--;};
LBRACE : '{' {nesting++;};
RBRACE : '}' {nesting--;};

CRLF :  '\r'? '\n' {nesting==0}?;
CRLFSKIP : '\r'? '\n' {nesting>0}? -> skip;    
SEMICOLON : ';';

ASSIGN : '=';

SL_COMMENT : ('#' ~('\r' | '\n')* '\r'? '\n') -> skip;
ML_COMMENT : ('=begin' .*? '=end' '\r'? '\n') -> skip;
WS : (' '|'\t')+ -> skip;

INT : [0-9]+;
FLOAT : [0-9]*'.'[0-9]+;
IDENTIFIER : [a-zA-Z_][a-zA-Z0-9_]*;
FNAME		:  //'..' | '|' | '^' | '&'
  		  '<=>'  | '===' | '=~'
  		//| '=='
         //         | '>' | '>=' | '<' | '<='
  		//| '+' | '-' | '*' | '/' | '%' | '**'
  		//| '<<' | '>>' | '~'
                  | '+@' | '-@' | '[]' | '[]=' 
          ;

IDENTIFIER_GLOBAL : '$'IDENTIFIER
                    '$' .;
IDENTIFIER_FUNCTION : IDENTIFIER '?';
HERE_DOC : '<<' (IDENTIFIER| LITERAL) ( ESCAPED_QUOTE | ~('\n'|'\r') )*? IDENTIFIER;

ERROR_CHARACTER : . ;
