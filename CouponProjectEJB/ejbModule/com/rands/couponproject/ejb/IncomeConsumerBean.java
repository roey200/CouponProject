package com.rands.couponproject.ejb;

import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.rands.couponproject.jpa.Income;

/**
 * Message-Driven Bean implementation class for: IncomeConsumerBean
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
        propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(
        propertyName = "destination", propertyValue = "java:/jms/queue/MyQueue")
        })
public class IncomeConsumerBean implements MessageListener {
	
	static Logger logger = Logger.getLogger(IncomeConsumerBean.class);
	
	@EJB
	IncomeServiceBean isb;

    /**
     * Default constructor. 
     */
    public IncomeConsumerBean() {
        // TODO Auto-generated constructor stub
    }
	
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                logger.info("onMessage received a TextMessage at " + new Date());
                TextMessage msg = (TextMessage) message;
                logger.warn("onMessage ignoring TextMessage : " + msg.getText());
            } else if (message instanceof ObjectMessage) {
                logger.info("onMessage received an ObjectMessage at " + new Date());
                
                ObjectMessage msg = (ObjectMessage) message;
                
                Object o = msg.getObject();
                logger.info("o=" + o);
                
                Income income = (Income) msg.getObject(); // assume it is an Income record
                logger.info("Message is : " + income);
                
                isb.StoreIncome(income);
            } else {
            	logger.error("onMessage received an invalid message type");
            }
 
        } catch (JMSException e) {
        	logger.error("onMessage failed : " + e.toString());
        }
    }


}
