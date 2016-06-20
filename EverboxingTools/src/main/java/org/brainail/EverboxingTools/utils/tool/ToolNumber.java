package org.brainail.EverboxingTools.utils.tool;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 */
public final class ToolNumber {
    public enum ValidationStatus {OK, TOO_SMALL, TOO_BIG, INCORRECT_FORMAT}

    public static ValidationStatus validateIntNumber(final String number, final int from, final int to) {
        if (number.startsWith ("0")) {
            return ValidationStatus.INCORRECT_FORMAT;
        }

        int numberInt = -1;
        try {
            numberInt = Integer.parseInt (number);
        } catch (final Exception exception) {
            return ValidationStatus.INCORRECT_FORMAT;
        }

        if (numberInt < from) {
            return ValidationStatus.TOO_SMALL;
        }

        if (numberInt > to) {
            return ValidationStatus.TOO_BIG;
        }

        return ValidationStatus.OK;
    }
}
