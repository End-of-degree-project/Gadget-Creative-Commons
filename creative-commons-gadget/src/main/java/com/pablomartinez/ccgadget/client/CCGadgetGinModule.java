
package com.pablomartinez.ccgadget.client;

import cc.kune.gadget.client.AbstractKuneGadgetGinModule;

import com.google.inject.Singleton;

public class CCGadgetGinModule extends AbstractKuneGadgetGinModule {

  /*
   * (non-Javadoc)
   * 
   * @see com.google.gwt.inject.client.AbstractGinModule#configure()
   */
  @Override
  protected void configure() {
    bind(CCGadgetMainPanel.class).in(Singleton.class);
    bind(CCGadgetMessages.class).in(Singleton.class);
  };
}
