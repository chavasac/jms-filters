package com.sachin.jms.claimmanagement;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClaimManagement {

	public static void main(String[] args) throws NamingException, JMSException {
		InitialContext initialContext = new InitialContext();
		Queue claimQueue = (Queue) initialContext.lookup("queue/claimQueue");

		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		JMSContext jmsContext = connectionFactory.createContext();

		JMSProducer producer = jmsContext.createProducer();
		ObjectMessage message = jmsContext.createObjectMessage();
		/*
		 * To see if the messages are getting filtered out, change the value of below
		 * property anything other than 1
		 */
		// message.setIntProperty("hospitalId", 1);
		// message.setDoubleProperty("claimAmount", 1000);
		// message.setStringProperty("doctorName", "Iryna");
		message.setStringProperty("doctorType", "Gyna");

		/*
		 * Here we have created a consumer with filter using selector expression
		 * hospitalId=1 So this consumer will only send message ahead if the hospital id
		 * of the message is 1
		 */
		// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "hospitalId=1");
		// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "claimAmount
		// BETWEEN 1001 AND 5000");
		// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorName LIKE 'Ir%'");
		// JMSConsumer consumer = jmsContext.createConsumer(claimQueue, "doctorType IN ('Neuro', 'Gyna')");

		/*
		 * To demonstrate how the Selected JMS headers can be used for filtering,
		 * consider below example of how the JMSPriority is being used along with
		 * doctorType filter. We have used OR expression to check if JMSPriority is
		 * between 3 & 6. As default message priority is 4, this will become true
		 */
		JMSConsumer consumer = jmsContext.createConsumer(claimQueue,
				"doctorType IN ('Neuro', 'General') OR JMSPriority BETWEEN 3 AND 6");

		Claim claim = new Claim();
		claim.setHospitalId(1);
		claim.setDoctorName("Iryna");
		claim.setDoctorType("Gyna");
		claim.setInsuranceProvider("Blue Cross");
		claim.setClaimAmount(1000);

		message.setObject(claim);
		producer.send(claimQueue, message);

		Claim receiveClaim = consumer.receiveBody(Claim.class);
		System.out.println("Received claim amount : " + receiveClaim.getClaimAmount());

	}

}
