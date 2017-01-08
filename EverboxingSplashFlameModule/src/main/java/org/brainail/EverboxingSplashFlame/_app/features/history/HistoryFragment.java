package org.brainail.EverboxingSplashFlame._app.features.history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.brainail.EverboxingSplashFlame.Constants;
import org.brainail.EverboxingSplashFlame.R;
import org.brainail.EverboxingSplashFlame._app.features.history.FilesHistoryRecyclerViewAdapter.FilesListDiffCallback;
import org.brainail.EverboxingSplashFlame.files.FileCreator;
import org.brainail.EverboxingSplashFlame.ui.fragments.base.RxBaseFragment;
import org.brainail.EverboxingSplashFlame.utils.tool.rx.RxToolFiles;
import org.brainail.EverboxingTools.ui.views.AutoFitGridRecyclerView;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
 */
public class HistoryFragment
        extends RxBaseFragment
        implements FilesHistoryRecyclerViewAdapter.OnItemClickListener<File> {

    @BindView (R.id.files_history)
    protected AutoFitGridRecyclerView mFilesHistory;

    protected FilesHistoryRecyclerViewAdapter mFilesHistoryAdapter;

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);

        mFilesHistoryAdapter = new FilesHistoryRecyclerViewAdapter (getContext (), this);
        mFilesHistory.setAdapter (mFilesHistoryAdapter);
    }

    @Override
    public void onResume () {
        super.onResume ();

        bindSubscription (
                loadFilesDiff()
                        .subscribeOn (Schedulers.io ())
                        .observeOn (AndroidSchedulers.mainThread ())
                        .subscribe (this :: onFilesDiff)
        );
    }

    protected Observable<Pair<List<File>, DiffUtil.DiffResult>> loadFilesDiff() {
        return RxToolFiles.files (new File (Constants.APP_MEDIA_DIR_PATH), FileCreator.FLAME_PREVIEWS_FILES_FILTER)
                .toList ()
                .flatMap (files -> Observable.just (
                        Pair.create (files, DiffUtil.calculateDiff (createFilesDiffCallback (files)))));
    }

    private DiffUtil.Callback createFilesDiffCallback(List<File> files) {
        return new FilesListDiffCallback (mFilesHistoryAdapter.getData (), files);
    }

    protected void onFilesDiff (final Pair<List<File>, DiffUtil.DiffResult> result) {
        mFilesHistoryAdapter.swapDataQuietly (result.first);
        result.second.dispatchUpdatesTo(mFilesHistoryAdapter);
    }

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate (R.layout.fragment_history, container, false);
        return bind (view);
    }

    @Override
    public void onItemClick (final View itemView, final File data) {
        mNavigator.flameFilePreview (data.getAbsolutePath ()).start ();
    }

}
