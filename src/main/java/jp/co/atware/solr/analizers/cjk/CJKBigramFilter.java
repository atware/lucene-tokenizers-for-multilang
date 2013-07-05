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

package jp.co.atware.solr.analizers.cjk;

import java.io.IOException;

import jp.co.atware.solr.analizers.cjk.attributes.PosticheOffsetAttribute;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.ArrayUtil;

public final class CJKBigramFilter extends TokenFilter {

    /** when we emit a bigram, its then marked as this type */
    public static final String DOUBLE_TYPE = "<DOUBLE>";
    /** when we emit a unigram, its then marked as this type */
    public static final String SINGLE_TYPE = "<SINGLE>";

    /** if true output unigram tokens */
    private final boolean outputUnigrams;
    private boolean ngramState; // false = output unigram, true = output bigram

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionLengthAttribute posLengthAtt = addAttribute(PositionLengthAttribute.class);
    private final PosticheOffsetAttribute posticheOffsetAtt = addAttribute(PosticheOffsetAttribute.class);

    // buffers containing codepoint and offsets in parallel
    int buffer[] = new int[8];
    int startOffset[] = new int[8];
    int endOffset[] = new int[8];
    // length of valid buffer
    int bufferLen;
    // current buffer index
    int index;

    // the last end offset, to determine if we should bigram across tokens
    int lastEndOffset;
    int posticheLastEndOffset;

    private boolean exhausted;

    public CJKBigramFilter(TokenStream in) {
        this(in, false);
    }

    public CJKBigramFilter(TokenStream in, boolean outputUnigrams) {
        super(in);
        this.outputUnigrams = outputUnigrams;
    }

    /*
     * much of this complexity revolves around handling the special case of a
     * "lone cjk character" where cjktokenizer would output a unigram. this is
     * also the only time we ever have to captureState.
     */
    @Override
    public boolean incrementToken() throws IOException {
        while (true) {
            if (hasBufferedBigram()) {

                // case 1: we have multiple remaining codepoints buffered,
                // so we can emit a bigram here.

                if (outputUnigrams) {

                    // when also outputting unigrams, we output the unigram first,
                    // then rewind back to revisit the bigram.
                    // so an input of ABC is A + (rewind)AB + B + (rewind)BC + C
                    // the logic in hasBufferedUnigram ensures we output the C, 
                    // even though it did actually have adjacent CJK characters.

                    if (ngramState) {
                        flushBigram();
                    } else {
                        flushUnigram();
                        index--;
                    }
                    ngramState = !ngramState;
                } else {
                    flushBigram();
                }
                return true;
            } else if (doNext()) {

                // case 2: look at the token type. should we form any n-grams?

                String type = typeAtt.type();
                if (type == CJKTokenizer.TOKEN_TYPES[CJKTokenizer.NGRAM]) {

                    // acceptable CJK type: we form n-grams from these.
                    // as long as the offsets are aligned, we just add these to our current buffer.
                    // otherwise, we clear the buffer and start over.

                    if (posticheOffsetAtt.startOffset() != posticheLastEndOffset) { // unaligned, clear queue
                        if (hasBufferedUnigram()) {

                            // we have a buffered unigram, and we peeked ahead to see if we could form
                            // a bigram, but we can't, because the offsets are unaligned. capture the state 
                            // of this peeked data to be revisited next time thru the loop, and dump our unigram.

                            loneState = captureState();
                            flushUnigram();
                            return true;
                        }
                        index = 0;
                        bufferLen = 0;
                    }
                    refill();
                } else {

                    // not a CJK type: we just return these as-is.

                    if (hasBufferedUnigram()) {

                        // we have a buffered unigram, and we peeked ahead to see if we could form
                        // a bigram, but we can't, because its not a CJK type. capture the state 
                        // of this peeked data to be revisited next time thru the loop, and dump our unigram.

                        loneState = captureState();
                        flushUnigram();
                        return true;
                    }
                    return true;
                }
            } else {

                // case 3: we have only zero or 1 codepoints buffered, 
                // so not enough to form a bigram. But, we also have no
                // more input. So if we have a buffered codepoint, emit
                // a unigram, otherwise, its end of stream.

                if (hasBufferedUnigram()) {
                    flushUnigram(); // flush our remaining unigram
                    return true;
                }
                return false;
            }
        }
    }

    private State loneState; // rarely used: only for "lone cjk characters", where we emit unigrams

    /**
     * looks at next input token, returning false is none is available
     */
    private boolean doNext() throws IOException {
        if (loneState != null) {
            restoreState(loneState);
            loneState = null;
            return true;
        } else {
            if (exhausted) {
                return false;
            } else if (input.incrementToken()) {
                return true;
            } else {
                exhausted = true;
                return false;
            }
        }
    }

    /**
     * refills buffers with new data from the current token.
     */
    private void refill() {
        // compact buffers to keep them smallish if they become large
        // just a safety check, but technically we only need the last codepoint
        if (bufferLen > 64) {
            int last = bufferLen - 1;
            buffer[0] = buffer[last];
            startOffset[0] = startOffset[last];
            endOffset[0] = endOffset[last];
            bufferLen = 1;
            index -= last;
        }

        char termBuffer[] = termAtt.buffer();
        int len = termAtt.length();
        int start = offsetAtt.startOffset();
        int end = offsetAtt.endOffset();

        int newSize = bufferLen + len;
        buffer = ArrayUtil.grow(buffer, newSize);
        startOffset = ArrayUtil.grow(startOffset, newSize);
        endOffset = ArrayUtil.grow(endOffset, newSize);
        lastEndOffset = end;
        posticheLastEndOffset = posticheOffsetAtt.endOffset();

        if (end - start != len) {
            // crazy offsets (modified by synonym or charfilter): just preserve
            for (int i = 0, cp = 0; i < len; i += Character.charCount(cp)) {
                cp = buffer[bufferLen] = Character.codePointAt(termBuffer, i,
                        len);
                startOffset[bufferLen] = start;
                endOffset[bufferLen] = end;
                bufferLen++;
            }
        } else {
            // normal offsets
            for (int i = 0, cp = 0, cpLen = 0; i < len; i += cpLen) {
                cp = buffer[bufferLen] = Character.codePointAt(termBuffer, i,
                        len);
                cpLen = Character.charCount(cp);
                startOffset[bufferLen] = start;
                start = endOffset[bufferLen] = start + cpLen;
                bufferLen++;
            }
        }
    }

    /**
     * Flushes a bigram token to output from our buffer This is the normal case,
     * e.g. ABC -> AB BC
     */
    private void flushBigram() {
        clearAttributes();
        char termBuffer[] = termAtt.resizeBuffer(4); // maximum bigram length in code units (2 supplementaries)
        int len1 = Character.toChars(buffer[index], termBuffer, 0);
        int len2 = len1
                + Character.toChars(buffer[index + 1], termBuffer, len1);
        termAtt.setLength(len2);
        offsetAtt.setOffset(startOffset[index], endOffset[index + 1]);
        typeAtt.setType(DOUBLE_TYPE);
        posLengthAtt.setPositionLength(2);
        index++;
    }

    /**
     * Flushes a unigram token to output from our buffer. This happens when we
     * encounter isolated CJK characters, either the whole CJK string is a
     * single character, or we encounter a CJK character surrounded by space,
     * punctuation, english, etc, but not beside any other CJK.
     */
    private void flushUnigram() {
        clearAttributes();
        char termBuffer[] = termAtt.resizeBuffer(2); // maximum unigram length (2 surrogates)
        int len = Character.toChars(buffer[index], termBuffer, 0);
        termAtt.setLength(len);
        offsetAtt.setOffset(startOffset[index], endOffset[index]);
        typeAtt.setType(SINGLE_TYPE);
        index++;
    }

    /**
     * True if we have multiple codepoints sitting in our buffer
     */
    private boolean hasBufferedBigram() {
        return bufferLen - index > 1;
    }

    /**
     * True if we have a single codepoint sitting in our buffer, where its
     * future (whether it is emitted as unigram or forms a bigram) depends upon
     * not-yet-seen inputs.
     */
    private boolean hasBufferedUnigram() {
        if (outputUnigrams) {
            // when outputting unigrams always
            return bufferLen - index == 1;
        } else {
            // otherwise its only when we have a lone CJK character
            return bufferLen == 1 && index == 0;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        bufferLen = 0;
        index = 0;
        lastEndOffset = 0;
        loneState = null;
        exhausted = false;
        ngramState = false;
    }
}
