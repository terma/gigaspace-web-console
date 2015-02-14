package com.github.terma.gigaspacesqlconsole.provider;

/*
Main construction is:
generate count of spaceDocumentType
generate count of spaceDocumentType with fieldName = fieldValue
generate count of spaceDocumentType with fieldName = fieldValue and fieldName1 = fieldValue1
*/

/* --------------------------Usercode Section------------------------ */


import java_cup.sym;
//import java_cup.runtime.*;
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

    public CopySql getSql() { return new CopySql(typeName, reset, where); }

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

%state TYPE_NAME

%state COPY_KEYWORD

%state RESET_KEYWORD
%state RESET_FIELD
%state AFTER_RESET_FIELD

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
    {typeName} { typeName = yytext(); yybegin(RESET_KEYWORD); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<RESET_KEYWORD> {
    {resetKeyword} { yybegin(RESET_FIELD); }
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
    {whereKeyword} { yybegin(WHERE); }
    {WhiteSpace} { /* do nothing */ }
}

<WHERE> {
    {where} { where = yytext(); }
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^] {
    if (copyStatement) throw new IOException("Illegal character <"+yytext()+">");
    else throw new UnsupportedOperationException();
}
