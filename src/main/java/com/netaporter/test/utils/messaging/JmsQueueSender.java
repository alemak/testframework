package com.netaporter.test.utils.messaging;


//import com.netaporter.utils.http.activityservice.pojos.ActivityServiceMsgObj;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.*;


/**
 * Created by IntelliJ IDEA.
 * User: cucumber
 * Date: 13/07/12
 * Time: 12:16
 */

public class JmsQueueSender {

  protected JmsTemplate jmsTemplate;
  protected Queue queue;

  public void setConnectionFactory(ConnectionFactory cf) {
    this.jmsTemplate = new JmsTemplate(cf);
  }

  public void setQueue(Queue queue) {
    this.queue = queue;
  }

  public void setMessageConverter(MessageConverter mCon){
      this.jmsTemplate.setMessageConverter(mCon);
  }

  public void simpleSend() {
    this.jmsTemplate.send(this.queue, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage("hello queue world");
      }
    });
  }
}
