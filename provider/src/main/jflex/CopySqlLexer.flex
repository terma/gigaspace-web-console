/*
Copyright 2015 Artem Stasiuk

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

package com.github.terma.gigaspacesqlconsole.provider;

/*
Main construction is:
copy <TypeName> [reset <FieldName>[, <FieldName>]] [from <From>] [to <To>] [where conditions]
*/

/* --------------------------Usercode Section------------------------ */


import java_cup.sym;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;

%%
   
/* -----------------Options and Declarations Section----------------- */
   
/* 
   The name of the class JFlex will create will be Lexer.
   Will write the code to the file Lexer.java. 
*/
%class CopySqlLexer

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
   
/*
  Declarations
   
  Code between %{ and %}, both of which must be at the beginning of a
  line, will be copied letter to letter into the lexer class source.
  Here you declare member variables and functions that are used inside
  scanner actions.  
*/
%{

    private boolean copyStatement = false;

    private String typeName = "";
    private String where = "";
    private Set<String> reset = new HashSet<>();
    private String from;
    private String only;

    public CopySql getSql() {
        Integer fromInt = null;
        if (from != null) fromInt = Integer.parseInt(from);

        Integer onlyInt = null;
        if (only != null) onlyInt = Integer.parseInt(only);

        return new CopySql(typeName, reset, where, fromInt, onlyInt);
    }

%}
   

/*
  Macro Declarations
  
  These declarations are regular expressions that will be used latter
  in the Lexical Rules Section.  
*/
   
/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or line feed. */
WhiteSpace = {LineTerminator} | [ \t\f]

copyKeyword = copy
typeName = [A-Za-z_][-.A-Za-z_0-9]*

resetKeyword = reset
fieldName = [A-Za-z_][-.A-Za-z_0-9]*
nextResetField = ,

whereKeyword = where
where = [^]+

fromKeyword = from
from = [0-9]+
onlyKeyword = only
only = [0-9]+


%state COPY_KEYWORD

%state TYPE_NAME
%state AFTER_TYPE_NAME

%state RESET_FIELD
%state AFTER_RESET_FIELD

%state FROM_VALUE
%state AFTER_FROM
%state ONLY_VALUE
%state AFTER_ONLY

%state WHERE

%%
/* ------------------------Lexical Rules Section---------------------- */
   
/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */
   
   /* YYINITIAL is the state at which the lexer begins scanning.  So
   these regular expressions will only be matched if the scanner is in
   the start state YYINITIAL. */
   
<YYINITIAL> {
    {copyKeyword} { copyStatement = true; yybegin(TYPE_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<TYPE_NAME> {
    {typeName} { typeName = yytext(); yybegin(AFTER_TYPE_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<AFTER_TYPE_NAME> {
    {resetKeyword} { yybegin(RESET_FIELD); }
    {fromKeyword} { yybegin(FROM_VALUE); }
    {onlyKeyword} { yybegin(ONLY_VALUE); }
    {whereKeyword} { yybegin(WHERE); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<RESET_FIELD> {
    {fieldName} { reset.add(yytext()); yybegin(AFTER_RESET_FIELD); }
    {nextResetField} { yybegin(RESET_FIELD); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<AFTER_RESET_FIELD> {
    {nextResetField} { yybegin(RESET_FIELD); }
    {fromKeyword} { yybegin(FROM_VALUE); }
    {onlyKeyword} { yybegin(ONLY_VALUE); }
    {whereKeyword} { yybegin(WHERE); }
    {WhiteSpace} { /* do nothing */ }
}

<WHERE> {
    {where} { where = yytext(); }
}

<FROM_VALUE> {
    {from} { from = yytext(); yybegin(AFTER_FROM); }
    {WhiteSpace} { /* do nothing */ }
}

<AFTER_FROM> {
    {onlyKeyword} { yybegin(ONLY_VALUE); }
    {whereKeyword} { yybegin(WHERE); }
    {WhiteSpace} { /* do nothing */ }
}

<ONLY_VALUE> {
    {only} { only = yytext(); yybegin(AFTER_ONLY); }
    {WhiteSpace} { /* do nothing */ }
}

<AFTER_ONLY> {
    {whereKeyword} { yybegin(WHERE); }
    {WhiteSpace} { /* do nothing */ }
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^] {
    if (copyStatement) throw new IOException("Illegal character <" + yytext() + "> near: " + zzStartRead);
    else throw new UnsupportedOperationException();
}
