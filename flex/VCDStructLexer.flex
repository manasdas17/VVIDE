package vvide.parser;

import vvide.signal.Scope;

/*
 * This file is part of the VVIDE project.
 * 
 * Copyright (C) 2011 Pavel Fischer rubbiroid@gmail.com
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

%%
%public
%class VCDStructLexer
%extends AbstractStructLexer
%unicode
%char
%type Object

%{
	/**
	 * Add new timestamp
	 */
	protected void addTimeStamp( Long timestamp ) {
		countChangesAdded++;
		minInterval = Math.min( getMinInterval(), (timestamp - lastTimestamp) );
		lastTimestamp = timestamp;
		if ( (countChangesAdded & 0x1FFF) == 0)  {
			countChangesAdded = 0;
			parser.setProgress( (int) (((long)yychar)*100 / fileSize));
		}
	}
%}

LineTerminator                   = \r|\n|\r\n
WhiteSpace                       = {LineTerminator} | [ \t\f]
Letter                           = [A-Za-z]
LetterOrUnderscore               = ({Letter}|"_")
Digit                            = [0-9]
IdentifierStart                  = {LetterOrUnderscore} | (\\{Digit})
Identifier                       = {IdentifierStart} ({LetterOrUnderscore} | {Digit} | [$])*
AllOther                         = \n | .

TimeUnit                         = [munpf]?"s"

ScopeType                        = "module" | "task" | "function" | "begin" | "fork"
ScopeName                        = {Identifier}

VarType                          = "event" | "integer" | "parameter" | "real" | "reg" | "supply0" | "supply1" | "time" | "tri" | "triand" | "trior" | "trireg" | "tri0" | "tri1" | "wand" | "wire" | "wor"
IdentifierCode                   = [!-~]+

TimeStamp                        = "#"{Digit}+
BeginValue                       = [01xXzZrRbB]
ScalarValue                      = [01xXzZ]
RealValue                        = [rR]({Digit}|".")+
VectorValue                      = [bB][01xXzZ]+

OtherCommand                     ="$"{Letter}+

%state IGNORED
%state TIMESCALE
%state SCOPE
%state VAR
%state VAR_SIZE
%state VAR_ID
%state VAR_REF
%state VALUE_CHANGE
%state VALUE_CHANGE_ID
%state COMMENT

%%
<YYINITIAL> {
	"$timescale"             {yybegin(TIMESCALE);}
	"$scope"                 {createNewScope(); yybegin(SCOPE);}
	"$upscope"{WhiteSpace}"$end" {setCurrentScope((Scope)getCurrentScope().getParent() );}
	"$enddefinitions"{WhiteSpace}"$end" {createChangesMap();}
	"$comment"               {commentBuffer.append("Found comment: "); yybegin(COMMENT);}
	"$var"                   {if (interrupted) return null; beginVar(); yybegin(VAR);}
	"$dumpvars"              {}
	{OtherCommand}           {yybegin(IGNORED);}
	
	{TimeStamp}              {if (interrupted) return null; addTimeStamp( Long.valueOf( yytext().substring(1) ) );}
	{BeginValue}             {yypushback(1); yybegin(VALUE_CHANGE);}

	<<EOF>>                  {return null;}
	{AllOther}               {}
}

<TIMESCALE> {
	"$end"                   {yybegin(YYINITIAL);}
	{Digit}+                 {setTimeScaleValue( Integer.valueOf( yytext() ) );}
	{TimeUnit}               {setTimeScaleUnit( yytext() );}
	{AllOther}               {}
}

<SCOPE> {
	"$end"                   {yybegin(YYINITIAL);}
	{ScopeType}              {currentScope.setType( yytext() );}
	{ScopeName}              {currentScope.setName( yytext() );}
	{AllOther}               {}
}

<VAR> {
	{VarType}                {var.setType( yytext() ); yybegin( VAR_SIZE );}
	{AllOther}               {}
}

<VAR_SIZE> {
	{Digit}+                 {var.setBitWidth( Integer.valueOf( yytext() ) ); yybegin( VAR_ID );}
	{AllOther}               {}
}

<VAR_ID> {
	{IdentifierCode}         {var.setId( yytext() ); yybegin( VAR_REF );}
	{AllOther}               {}
}

<VAR_REF> {
	"$end"                   {yybegin( YYINITIAL ); endVar();}
	{Identifier}             {var.setReference( yytext() );}
	"["{Digit}+":"           {var.setMsb( Integer.valueOf( yytext().substring(1, yytext().length() - 1) ) );}
	{Digit}+"]"              {var.setLsb( Integer.valueOf( yytext().substring(0, yytext().length() - 1) ) );}
	"["{Digit}+"]"           {var.setBitNr( Integer.valueOf( yytext().substring(1, yytext().length() - 1) ) ); appendToCompoundVar(); }
	{AllOther}               {}
}

<COMMENT> {
	"$end"                   {yybegin( YYINITIAL ); printComment(); }
	{AllOther}               {commentBuffer.append( yytext() );}
}

<VALUE_CHANGE> {
	{RealValue}              {yybegin(VALUE_CHANGE_ID); }
	{VectorValue}            {yybegin(VALUE_CHANGE_ID); }
	{ScalarValue}            {yybegin(VALUE_CHANGE_ID); }
}

<VALUE_CHANGE_ID> {
	{LineTerminator}         {yybegin(YYINITIAL);}
	{IdentifierCode}         {addVarChange ( yytext() );}
	{AllOther}               {}
}

<IGNORED> {
	~"$end"                   {yybegin(YYINITIAL);}
}
