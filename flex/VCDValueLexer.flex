package vvide.parser;

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
%class VCDValueLexer
%extends AbstractValueLexer
%unicode
%char
%type Object

%{
	/**
	 * Set a current timestamp
	 */
	protected void setTimeStamp( Long timestamp ) {
		countChangesAdded++;
		this.timestamp = timestamp;
		if ( (countChangesAdded & 0x1FFF) == 0)  {
			countChangesAdded = 0;
			parser.setProgress( (int) (((long)yychar)*100 / fileSize));
		}
	}
%}

LineTerminator                   = \r|\n|\r\n
Letter                           = [A-Za-z]
Digit                            = [0-9]
AllOther                         = \n | .

IdentifierCode                   = [!-~]+

TimeStamp                        = "#"{Digit}+
ScalarValue                      = [01xXzZ]
RealValue                        = [rR]({Digit}|".")+
VectorValue                      = [bB][01xXzZ]+

OtherCommand                     ="$"{Letter}+

%state IGNORED
%state VALUE_CHANGE_ID

%%
<YYINITIAL> {
	"$dumpvars"              {}
	{OtherCommand}           {yybegin(IGNORED);}
	
	{TimeStamp}              {if (interrupted) return null; setTimeStamp( Long.valueOf( yytext().substring(1) ) );}
	{RealValue}              {value = yytext().substring(1); yybegin(VALUE_CHANGE_ID); }
	{VectorValue}            {value = yytext().substring(1); yybegin(VALUE_CHANGE_ID); }
	{ScalarValue}            {value = yytext(); yybegin(VALUE_CHANGE_ID); }

	<<EOF>>                  {return null;}
	{AllOther}               {}
}

<VALUE_CHANGE_ID> {
	{LineTerminator}         {yybegin(YYINITIAL);}
	{IdentifierCode}         {addVarChange ( yytext() );}
	{AllOther}               {}
}

<IGNORED> {
	~"$end"                   {yybegin(YYINITIAL);}
}
