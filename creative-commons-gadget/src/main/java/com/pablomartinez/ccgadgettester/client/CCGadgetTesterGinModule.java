
package com.pablomartinez.ccgadgettester.client;


import com.google.inject.Singleton;
import com.pablomartinez.ccgadget.client.CCGadgetGinModule;
import com.pablomartinez.ccgadget.client.CCGadgetMainPanel;
import com.thezukunft.wave.connector.Wave;
import com.thezukunft.wave.connectormock.WaveMock;

public class CCGadgetTesterGinModule extends CCGadgetGinModule {

  @Override
  protected void configure() {

    bind(CCGadgetMainPanel.class);
    
    bind(Wave.class).to(WaveMock.class).in(Singleton.class);
  };
}
