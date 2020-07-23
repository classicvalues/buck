/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.buck.event.listener;

import com.facebook.buck.core.build.event.BuildRuleExecutionEvent;
import com.facebook.buck.event.BuckEventListener;
import com.facebook.buck.remoteexecution.event.RemoteBuildRuleExecutionEvent;
import com.facebook.buck.support.SlowTarget;
import com.facebook.buck.support.TopSlowTargetsBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.TimeUnit;

/**
 * {@link BuckEventListener} that is intended to build Critical path of the build (The longest by
 * time path )
 */
public class TopSlowTargetsEventListener implements BuckEventListener {

  private final TopSlowTargetsBuilder slowTargetsBuilder = new TopSlowTargetsBuilder();

  /** Subscribes to {@link BuildRuleExecutionEvent.Finished} events */
  @Subscribe
  public void subscribe(BuildRuleExecutionEvent.Finished event) {
    long elapsedTimeMillis = TimeUnit.NANOSECONDS.toMillis(event.getElapsedTimeNano());
    slowTargetsBuilder.onTargetCompleted(event.getTarget(), elapsedTimeMillis);
  }

  /** Subscribes to {@link RemoteBuildRuleExecutionEvent} events */
  @Subscribe
  public void subscribe(RemoteBuildRuleExecutionEvent event) {
    slowTargetsBuilder.onTargetCompleted(
        event.getBuildRule().getBuildTarget(), event.getExecutionDurationMs());
  }

  public ImmutableList<SlowTarget> getTopSlowTargets() {
    return slowTargetsBuilder.getSlowRules();
  }
}