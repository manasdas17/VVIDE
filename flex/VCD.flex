package test;

import tokens.*;

import java.util.Stack;

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
%class Lexer
%unicode
%ignorecase
%type TokenType

%{
private StringBuffer sb = new StringBuffer();

public String getTokenText() {
	return sb.toString();
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

TimeUnit                         = "s" | "ms" | "us" | "ns" | "ps" | "fs"

ScopeType                        = "module" | "task" | "function" | "begin" | "fork"
ScopeName                        = {Identifier}

VarType                          = "event" | "integer" | "parameter" | "real" | "reg" | "supply0" | "supply1" | "time" | "tri" | "triand" | "trior" | "trireg" | "tri0" | "tri1" | "wand" | "wire" | "wor"
IdentifierCode                   = [!-~]+
Reference                        = ({Identifier}[ ]*"["{Digit}+":"{Digit}+"]") | ({Identifier}[ ]*"["{Digit}+"]") | {Identifier}

TimeStamp                        = "#"{Digit}+
BeginValue                       = [01xXzZrRbB]
ScalarValue                      = [01xXzZ]
RealValue                        = [rR]({Digit}|".")+
VectorValue                      = [bB][01xXzZ]+

OtherCommand                     ="$"{Letter}+

%state DATE
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
	"$date"                  {sb.setLength(0); yybegin(DATE);}
	"$timescale"             {sb.setLength(0); yybegin(TIMESCALE);}
	"$scope"                 {sb.setLength(0); yybegin(SCOPE);}
	"$upscope"{WhiteSpace}"$end" {return TokenType.UPSCOPE;}
	"$comment"               {sb.setLength(0); yybegin(COMMENT);}
	"$var"                   {sb.setLength(0); yybegin(VAR);}
	{OtherCommand}           {yybegin(IGNORED);}
	
	{TimeStamp}              {sb.setLength(0); sb.append(yytext().substring(1)); return TokenType.TIMESTAMP;}
	{BeginValue}             {yypushback(1); sb.setLength(0); yybegin(VALUE_CHANGE);}

	<<EOF>>                  {return TokenType.EOF;}
	{AllOther}               {}
}

<DATE> {
	"$end"                   {yybegin(YYINITIAL); return TokenType.DATE;}
	\\t | {LineTerminator}   {}
	.                        {sb.append( yytext() );}
}

<TIMESCALE> {
	"$end"                   {yybegin(YYINITIAL); return TokenType.TIMESCALE;}
	{Digit}+                 {sb.append( yytext() );}
	{TimeUnit}               {sb.append( yytext() );}
	{AllOther}               {}
}

<SCOPE> {
	"$end"                   {yybegin(YYINITIAL); return TokenType.SCOPE;}
	{ScopeType}              {sb.append(" type=").append( yytext());}
	{ScopeName}              {sb.append(" name=").append( yytext());}
	{AllOther}               {}
}

<VAR> {
	{VarType}                {sb.append(" type=").append( yytext()); yybegin( VAR_SIZE );}
	{AllOther}               {}
}

<VAR_SIZE> {
	{Digit}+                 {sb.append(" size=").append( yytext()); yybegin( VAR_ID );}
	{AllOther}               {}
}

<VAR_ID> {
	{IdentifierCode}         {sb.append(" ID=").append( yytext()); yybegin( VAR_REF );}
	{AllOther}               {}
}

<VAR_REF> {
	"$end"                   {yybegin( YYINITIAL ); return TokenType.VAR;}
	{Identifier}             {sb.append(" Reference=").append( yytext());}
	"["{Digit}+":"           {sb.append(" MSB=").append( yytext().substring(1, yytext().length() - 1));}
	{Digit}+"]"              {sb.append(" LSB=").append( yytext().substring(0, yytext().length() - 1));}
	"["{Digit}+"]"           {sb.append(" #=").append( yytext().substring(1, yytext().length() - 1));}
	{AllOther}               {}
}

<COMMENT> {
	"$end"                   {yybegin( YYINITIAL ); return TokenType.COMMENT;}
	{AllOther}               {sb.append( yytext() );}
}

<VALUE_CHANGE> {
	{RealValue}              {sb.append( "Real Value=" ).append( yytext().substring(1) ); yybegin(VALUE_CHANGE_ID); }
	{VectorValue}            {sb.append( "Vector Value=" ).append( yytext().substring(1) ); yybegin(VALUE_CHANGE_ID); }
	{ScalarValue}            {sb.append( "Scalar Value=" ).append( yytext() ); yybegin(VALUE_CHANGE_ID); }
}

<VALUE_CHANGE_ID> {
	{LineTerminator}         {yybegin(YYINITIAL); return TokenType.VALUE_CHANGE;}
	{IdentifierCode}         {sb.append( " SignalID=" ).append( yytext());}
	{AllOther}               {}
}

<IGNORED> {
	"$end"                   {yybegin(YYINITIAL); return TokenType.NULL;}
	{AllOther}               {}
}
