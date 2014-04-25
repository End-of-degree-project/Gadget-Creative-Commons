
package com.pablomartinez.ccgadgettester.client;

import cc.kune.gadget.client.KuneGadgetGinInjector;

import com.google.gwt.inject.client.GinModules;
import com.pablomartinez.ccgadget.client.CCGadgetMainPanel;
import com.pablomartinez.ccgadget.client.CCGadgetMessages;


@GinModules(CCGadgetTesterGinModule.class)
public interface CCGadgetGinInjector extends KuneGadgetGinInjector {

  /**
   * Gets the main panel.
   * 
   * @return the main panel
   */
  CCGadgetMainPanel getMainPanel();

  /**
   * Gets the gadget messages.
   * 
   * @return the gadget messages
   */
  CCGadgetMessages getGadgetMessages();
}
