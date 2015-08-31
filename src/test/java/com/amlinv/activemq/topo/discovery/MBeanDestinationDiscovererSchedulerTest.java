/*
 * Copyright 2015 AML Innovation & Consulting LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.amlinv.activemq.topo.discovery;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by art on 8/29/15.
 */
public class MBeanDestinationDiscovererSchedulerTest {

    private MBeanDestinationDiscovererScheduler scheduler;

    private ScheduledExecutorService mockExecutor;
    private MBeanDestinationDiscoverer mockDiscoverer;
    private ScheduledFuture mockFuture;
    private Logger mockLogger;

    private Runnable runnable;
    private boolean stopSchedulerDuringStartup = false;
    private boolean doNotReturnFuture = false;

    @Before
    public void setupTest() throws Exception {
        this.scheduler = new MBeanDestinationDiscovererScheduler();

        this.mockExecutor = Mockito.mock(ScheduledExecutorService.class);
        this.mockDiscoverer = Mockito.mock(MBeanDestinationDiscoverer.class);
        this.mockFuture = Mockito.mock(ScheduledFuture.class);
        this.mockLogger = Mockito.mock(Logger.class);
    }

    @Test
    public void testGetSetExecutor() throws Exception {
        assertNull(this.scheduler.getExecutor());

        this.scheduler.setExecutor(this.mockExecutor);
        assertSame(this.mockExecutor, this.scheduler.getExecutor());
    }

    @Test
    public void testGetSetDiscoverer() throws Exception {
        assertNull(this.scheduler.getDiscoverer());

        this.scheduler.setDiscoverer(this.mockDiscoverer);
        assertSame(this.mockDiscoverer, this.scheduler.getDiscoverer());
    }

    @Test
    public void testGetSetInterPollDelay() throws Exception {
        assertEquals(MBeanDestinationDiscovererScheduler.DEFAULT_INTER_POLL_DELAY, this.scheduler.getInterPollDelay());

        this.scheduler.setInterPollDelay(1133);
        assertEquals(1133, this.scheduler.getInterPollDelay());
    }

    @Test
    public void testGetSetLog() throws Exception {
        assertNotNull(this.scheduler.getLog());
        assertNotSame(this.mockLogger, this.scheduler.getLog());

        this.scheduler.setLog(this.mockLogger);
        assertSame(this.mockLogger, this.scheduler.getLog());
    }

    @Test
    public void testStart() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.start();


        //
        // VALIDATE
        //
        assertNotNull(this.runnable);
    }

    @Test
    public void testStartedRunnable() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.start();

        // Make sure pollOnce has not yet been called.
        Mockito.verify(this.mockDiscoverer, Mockito.times(0)).pollOnce();

        this.runnable.run();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockDiscoverer).pollOnce();
    }

    @Test
    public void testStop() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.start();
        this.scheduler.stop();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockFuture).cancel(true);
    }

    @Test
    public void testStopBeforeStart() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.stop();
    }

    @Test
    public void testStopTwice() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.start();
        this.scheduler.stop();
        this.scheduler.stop();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockFuture, Mockito.times(1)).cancel(true);
    }

    @Test
    public void testStopWithoutFuture() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();
        this.doNotReturnFuture = true;


        //
        // EXECUTE
        //
        this.scheduler.start();
        this.scheduler.stop();


        //
        // VALIDATE
        //
        Mockito.verifyZeroInteractions(this.mockFuture);
    }

    @Test
    public void testStartWhenAlreadyStarted() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();


        //
        // EXECUTE
        //
        this.scheduler.start();

        try {
            this.scheduler.start();
            fail("missing expected exception");
        } catch ( IllegalStateException isExc ) {

            //
            // VALIDATE
            //

            assertEquals("already running", isExc.getMessage());
        }
    }

    @Test
    public void testExceptionOnRunnable() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();
        IOException ioExc = new IOException("x-io-exc-x");
        Mockito.doThrow(ioExc).when(this.mockDiscoverer).pollOnce();


        //
        // EXECUTE
        //
        this.scheduler.start();
        this.runnable.run();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockLogger).warn("error on poll for destination updates", ioExc);
    }

    @Test
    public void testStopWhileStarting() throws Exception {
        //
        // SETUP
        //
        this.setupScheduler();
        this.stopSchedulerDuringStartup = true;


        //
        // EXECUTE
        //
        this.scheduler.start();


        //
        // VALIDATE
        //
        Mockito.verify(this.mockFuture).cancel(true);
    }


                                                 ////             ////
                                                 ////  INTERNALS  ////
                                                 ////             ////

    /**
     * Setup standard test data and interactions for the scheduler.
     */
    protected void setupScheduler() {
        this.scheduler.setExecutor(this.mockExecutor);
        this.scheduler.setDiscoverer(this.mockDiscoverer);
        this.scheduler.setLog(this.mockLogger);

        //
        // Mock the scheduleWithFixedDelay() method to both (a) return a mock ScheduledFuture and (b) capture the
        //  Runnable argument.  Also optionally stop the scheduler, and optionally return null instead of the future.
        //
        Answer<ScheduledFuture> scheduleAnswer = new Answer<ScheduledFuture>() {
            @Override
            public ScheduledFuture answer(InvocationOnMock invocationOnMock) throws Throwable {
                runnable = (Runnable) invocationOnMock.getArguments()[0];
                if (stopSchedulerDuringStartup) {
                    scheduler.stop();
                }

                if (doNotReturnFuture)
                    return null;

                return mockFuture;
            }
        };
        Mockito.when(this.mockExecutor
                .scheduleWithFixedDelay(
                        Mockito.any(Runnable.class),
                        Mockito.eq(0L),
                        Mockito.eq(MBeanDestinationDiscovererScheduler.DEFAULT_INTER_POLL_DELAY),
                        Mockito.any(TimeUnit.class)))
                .thenAnswer(scheduleAnswer);
    }
}
