/*
 * Copyright 2013 atWare, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.atware.solr.analizers.cjk.scanner;

import java.io.Reader;

import jp.co.atware.solr.analizers.cjk.CJKTokenizer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class CJKTokenizerScanner {

    /** This character denotes the end of file */
    public static final int YYEOF = -1;

    /** initial size of the lookahead buffer */
    private static final int ZZ_BUFFERSIZE = 4096;

    /** lexical states */
    public static final int YYINITIAL = 0;

    /**
     * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
     * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l at the
     * beginning of a line l is of the form l = 2*k, k a non negative integer
     */
    private static final int ZZ_LEXSTATE[] = { 0, 0 };

    /**
     * ZZ_CMAP
     */
    private final char[] ZZ_CMAP;

    /**
     * Translates DFA states to action switch labels.
     */
    private final int[] ZZ_ACTION;

    /**
     * The transition table of the DFA
     */
    private final int[][] ZZ_TRANS;

    /* error codes */
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;

    /* error messages for the codes above */
    private static final String ZZ_ERROR_MSG[] = {
            "Unkown internal scanner error", "Error: could not match input",
            "Error: pushback value was too large" };

    /** the input device */
    private java.io.Reader zzReader;

    /** the current state of the DFA */
    private int zzState;

    /** the current lexical state */
    private int zzLexicalState = YYINITIAL;

    /**
     * this buffer contains the current text to be matched and is the source of
     * the yytext() string
     */
    private char zzBuffer[] = new char[ZZ_BUFFERSIZE];

    /** the textposition at the last accepting state */
    private int zzMarkedPos;

    /** the current text position in the buffer */
    private int zzCurrentPos;

    /** startRead marks the beginning of the yytext() string in the buffer */
    private int zzStartRead;

    /**
     * endRead marks the last character in the buffer, that has been read from
     * input
     */
    private int zzEndRead;

    /** the number of characters up to the start of the matched text */
    private int yychar;

    /** zzAtEOF == true <=> the scanner is at the EOF */
    private boolean zzAtEOF;

    /**
     * Chars in class \p{Line_Break = Complex_Context} are from South East Asian
     * scripts (Thai, Lao, Myanmar, Khmer, etc.). Sequences of these are kept
     * together as as a single token rather than broken up, because the logic
     * required to break them at word boundaries is too complex for UAX#29.
     * <p>
     * See Unicode Line Breaking Algorithm:
     * http://www.unicode.org/reports/tr14/#SA
     */
    public static final int WORD_TYPE = CJKTokenizer.WORD;
    public static final int NGRAM_TYPE = CJKTokenizer.NGRAM;
    public static final int NON_CHARACTER_TYPE = CJKTokenizer.NON_CHARACTER;

    public CJKTokenizerScanner(Reader in, char[] zzCMap, int[] zzAction,
            int[][] zzTrans) {
        this.zzReader = in;
        this.ZZ_ACTION = zzAction;
        this.ZZ_CMAP = zzCMap;
        this.ZZ_TRANS = zzTrans;
    }

    public final int yychar() {
        return yychar;
    }

    /**
     * Fills CharTermAttribute with the current token text.
     */
    public final void getText(CharTermAttribute t) {
        t.copyBuffer(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    /**
     * Refills the input buffer.
     * @return <code>false</code>, iff there was new input.
     * @exception java.io.IOException if any I/O-Error occurs
     */
    private boolean zzRefill() throws java.io.IOException {

        /* first: make room (if you can) */
        if (zzStartRead > 0) {
            System.arraycopy(zzBuffer, zzStartRead, zzBuffer, 0, zzEndRead
                    - zzStartRead);

            /* translate stored positions */
            zzEndRead -= zzStartRead;
            zzCurrentPos -= zzStartRead;
            zzMarkedPos -= zzStartRead;
            zzStartRead = 0;
        }

        /* is the buffer big enough? */
        if (zzCurrentPos >= zzBuffer.length) {
            /* if not: blow it up */
            char newBuffer[] = new char[zzCurrentPos * 2];
            System.arraycopy(zzBuffer, 0, newBuffer, 0, zzBuffer.length);
            zzBuffer = newBuffer;
        }

        /* finally: fill the buffer with new input */
        int numRead = zzReader.read(zzBuffer, zzEndRead, zzBuffer.length
                - zzEndRead);

        if (numRead > 0) {
            zzEndRead += numRead;
            return false;
        }
        // unlikely but not impossible: read 0 characters, but not at end of stream    
        if (numRead == 0) {
            int c = zzReader.read();
            if (c == -1) {
                return true;
            } else {
                zzBuffer[zzEndRead++] = (char) c;
                return false;
            }
        }

        // numRead < 0
        return true;
    }

    /**
     * Closes the input stream.
     */
    public final void yyclose() throws java.io.IOException {
        zzAtEOF = true; /* indicate end of file */
        zzEndRead = zzStartRead; /* invalidate buffer */

        if (zzReader != null)
            zzReader.close();
    }

    /**
     * Resets the scanner to read from a new input stream. Does not close the
     * old reader. All internal variables are reset, the old input stream
     * <b>cannot</b> be reused (internal buffer is discarded and lost). Lexical
     * state is set to <tt>ZZ_INITIAL</tt>. Internal scan buffer is resized down
     * to its initial length, if it has grown.
     * @param reader the new input stream
     */
    public final void yyreset(java.io.Reader reader) {
        zzReader = reader;
        zzAtEOF = false;
        zzEndRead = zzStartRead = 0;
        zzCurrentPos = zzMarkedPos = 0;
        yychar = 0;
        zzLexicalState = YYINITIAL;
        if (zzBuffer.length > ZZ_BUFFERSIZE)
            zzBuffer = new char[ZZ_BUFFERSIZE];
    }

    /**
     * Returns the current lexical state.
     */
    public final int yystate() {
        return zzLexicalState;
    }

    /**
     * Enters a new lexical state
     * @param newState the new lexical state
     */
    public final void yybegin(int newState) {
        zzLexicalState = newState;
    }

    /**
     * Returns the text matched by the current regular expression.
     */
    public final String yytext() {
        return new String(zzBuffer, zzStartRead, zzMarkedPos - zzStartRead);
    }

    /**
     * Returns the character at position <tt>pos</tt> from the matched text. It
     * is equivalent to yytext().charAt(pos), but faster
     * @param pos the position of the character to fetch. A value from 0 to
     *            yylength()-1.
     * @return the character at position pos
     */
    public final char yycharat(int pos) {
        return zzBuffer[zzStartRead + pos];
    }

    /**
     * Returns the length of the matched text region.
     */
    public final int yylength() {
        return zzMarkedPos - zzStartRead;
    }

    /**
     * Reports an error that occured while scanning. In a wellformed scanner (no
     * or only correct usage of yypushback(int) and a match-all fallback rule)
     * this method will only be called with things that "Can't Possibly Happen".
     * If this method is called, something is seriously wrong (e.g. a JFlex bug
     * producing a faulty scanner etc.). Usual syntax/scanner level error
     * handling should be done in error fallback rules.
     * @param errorCode the code of the errormessage to display
     */
    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        } catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
        }

        throw new Error(message);
    }

    /**
     * Pushes the specified amount of characters back into the input stream.
     * They will be read again by then next call of the scanning method
     * @param number the number of characters to be read again. This number must
     *            not be greater than yylength()!
     */
    public void yypushback(int number) {
        if (number > yylength())
            zzScanError(ZZ_PUSHBACK_2BIG);

        zzMarkedPos -= number;
    }

    /**
     * Resumes scanning until the next regular expression is matched, the end of
     * input is encountered or an I/O-Error occurs.
     * @return the next token
     * @exception java.io.IOException if any I/O-Error occurs
     */
    public int getNextToken() throws java.io.IOException {
        int zzInput;
        int zzAction;

        // cached fields:
        int zzCurrentPosL;
        int zzMarkedPosL;
        int zzEndReadL = zzEndRead;
        char[] zzBufferL = zzBuffer;
        char[] zzCMapL = ZZ_CMAP;
        int[][] zzTransL = ZZ_TRANS;

        while (true) {
            zzMarkedPosL = zzMarkedPos;

            yychar += zzMarkedPosL - zzStartRead;

            zzAction = -1;

            zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

            zzState = ZZ_LEXSTATE[zzLexicalState];

            zzForAction: {
                while (true) {

                    if (zzCurrentPosL < zzEndReadL)
                        zzInput = zzBufferL[zzCurrentPosL++];
                    else if (zzAtEOF) {
                        zzInput = YYEOF;
                        break zzForAction;
                    } else {
                        // store back cached positions
                        zzCurrentPos = zzCurrentPosL;
                        zzMarkedPos = zzMarkedPosL;
                        boolean eof = zzRefill();
                        // get translated positions and possibly new buffer
                        zzCurrentPosL = zzCurrentPos;
                        zzMarkedPosL = zzMarkedPos;
                        zzBufferL = zzBuffer;
                        zzEndReadL = zzEndRead;
                        if (eof) {
                            zzInput = YYEOF;
                            break zzForAction;
                        } else {
                            zzInput = zzBufferL[zzCurrentPosL++];
                        }
                    }
                    int zzNext = zzTransL[zzState][zzCMapL[zzInput]];
                    if (zzNext == -1)
                        break zzForAction;
                    zzState = zzNext;
                    zzAction = zzState;
                    zzMarkedPosL = zzCurrentPosL;
                }
            }

            // store back cached position
            zzMarkedPos = zzMarkedPosL;

            zzAction = zzAction < 0 ? zzAction : ZZ_ACTION[zzAction];
            switch (zzAction) {
            case NON_CHARACTER_TYPE: {
                /*
                 * Not numeric, word, kanji, hiragana, katakana or SE Asian or
                 * other character -- ignore it.
                 */
                break;
            }
            case WORD_TYPE:
            case NGRAM_TYPE: {
                return zzAction;
            }
            default:
                if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
                    zzAtEOF = true;
                    {
                        return YYEOF;
                    }
                } else {
                    zzScanError(ZZ_NO_MATCH);
                }
            }
        }
    }
}
