package com.rands.couponproject.ejb.client;

import java.util.Date;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.rands.couponproject.jpa.Income;
import com.rands.couponproject.jpa.IncomeType;

public class IncomeMDBClient {

	static Logger logger = Logger.getLogger(IncomeMDBClient.class);

	public void produceIncome() {
		logger.info("produceIncome");
		try {

			Income income = new Income();
			income.setId(0);
			income.setName("Kumar");
			income.setDescription(IncomeType.COMPANY_CREATE_COUPON);
			income.setAmount(12345);
			income.setDate(new Date());
			
			produceIncome(income);
		} catch (Exception e) {
		}
	}

	public void produceIncome(String name, double amount, IncomeType description) {
		logger.info("produceIncome");
		try {
			Income income = new Income();
			income.setId(0);
			income.setName(name);
			income.setDescription(description);
			income.setAmount(amount);
			income.setDate(new Date());
			
			produceIncome(income);
		} catch (Exception e) {

		}
	}

	public void produceIncome(Income income) {
		logger.info("produceIncome");

		final String QUEUE_LOOKUP = "java:/jms/queue/MyQueue";
		final String CONNECTION_FACTORY = "ConnectionFactory";

		try {
			javax.naming.Context context = new InitialContext();
			QueueConnectionFactory factory =
					(QueueConnectionFactory) context.lookup(CONNECTION_FACTORY);
			QueueConnection connection = factory.createQueueConnection();
			QueueSession session =
					connection.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);

			Queue queue = (Queue) context.lookup(QUEUE_LOOKUP);
			QueueSender sender = session.createSender(queue);

			//1. Sending TextMessage to the Queue 
			// TextMessage message = session.createTextMessage();
			// message.setText("Hello EJB3 MDB Queue!!!");
			// sender.send(message);
			//logger.info("1. Sent TextMessage to the Queue");

			//2. Sending ObjectMessage to the Queue
			ObjectMessage objMsg = session.createObjectMessage();

			objMsg.setObject(income);
			sender.send(objMsg);
			logger.info("Sent Income to the Queue");

			session.close();
			connection.close();
			
		} catch (Exception e) {
			logger.error("produceIncome failed : " + e.toString());
		}

	}
}