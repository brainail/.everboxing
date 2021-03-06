package org.brainail.EverboxingHardyDialogs;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This file is part of Everboxing modules. <br/><br/>
 * <p/>
 * The MIT License (MIT) <br/><br/>
 * <p/>
 * Copyright (c) 2014 Malyshev Yegor aka brainail at wsemirz@gmail.com <br/><br/>
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy <br/>
 * of this software and associated documentation files (the "Software"), to deal <br/>
 * in the Software without restriction, including without limitation the rights <br/>
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell <br/>
 * copies of the Software, and to permit persons to whom the Software is <br/>
 * furnished to do so, subject to the following conditions: <br/><br/>
 * <p/>
 * The above copyright notice and this permission notice shall be included in <br/>
 * all copies or substantial portions of the Software. <br/><br/>
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR <br/>
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, <br/>
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE <br/>
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER <br/>
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, <br/>
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN <br/>
 * THE SOFTWARE.
 * <br/><br/>
 *
 * Dialog specification for list with items <br/>
 *
 * @author emalyshev
 */
public class ListDialogSpecification extends BaseDialogSpecification {

    private ArrayList<String> mItems;
    private ArrayList<String> mTags;

    protected ListDialogSpecification (final Builder<?> builder) {
        super (builder);
        mItems = builder.items;
        mTags = builder.tags;
    }

    public static class Builder <T extends Builder<T>> extends BaseDialogSpecification.Builder<T> {

        private ArrayList<String> items;
        private ArrayList<String> tags;

        protected Builder () {
            super ();
        }

        protected Builder (final ListDialogSpecification specification) {
            super (specification);
            items = specification.mItems;
            tags = specification.mTags;
        }

        @Override
        protected void fillDefaultValues () {
            super.fillDefaultValues ();
            // No items by default
            items (new ArrayList<String> ());
            // No tags by default
            tags (new ArrayList<String> ());
        }

        public T items (final int [] itemsResources) {
            final String [] items = new String [itemsResources.length];

            int index = 0;
            for (final int itemResource : itemsResources) {
                items [index ++] = HardyDialogsContext.get ().getString (itemResource);
            }

            return items (items);
        }

        public T items (final String [] items) {
            return items (new ArrayList<String> (Arrays.asList (items)));
        }

        public T tags (final String [] tags) {
            return tags (new ArrayList<String> (Arrays.asList (tags)));
        }

        public T items (final ArrayList<String> items) {
            this.items = items;
            return self ();
        }

        public T tags (final ArrayList<String> tags) {
            this.tags = tags;
            return self ();
        }

        @Override
        ListDialogSpecification build () {
            return new ListDialogSpecification (this);
        }

    }

    @Override
    public Builder<?> basedOn () {
        return new Builder (this);
    }

    @SuppressWarnings ("rawtypes")
    public static Builder<?> create () {
        return new Builder ();
    }

    @Override
    protected void fillBundle (final Bundle args) {
        args.putBoolean (HardyDialogFragment.Args.HAS_LIST, true);
        args.putStringArrayList (HardyDialogFragment.Args.LIST_ITEMS, mItems);
        args.putStringArrayList (HardyDialogFragment.Args.LIST_ITEMS_TAGS, mTags);
        super.fillBundle (args);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }

        if (! (o instanceof ListDialogSpecification)) {
            return false;
        }

        if (! super.equals (o)) {
            return false;
        }

        ListDialogSpecification that = (ListDialogSpecification) o;

        return mItems.equals (that.mItems);

    }

    @Override
    public int hashCode () {
        int result = super.hashCode ();
        result = 31 * result + mItems.hashCode ();
        return result;
    }

}
