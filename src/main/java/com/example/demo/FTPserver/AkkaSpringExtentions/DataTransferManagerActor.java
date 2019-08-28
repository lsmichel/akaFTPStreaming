package com.example.demo.FTPserver.AkkaSpringExtentions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.util.ByteString;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataTransferManagerActor extends  AbstractActor {
	private String state = "stopSending";
	private List<ByteString> dataQue = new ArrayList<ByteString>() ;
	private ActorRef dataTransferActor=null;
	
	 private static final Logger log = LoggerFactory.getLogger(DataTransferManagerActor.class);

	 @Override
	   public Receive createReceive() {
	     return receiveBuilder()
	         .match(
	        	 ActorRef.class,
	             act -> {
	            	 
	            	 if(dataTransferActor ==null)
	            		 dataTransferActor=act;
	             })
	         .match(
	        	 ByteString.class,
	             msg -> {
	               if(state=="sending") {
	            	   dataQue.add(msg);
	               }
	                   
	               else {
	            	   
	            	   dataTransferActor.tell(msg, getSelf());
	            	  // state="sending";
	               }
	             })
	         .build();
	   }

}
