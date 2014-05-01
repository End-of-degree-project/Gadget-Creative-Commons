
package com.pablomartinez.ccgadget.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.thezukunft.wave.connector.StateUpdateEvent;
import com.thezukunft.wave.connector.StateUpdateEventHandler;
import com.thezukunft.wave.connector.Wave;


public class CCGadgetMainPanel extends Composite {

  public enum QuestionState {UNANSWERED, ANSWERED_NO, ANSWERED_YES};
  
  /** The messages. */
  private final CCGadgetMessages messages;
  
  private static final String SA_K = "SA_KEY";
  private static final String NC_K = "NC_KEY";
  private static final String ND_K = "ND_KEY";

  private QuestionState SA = QuestionState.UNANSWERED;
  private QuestionState ND = QuestionState.UNANSWERED;
  private QuestionState NC = QuestionState.UNANSWERED;
  
  private static List<String> latestAnswers = new ArrayList<String>();
  
  public Button backButton;
  
  private HandlerRegistration handler = null;
  
  @Inject
  public CCGadgetMainPanel(final EventBus eventBus, final Wave wave,
      final CCGadgetMessages gadgetMessages) {
    this.messages = gadgetMessages;
    
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      // We run this deferred, at the end of the gadget load
      @Override
      public void execute() {

        initState(wave);
        
        final Label question = new Label(getQuestion(SA, ND, NC));
        final VerticalPanel options = new VerticalPanel();
        final RadioButton buttonYes = new RadioButton("answer", messages.yes());
        final RadioButton buttonNo = new RadioButton("answer", messages.no());
        buttonYes.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            registerAnswerWithQuestion(question.getText(), QuestionState.ANSWERED_YES, wave);
            buttonYes.setValue(false);
            buttonNo.setValue(false);
          }
        });
        buttonNo.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            registerAnswerWithQuestion(question.getText(), QuestionState.ANSWERED_NO, wave);
            buttonYes.setValue(false);
            buttonNo.setValue(false);
          }
        });
        options.add(buttonYes);
        options.add(buttonNo);
        backButton = new Button(messages.back());
        backButton.setEnabled(false);
        backButton.addClickHandler(new ClickHandler() {
          @Override
          public void onClick(ClickEvent event) {
            String key = latestAnswers.get(latestAnswers.size()-1);
            latestAnswers.remove(latestAnswers.size()-1);
            registerAnswer(key, QuestionState.UNANSWERED, wave);
          }
        });
        final Image license = new Image();
        license.getElement().getStyle().setCursor(Cursor.POINTER);
        final VerticalPanel vp = new VerticalPanel();
        vp.add(question);
        vp.add(options);
        vp.add(license);
        vp.add(backButton);
        initWidget(vp);
        
        eventBus.addHandler(StateUpdateEvent.TYPE, new StateUpdateEventHandler() {
          @Override
          public void onUpdate(StateUpdateEvent event) {
            QuestionState sa;
            try { sa = QuestionState.valueOf(event.getState().get(SA_K)); }
            catch (NullPointerException e){ sa = QuestionState.UNANSWERED; }
            
            QuestionState nd;
            try{ nd = QuestionState.valueOf(event.getState().get(ND_K)); }
            catch (NullPointerException e){ nd = QuestionState.UNANSWERED; }
            
            QuestionState nc;
            try { nc = QuestionState.valueOf(event.getState().get(NC_K)); }
            catch(NullPointerException e){ nc = QuestionState.UNANSWERED; }
            
            String licenseImageUrl = getLicenseImageUrl(sa, nd, nc);
            final String licenseUrl = getLicenseUrl(licenseImageUrl);
            String nextQuestion = getQuestion(sa, nd, nc);
            
            if(nextQuestion == null){
              buttonYes.setVisible(false);
              buttonNo.setVisible(false);
              backButton.setVisible(false);
              question.setVisible(false);
            }
            else{
              buttonYes.setVisible(true);
              buttonNo.setVisible(true);
              backButton.setVisible(true);
              question.setVisible(true);
            }
            
            license.setUrl(licenseImageUrl);
            
            if(handler != null){
              handler.removeHandler();
            }
            
            handler = license.addClickHandler(new ClickHandler() {
              @Override
              public void onClick(ClickEvent event) {
                Window.open(licenseUrl,"_blank","");
              }
            });
            
            question.setText(nextQuestion);
            
            adjustHeight();
          }
        });
        adjustHeight();
      }
    });
  }
  
  private void registerAnswer(String key, QuestionState answer, Wave wave){
    if(key != null) {
      if(key.equals(SA_K)){
        SA = answer;
      }
      else if(key.equals(ND_K)){
        ND = answer;
      }
      else if(key.equals(NC_K)){
        NC = answer;
      }
      
      wave.getState().submitValue(key, answer.name());
      if(!answer.equals(QuestionState.UNANSWERED)){
        latestAnswers.add(key);
      }
      if(latestAnswers.size() == 0){
        backButton.setEnabled(false);
      }
      else{
        backButton.setEnabled(true);
      }
    }
  }
  
  private void initState(Wave wave){
    String value = wave.getState().get(SA_K);
    try{
      if(value.equals(QuestionState.ANSWERED_NO)){
        SA = QuestionState.ANSWERED_NO;
      }
      else if(value.equals(QuestionState.ANSWERED_YES)){
        SA = QuestionState.ANSWERED_YES;
      }
    }catch (Exception e){}
    
    value = wave.getState().get(ND_K);
    try{
      if(value.equals(QuestionState.ANSWERED_NO)){
        ND = QuestionState.ANSWERED_NO;
      }
      else if(value.equals(QuestionState.ANSWERED_YES)){
        ND = QuestionState.ANSWERED_YES;
      }
    }catch (Exception e){}
    
    value = wave.getState().get(NC_K);
    try{
      if(value.equals(QuestionState.ANSWERED_NO)){
        NC = QuestionState.ANSWERED_NO;
      }
      else if(value.equals(QuestionState.ANSWERED_YES)){
        NC = QuestionState.ANSWERED_YES;
      }
    }catch (Exception e){}
  }
  
  private void registerAnswerWithQuestion(String question, QuestionState answer, Wave wave){
    if(question == null || question.equals("")){
      return;
    }
    if(question.equals(messages.question_SA())){
      registerAnswer(SA_K, answer, wave);
    }
    else if(question.equals(messages.question_ND())){
      registerAnswer(ND_K, answer, wave);
    }
    else if(question.equals(messages.question_NC())){
      registerAnswer(NC_K, answer, wave);
    }
  }
  
  private String getLicenseUrl(String licenseImageUrl){
    if(licenseImageUrl.equals(messages.CC_BY())){
      return messages.CC_BY_L();
    }
    else if(licenseImageUrl.equals(messages.CC_BY_NC())){
      return messages.CC_BY_NC_L();
    }
    else if(licenseImageUrl.equals(messages.CC_BY_NC_ND())){
      return messages.CC_BY_NC_ND_L();
    }
    else if(licenseImageUrl.equals(messages.CC_BY_NC_SA())){
      return messages.CC_BY_NC_SA_L();
    }
    else if(licenseImageUrl.equals(messages.CC_BY_ND())){
      return messages.CC_BY_ND_L();
    }
    else if(licenseImageUrl.equals(messages.CC_BY_SA())){
      return messages.CC_BY_SA_L();
    }
    else{
      return null;
    }
  }
  
  private String getLicenseImageUrl(QuestionState sa, QuestionState nd, QuestionState nc){
    if(nd.equals(QuestionState.ANSWERED_NO) && nc.equals(QuestionState.ANSWERED_NO)){
      return messages.CC_BY_NC_ND();
    }
    else if(nc.equals(QuestionState.ANSWERED_NO) && sa.equals(QuestionState.ANSWERED_NO)){
      return messages.CC_BY_NC_SA();
    }
    else if(nc.equals(QuestionState.ANSWERED_NO)){
      return messages.CC_BY_NC();
    }
    else if(nd.equals(QuestionState.ANSWERED_NO)){
      return messages.CC_BY_ND();
    }
    else if(sa.equals(QuestionState.ANSWERED_NO)){
      return messages.CC_BY_SA();
    }
    else{
      return messages.CC_BY();
    }
  }

  private String getQuestion(QuestionState sa, QuestionState nd, QuestionState nc) {
    if(sa.equals(QuestionState.UNANSWERED)){
      return messages.question_SA();
    }
    else if(nd.equals(QuestionState.UNANSWERED)){
      return messages.question_ND();
    }
    else if(nc.equals(QuestionState.UNANSWERED)){
      return messages.question_NC();
    }
    else return null;
  }
  
  private static native void adjustHeight() /*-{
    $wnd.gadgets.window.adjustHeight();
  }-*/;
}
