package org.brainail.EverboxingTools.utils.tool;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p>
 * The MIT License (MIT) <br/><br/>
 * <p>
 * Copyright (c) 2016 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
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
public final class ToolGestures {

    public static void fixTouchesOnClickableSpanWithNoEffectOnPressedState(final TextView view) {
        if (null == view) {
            // no money no honey
            return;
        }

        view.setOnTouchListener(new View.OnTouchListener () {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final TextView widget = (TextView) v;

                Spannable text;
                if (widget.getText() instanceof Spanned) {
                    text = Spannable.Factory.getInstance ().newSpannable (widget.getText());
                } else {
                    return false;
                }

                int action = event.getAction();
                if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_DOWN == action) {
                    final int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
                    final int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();

                    final Layout widgetLayout = widget.getLayout();
                    int line = widgetLayout.getLineForVertical(y);
                    int off = widgetLayout.getOffsetForHorizontal(line, x);

                    final ClickableSpan [] clickableSpans = text.getSpans(off, off, ClickableSpan.class);
                    if (0 != clickableSpans.length) {
                        if (MotionEvent.ACTION_UP == action) {
                            clickableSpans [0].onClick(widget);
                        }

                        return true;
                    }
                }

                return false;
            }
        });
    }

}
