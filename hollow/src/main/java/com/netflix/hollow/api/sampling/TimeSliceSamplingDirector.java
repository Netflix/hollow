/*
 *  Copyright 2016-2019 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.api.sampling;

import static com.netflix.hollow.core.util.Threads.daemonThread;

import java.util.ArrayList;
import java.util.List;

public class TimeSliceSamplingDirector extends HollowSamplingDirector {

    private final List<SamplingStatusListener> listeners = new ArrayList<SamplingStatusListener>();

    private int msOff;
    private int msOn;

    private boolean isInPlay = false;

    private boolean record = false;

    public TimeSliceSamplingDirector() {
        this(1000, 1);
    }

    public TimeSliceSamplingDirector(int msOff, int msOn) {
        this.msOff = msOff;
        this.msOn = msOn;
    }

    @Override
    public boolean shouldRecord() {
        return record && !isUpdateThread();
    }

    public void startSampling() {
        if(!isInPlay) {
            isInPlay = true;
            daemonThread(new SampleToggler(), getClass(), "toggler")
                    .start();
        }
    }

    public void setTiming(int msOff, int msOn) {
        this.msOff = msOff;
        this.msOn = msOn;
    }

    public void stopSampling() {
        isInPlay = false;
    }

    private class SampleToggler implements Runnable {
        @Override
        public void run() {
            while(isInPlay) {
                record = false;
                notifyListeners();
                sleep(msOff);
                record = isInPlay;
                notifyListeners();
                sleep(msOn);
            }

            record = false;
            notifyListeners();
        }

        private void sleep(int ms) {
            try {
                Thread.sleep(ms);
            } catch(InterruptedException ignore) { }
        }
    }

    private void notifyListeners() {
        for(int i=0;i<listeners.size();i++) {
            listeners.get(i).samplingStatusChanged(record);
        }
    }

    public void addSamplingStatusListener(SamplingStatusListener listener) {
        listener.samplingStatusChanged(record);
        listeners.add(listener);
    }

}
