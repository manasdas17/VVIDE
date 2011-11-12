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

/*
 * Generated on 7/29/11 2:06 PM
 */
package vvide.ui.views.editor.vhdl;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;

%%

%public
%class VHDLLexer
%extends AbstractJFlexTokenMaker
%unicode
%ignorecase
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public VHDLLexer() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * Returns the text to place at the beginning and end of a
	 * line to "comment" it in a this programming language.
	 *
	 * @return The start and end strings to add to a line to "comment"
	 *         it out.
	 */
	public String[] getLineCommentStartAndEnd() {
		return new String[]{"--", null};
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
			/* No multi-line comments */
			/* No documentation comments */
			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new DefaultToken();
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(java.io.Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

Letter					= [A-ZÄÖÜa-zäöü]
LetterOrUnderscore			= ({Letter}|"_")
NonzeroDigit				= [1-9]
Digit					= ("0"|{NonzeroDigit})
HexDigit				= ({Digit}|[A-Fa-f])
OctalDigit				= ([0-7])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
NonSeparator				= ([^\t\f\r\n\ \(\)\{\}\[\]\,\.\=\>\<\!\~\?\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierPart				= ({LetterOrUnderscore}|{Digit})

LineTerminator				= (\n)
WhiteSpace				= ([ \t\f]+)

CharLiteral				= (\'({LetterOrUnderscore}|{Digit})*\')

StringLiteral				= (\"({LetterOrUnderscore}|{Digit})*\")

/* No multi-line comments */
/* No documentation comments */
LineCommentBegin			= "--"

IntegerHelper1				= (({NonzeroDigit}{Digit}*)|"0")
IntegerHelper2				= ("0"(([xX]{HexDigit}+)|({OctalDigit}*)))
IntegerLiteral				= ({IntegerHelper1}[lL]?)
HexLiteral				= ({IntegerHelper2}[lL]?)
FloatHelper1				= ([fFdD]?)
FloatHelper2				= ([eE][+-]?{Digit}+{FloatHelper1})
FloatLiteral1				= ({Digit}+"."({FloatHelper1}|{FloatHelper2}|{Digit}+({FloatHelper1}|{FloatHelper2})))
FloatLiteral2				= ("."{Digit}+({FloatHelper1}|{FloatHelper2}))
FloatLiteral3				= ({Digit}+{FloatHelper2})
FloatLiteral				= ({FloatLiteral1}|{FloatLiteral2}|{FloatLiteral3}|({Digit}+[fFdD]))
BooleanLiteral				= ("true"|"false")

Separator				= ([\(\)\{\}\[\]])
Separator2				= ([\:;,.])

Identifier				= ({LetterOrUnderscore}{IdentifierPart}*)

URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved				= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter				= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters				= ({URLCharacter}*)
URLEndCharacter				= ([\/\$]|{Letter}|{Digit})
URL					= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)


/* No string state */
/* No char state */
/* No MLC state */
/* No documentation comment state */
%state EOL_COMMENT

%%

<YYINITIAL> {

/* Keywords */
"architecture" 	|
"after" |
"assert" 	|
"begin" 	|
"case" 		|
"component" 	|
"configuration" |
"constant" 	|
"downto" 	|
"end" 		|
"else" 		|
"elsif" 	|
"entity"	|
"error"		|
"file" 		|
"for" 		|
"function" 	|
"generic" 	|
"if" 		|
"in" 		|
"is" 		|
"library" 	|
"loop" 		|
"map" 		|
"note" 		|
"of" 		|
"others" 	|
"out"		|
"package" 	|
"port" 		|
"process" 	|
"range"		|
"report" 	|
"return" 	|
"select" 	|
"severity" 	|
"shared variable" |
"signal" 	|
"then" 		|
"to"		|
"type" 		|
"use" 		|
"variable" 	|
"wait" 		|
"warning"	|
"when" 		|
"while" 	|
"ms" |
"ps" |
"us" |
"ns" |
"fs" |
"with"		{ addToken(Token.RESERVED_WORD); }

/* Data types */
"access" 	|
"array" 	|
"file" 		|
"integer" 	|
"null" 		|
"record" 	|
"std_logic" 	|
"std_logic_vector" { addToken(Token.DATA_TYPE); }

	/* Functions */
"'active" 	|
"'event" 	|
"'left" 	|
"'low" 		|
"'right" 	|
"signed" 	|
"to_integer" 	|
"unsigned"	{ addToken(Token.FUNCTION); }

	/* Operators. */
"&" 		|
"*" 		|
"+" 		|
"-" 		|
"/" 		|
"/=" 		|
":="		|
"<" 		|
"<=" 		|
"=" 		|
">" 		|
">=" 		|
"abs" 		|
"and" 		|
"mod" 		|
"nand" 		|
"nor" 		|
"not" 		|
"or" 		|
"rem" 		|
"rol" 		|
"ror" 		|
"sla" 		|
"sll" 		|
"sra" 		|
"srl" 		|
"xor"		{ addToken(Token.OPERATOR); }

{LineTerminator}	{ addNullToken(); return firstToken; }
{Identifier}		{ addToken(Token.IDENTIFIER); }
{WhiteSpace}		{ addToken(Token.WHITESPACE); }

	/* String/Character literals. */
{CharLiteral}		{ addToken(Token.LITERAL_CHAR); }
{StringLiteral}		{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }

	/* Comment literals. */
	/* No multi-line comments */
	/* No documentation comments */
{LineCommentBegin}	{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

	/* Separators. */
{Separator}		{ addToken(Token.SEPARATOR); }
{Separator2}		{ addToken(Token.IDENTIFIER); }

	/* Numbers */
{IntegerLiteral}	{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
{HexLiteral}		{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
{FloatLiteral}		{ addToken(Token.LITERAL_NUMBER_FLOAT); }

	/* Ended with a line not in a string or comment. */
<<EOF>>			{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters. */
.			{ addToken(Token.IDENTIFIER); }
}

<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}
