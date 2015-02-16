package com.github.terma.gigaspacesqlconsole.provider;
   
/* --------------------------Usercode Section------------------------ */


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
%class UpdateSqlLexer

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

    private boolean updateStatement = false;

    private String typeName = "";

    private String tempSetFieldName;
    private Map<String, Object> setFields = new HashMap<String, Object>();

    private String conditions = "";

    public UpdateSql getSql() { return new UpdateSql(typeName, setFields, conditions); }

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

updateKeyword = update
typeName = [A-Za-z_][-.A-Za-z_0-9]*
setKeyword = set

fieldName = [A-Za-z_][-.A-Za-z_0-9]*
assinmentOperation = =
nextSetField = ,
fieldValueString = '[^\']*'
fieldValueBoolean = true | false | TRUE | FALSE
fieldValueNumber = -?[0-9]+

conditions = [^]+
conditionsKeyword = where

%state TYPE_NAME

%state SET_KEYWORD

%state SET_FIELD_NAME
%state SET_FIELD_ASSIGNMENT
%state SET_FIELD_VALUE_START
%state SET_FIELD_VALUE
%state SET_FIELD_VALUE_END
%state SET_FIELD_MORE

%state CONDITIONS

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
    {updateKeyword} { updateStatement = true; yybegin(TYPE_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<TYPE_NAME> {
    {typeName} { typeName = yytext(); yybegin(SET_KEYWORD); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<SET_KEYWORD> {
    {setKeyword} { yybegin(SET_FIELD_NAME); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<SET_FIELD_NAME> {
    {fieldName} { tempSetFieldName = yytext(); yybegin(SET_FIELD_ASSIGNMENT); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<SET_FIELD_ASSIGNMENT> {
    {assinmentOperation} { yybegin(SET_FIELD_VALUE); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<SET_FIELD_VALUE> {
    {fieldValueBoolean} { setFields.put(tempSetFieldName, Boolean.parseBoolean(yytext().toLowerCase())); yybegin(SET_FIELD_MORE); }
    {fieldValueNumber} { setFields.put(tempSetFieldName, Long.parseLong(yytext())); yybegin(SET_FIELD_MORE); }
    {fieldValueString} { setFields.put(tempSetFieldName, yytext().substring(1, yytext().length() - 1)); yybegin(SET_FIELD_MORE); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<SET_FIELD_MORE> {
    {nextSetField} { yybegin(SET_FIELD_NAME); }
    {conditionsKeyword} { yybegin(CONDITIONS); }
    {WhiteSpace} { /* just skip what was found, do nothing */ }
}

<CONDITIONS> {
    {conditions} { conditions = yytext(); }
}


/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^] {
    if (updateStatement) throw new IOException("Illegal character <"+yytext()+">");
    else throw new UnsupportedOperationException();
}
