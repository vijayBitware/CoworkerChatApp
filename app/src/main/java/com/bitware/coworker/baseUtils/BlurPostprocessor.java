//package com.zybertron.zoechat.baseUtils;
//
//import android.graphics.Bitmap;
//
///**
// * Created by user on 26-05-2017.
// */
//
//public class BlurPostprocessor extends BasePostprocessorWithDurationCallback{
//    protected static final int BLUR_RADIUS = 25;
//    protected static final int BLUR_ITERATIONS = 3;
//
//    public BlurPostprocessor(DurationCallback durationCallback) {
//        super(durationCallback);
//    }
//
//    public void process(Bitmap bitmap) {
//        final long startNs = System.nanoTime();
//
//        NativeBlurFilter.iterativeBoxBlur(bitmap, BLUR_RADIUS, BLUR_ITERATIONS);
//
//        showDuration(System.nanoTime() - startNs);
//    }
//}
