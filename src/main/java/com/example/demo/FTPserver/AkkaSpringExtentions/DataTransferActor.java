package com.example.demo.FTPserver.AkkaSpringExtentions;

import java.net.InetSocketAddress;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.demo.FTPserver.ftpConsure.Ftpconsumer;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor;
import akka.io.Tcp;
import akka.io.Tcp.CommandFailed;
import akka.io.Tcp.Connected;
import akka.io.Tcp.ConnectionClosed;
import akka.io.Tcp.Received;
import akka.io.TcpMessage;
import  akka.io.Tcp.CompoundWrite;
import  akka.io.Tcp.WriteCommand;
import akka.util.ByteString;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Component
@Order(1)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataTransferActor extends  AbstractActor {
	
       private static final Logger log = LoggerFactory.getLogger(DataTransferActor.class);
       private static Integer nb = 0 ;
	
       @Value("${data.tranfert.tcp.aresse}")
       private String adresse;
       
       @Value("${data.tranfert.tcp.port}")
       private Integer port;
       
	   InetSocketAddress remote;
	   ActorRef listener;

	   @Override
	   public void preStart() throws Exception {
		   final ActorRef tcp = Tcp.get(getContext().getSystem()).manager();
		   remote = new InetSocketAddress(adresse, port);
		   tcp.tell(TcpMessage.connect(remote), getSelf());
	   }

	   @Override
	   public Receive createReceive() {
	     return receiveBuilder()
	         .match(
	             CommandFailed.class,
	             msg -> {
	            	 
	               getContext().stop(getSelf());
	             })
	         .match(
	             Connected.class,
	             msg -> {
	               getSender().tell(TcpMessage.register(getSelf()), getSelf());
	               getContext().become(connected(getSender()));
	             })
	         .build();
	   }

	   private Receive connected(final ActorRef connection) {
	     return receiveBuilder()
	         .match(
	        	
	             ByteString.class,
	             msg -> {
	            	 if(nb==0) {
		            	   nb=1; 
	               connection.tell(TcpMessage.write( msg), getSelf());
	            	 }
	             })
	         .match(
	             CommandFailed.class,
	             msg -> {
	              
	             })
	         .match(
	             Received.class,
	             msg -> {
	            	 
	             })
	         .matchEquals(
	             "close",
	             msg -> {
	               
	               connection.tell(TcpMessage.close(), getSelf());
	               
	               
	             })
	         .match(
	             ConnectionClosed.class,
	             msg -> {
	               getContext().stop(getSelf());
	             })
	         .build();
	   }
}
