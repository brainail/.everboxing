package org.brainail.EverboxingSplashFlame.ui.fragments.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import org.brainail.EverboxingSplashFlame.di.HasComponent;
import org.brainail.EverboxingSplashFlame.di.component.ActivityComponent;

/**
 * Contains logic of {@code BaseFragment} that can be shared between other fragments when inheritance is not possible.
 * Inspired by {@link AppCompatDelegate}.
 */
public class BaseFragmentDelegate {

    private BaseFragmentCallbacks mCallbacks;
    private HasComponent<ActivityComponent> mHasActivityComponent;

    public BaseFragmentDelegate (@NonNull BaseFragmentCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @SuppressWarnings ("unchecked")
    public void onAttach (Context context) {
        try {
            mHasActivityComponent = (HasComponent<ActivityComponent>) context;
        } catch (final Exception exception) {
            throw new ClassCastException (context.toString () + " must implement HasComponent<ActivityComponent> interface");
        }
    }

    public void onActivityCreated (@Nullable Bundle savedInstanceState) {
        mCallbacks.setupComponent (mHasActivityComponent.getComponent ());
        mCallbacks.injectMembers (mHasActivityComponent.getComponent ());
    }

    public interface BaseFragmentCallbacks {
        /**
         * Override this method to create custom component instance.
         * Super implementation <b>must</b> be called first.
         * Note that injection is not allowed here.
         * See {@link BaseFragment#injectMembers(ActivityComponent)}.
         *
         * @param activityComponent default activity component, for convenience
         */
        void setupComponent (ActivityComponent activityComponent);

        /**
         * Override this method to apply field injection.
         * Usually, you shouldn't call super implementation, because
         * fields in base classes are injected automatically.
         *
         * @param activityComponent default activity component, for convenience
         */
        void injectMembers (ActivityComponent activityComponent);
    }
}
