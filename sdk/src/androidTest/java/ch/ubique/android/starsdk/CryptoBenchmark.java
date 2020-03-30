/*
 * *
 *  * Created by Ubique Innovation AG on 3/30/20 2:55 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/30/20 2:54 PM
 *
 */

package ch.ubique.android.starsdk;

import androidx.benchmark.BenchmarkState;
import androidx.benchmark.junit4.BenchmarkRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.ubique.android.starsdk.crypto.STARModule;

@RunWith(AndroidJUnit4.class)
public class CryptoBenchmark {
    @Rule
    public BenchmarkRule benchmarkRule = new BenchmarkRule();

    @Test
    public void hmac_performance() {
        final BenchmarkState state = benchmarkRule.getState();
        STARModule module = STARModule.getInstance(InstrumentationRegistry.getInstrumentation().getContext());
        module.reset();
        if(module.init()) {
            byte[] star = module.newTOTP();
            int i =0;
            while(state.keepRunning()) {
                String key = "much longer key which is used for the hash functino but this should not have an influence" + Integer.toHexString(i);
                boolean assertion = module.validate(key.getBytes(), star);
                i += 1;
            }
        }
    }

}
