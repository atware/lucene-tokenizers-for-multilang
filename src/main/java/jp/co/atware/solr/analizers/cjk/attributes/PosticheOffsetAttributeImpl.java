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

package jp.co.atware.solr.analizers.cjk.attributes;

import org.apache.lucene.util.AttributeImpl;

public class PosticheOffsetAttributeImpl extends AttributeImpl implements
        PosticheOffsetAttribute, Cloneable {
    private int startOffset;
    private int endOffset;

    public int startOffset() {
        return startOffset;
    }

    public void setOffset(int startOffset, int endOffset) {

        if (startOffset < 0 || endOffset < startOffset) {
            throw new IllegalArgumentException(
                    "startOffset must be non-negative, and endOffset must be >= startOffset, "
                            + "startOffset=" + startOffset + ",endOffset="
                            + endOffset);
        }

        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public int endOffset() {
        return endOffset;
    }

    @Override
    public void clear() {
        startOffset = 0;
        endOffset = 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (other instanceof PosticheOffsetAttributeImpl) {
            PosticheOffsetAttributeImpl o = (PosticheOffsetAttributeImpl) other;
            return o.startOffset == startOffset && o.endOffset == endOffset;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int code = startOffset;
        code = code * 31 + endOffset;
        return code;
    }

    @Override
    public void copyTo(AttributeImpl target) {
        PosticheOffsetAttributeImpl t = (PosticheOffsetAttributeImpl) target;
        t.setOffset(startOffset, endOffset);
    }
}
