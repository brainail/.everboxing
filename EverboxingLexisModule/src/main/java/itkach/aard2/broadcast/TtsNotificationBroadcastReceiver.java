package itkach.aard2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

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
@SuppressWarnings ("Unused")
public class TtsNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {
        final TextToSpeech tts = new TextToSpeech (context, null);
        tts.stop ();
    }
}

// ArticleFragment
// ...
//    private final UtteranceProgressListener mTtsProgressListener = new UtteranceProgressListener () {
//        @Override
//        public void onStart (String utteranceId) {
//            showPlayer ();
//        }
//
//        @Override
//        public void onDone (String utteranceId) {
//            hidePlayer ();
//        }
//
//        @Override
//        public void onError (String utteranceId) {
//            hidePlayer ();
//        }
//
//        private void hidePlayer () {
//            NotificationManager notificationManager = (NotificationManager) getActivity ().getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel (-1024);
//        }
//
//        private void showPlayer () {
//            Intent notificationIntent = new Intent(getActivity (), TtsNotificationBroadcastReceiver.class);
//            PendingIntent np = PendingIntent.getBroadcast (getActivity (), 0, notificationIntent, 0);
//            final android.support.v4.app.NotificationCompat.Builder mNotificationBuilder =
//                    new NotificationCompat.Builder (getActivity ())
//                            .setSmallIcon (R.mipmap.ic_launcher)
//                            .addAction (android.R.drawable.ic_media_play, "Stop", np)
//                            .setContentTitle(mArticleTitle);
//
//            NotificationManager notificationManager = (NotificationManager) getActivity ().getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.notify(-1024, mNotificationBuilder.build());
//        }
//    }
