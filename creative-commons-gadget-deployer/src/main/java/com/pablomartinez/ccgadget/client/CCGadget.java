
package com.pablomartinez.ccgadget.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.gadgets.client.DynamicHeightFeature;
import com.google.gwt.gadgets.client.Gadget;
import com.google.gwt.gadgets.client.NeedsDynamicHeight;
import com.google.gwt.gadgets.client.NeedsSetPrefs;
import com.google.gwt.gadgets.client.SetPrefsFeature;
import com.google.gwt.gadgets.client.UserPreferences;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import com.thezukunft.wave.connector.GadgetUpdateEvent;
import com.thezukunft.wave.connector.GadgetUpdateEventHandler;
import com.thezukunft.wave.connectorimpl.NeedsWave;
import com.thezukunft.wave.connectorimpl.WaveGINWrapper;
import com.thezukunft.wave.connectorimpl.WaveGadget;

@Gadget.ModulePrefs( //
title = "Kune gadget sample", //
author = "The kune development team", //
author_link = "http://kune.ourproject.org", //
height = 640// , //
// width=550 //
// Commented only to use 100% width (see WAVE-309)//
)
@Gadget.InjectContent(files = { "ModuleContent.txt" })
public class CCGadget extends WaveGadget<UserPreferences> implements NeedsWave, EntryPoint,
    NeedsDynamicHeight, NeedsSetPrefs {

  // private MassmobPreferences userPrefsFeature;

  /** The dyn height feature. */
  private DynamicHeightFeature dynHeightFeature;

  /** The gin. */
  protected CCGadgetDeployGinjector gin;

  /** The set prefs feature. */
  private SetPrefsFeature setPrefsFeature;

  /**
   * Check ready.
   */
  private void checkReady() {
    if (dynHeightFeature != null && setPrefsFeature != null) {
      initGadget();
    }
  }

  /**
   * Creates the ui objects.
   */
  private void createUIObjects() {
    gin = GWT.create(CCGadgetDeployGinjector.class);
    final WaveGINWrapper w = (WaveGINWrapper) gin.getWave();
    w.setWave(getWave());

    final CCGadgetMainPanel mainPanel = gin.getMainPanel();

    gin.getEventBus().addHandler(GadgetUpdateEvent.TYPE, new GadgetUpdateEventHandler() {
      @Override
      public void onUpdate(final GadgetUpdateEvent event) {
        setHeight(mainPanel);
      }
    });
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        dynHeightFeature.getContentDiv().add(mainPanel);
        setHeight(mainPanel);
      }
    });
  }

  @Override
  protected void init(final UserPreferences preferences) {
  }

  /**
   * Inits the gadget.
   */
  private void initGadget() {
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      private Timer timer;

      @Override
      public void execute() {
        if (getWave().isInWaveContainer()) {
          // We only create and initialize the gadget panel when we are running
          // in a the wave container. If not we wait.
          timer = new Timer() {
            @Override
            public void run() {
              if (getWave().getState() == null) {
                timer.schedule(100);
              } else {
                // Ok state ready, create widget
                timer.cancel();
                createUIObjects();
              }
            }
          };
          timer.run();
        } else {
          GWT.log("The gadget is not running in a wave container");
        }
      }
    });
  }

  @Override
  public void initializeFeature(final DynamicHeightFeature dynHeightFeature) {
    this.dynHeightFeature = dynHeightFeature;
    checkReady();
  }

  @Override
  public void initializeFeature(final SetPrefsFeature feature) {
    this.setPrefsFeature = feature;
    checkReady();
  }

  /**
   * Sets the height.
   * 
   * @param gadget
   *          the new height
   */
  private void setHeight(final CCGadgetMainPanel gadget) {
    dynHeightFeature.adjustHeight();
  }
}
