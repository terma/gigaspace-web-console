/*
Copyright 2015-2016 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.gigaspacewebconsole.provider.executor.gigaspace;
   
/* --------------------------Usercode Section------------------------ */

/*

randomString(maxLength, value)

generate 12 of Customer with name = 'Misha', id = random
generate count of typeName with fieldName = fieldValue, fieldName1 = fieldValue2
generate 90 of Customer with fieldName = randomString('My name is ${random}')
generate 90 of Customer with fieldName = randomDouble()
generate 90 of Customer with fieldName = randomLong()
generate 90 of Customer with fieldName = randomInt()
*/

import java_cup.sym;
//import java_cup.runtime.*;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

%%
   
/* -----------------Options and Declarations Section----------------- */
   
/* 
   The name of the class JFlex will create will be Lexer.
   Will write the code to the file Lexer.java. 
*/
%class GenerateSqlLexer

/*
  The current line number can be accessed with the variable yyline
  and the current column number with the variable yycolumn.
*/
%line
%column
    
/* 
   Will switch to a CUP compatibility mode to interface with a CUP
   generated parser.
*/
%cup
   
/*Declarations */
%{


    private String tempFieldName;

    private boolean generateStatement = false;

    private String typeName = "";
    private int count = 1;
    private Map<String, Object> fields = new HashMap<String, Object>();

    public GenerateSql getSql() { return new GenerateSql(typeName, count, fields); }

%}
   

/*Macro Declarations */
   
/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace = {LineTerminator} | [ \t\f]

generateKeyword = generate
count = [0-9]+

ofKeyword = of
typeName = [A-Za-z_][-.A-Za-z_0-9]*

withKeyword = with
fieldName = [A-Za-z_][-.A-Za-z_0-9]*
assinmentOperation = =
nextField = ,
fieldValueString = '[^\']*'
fieldValueBoolean = true | false | TRUE | FALSE
fieldValueNumber = -?[0-9]+

%state COUNT
%state OF
%state TYPE_NAME
%state WITH_KEYWORD
%state FIELD_NAME
%state FIELD_ASSIGNMENT
%state FIELD_VALUE_START
%state FIELD_VALUE
%state FIELD_VALUE_END
%state FIELD_MORE

%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> {
    {generateKeyword} { generateStatement = true; yybegin(COUNT); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<COUNT> {
    {count} { count = Integer.parseInt(yytext()); yybegin(OF); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<OF> {
    {ofKeyword} { yybegin(TYPE_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<TYPE_NAME> {
    {typeName} { typeName = yytext(); yybegin(WITH_KEYWORD); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<WITH_KEYWORD> {
    {withKeyword} { yybegin(FIELD_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<FIELD_NAME> {
    {fieldName} { tempFieldName = yytext(); yybegin(FIELD_ASSIGNMENT); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<FIELD_ASSIGNMENT> {
    {assinmentOperation} { yybegin(FIELD_VALUE); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<FIELD_VALUE> {
    {fieldValueBoolean} { fields.put(tempFieldName, Boolean.parseBoolean(yytext().toLowerCase())); yybegin(FIELD_MORE); }
    {fieldValueNumber} { fields.put(tempFieldName, Long.parseLong(yytext())); yybegin(FIELD_MORE); }
    {fieldValueString} { fields.put(tempFieldName, yytext().substring(1, yytext().length() - 1)); yybegin(FIELD_MORE); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<FIELD_MORE> {
    {nextField} { yybegin(FIELD_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^] {
    if (generateStatement) throw new IOException("Illegal character <"+yytext()+">");
    else throw new UnsupportedOperationException();
}
