
package com.pablomartinez.ccgadget.client;

import cc.kune.gadget.client.KuneGadgetGinInjector;

import com.google.gwt.inject.client.GinModules;

@GinModules(CCGadgetDeployModule.class)
public interface CCGadgetDeployGinjector extends KuneGadgetGinInjector {

  /**
   * Gets the main panel.
   * 
   * @return the main panel
   */
  CCGadgetMainPanel getMainPanel();
}
