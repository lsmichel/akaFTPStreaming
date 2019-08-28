package com.example.demo.FTPserver.ftpConsure;

import akka.Done;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.alpakka.ftp.FtpCredentials;
import akka.stream.alpakka.ftp.javadsl.Ftp;
import akka.stream.alpakka.ftp.FtpSettings;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import java.io.PrintWriter;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import static akka.actor.ActorRef.noSender;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionStage;
import redis.clients.jedis.Jedis;
import static com.example.demo.FTPserver.AkkaSpringExtentions.SpringExtension.SPRING_EXTENSION_PROVIDER;


@Component
@EnableScheduling
public class Ftpconsumer {
	@Autowired
	ActorSystem system;
	
	private ActorRef dataTransferManagerActor=null;
	private ActorRef dataTransferActor =null ;
	
	private static final Logger log = LoggerFactory.getLogger(Ftpconsumer.class);
	
	@Scheduled(fixedRate = 5000)
	private void consume() throws UnknownHostException {
		ActorMaterializer materializer = ActorMaterializer.create(system);
	
		if(dataTransferManagerActor ==null) {
			dataTransferManagerActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("dataTransferManagerActor"), "dataTransferManagerActor"); 
		}
		if(dataTransferActor ==null) {
			dataTransferActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system).props("dataTransferActor"), "dataTransferActor"); 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataTransferManagerActor.tell(dataTransferActor, noSender());
		}
		FtpCredentials credentials = new FtpCredentials() {
			@Override
			public String username() {
				return "admin";
			}

			@Override
			public String password() {
				return "admin";
			}
		};
		FtpSettings ftpSettings = FtpSettings.create(InetAddress.getByName("localhost"))
				.withPort(2121)
				.withCredentials(credentials).withBinary(true).withPassiveMode(true)
				.withConfigureConnectionConsumer((FTPClient ftpClient) -> {
					ftpClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
				});
		HashMap<String, List<byte[]>> result = null;
		try {
			 listFiles(".", ftpSettings,  materializer).forEach(s -> {
				 
				// dataTransferActor.tell(  s.concat(ByteString.fromString("fin")), noSender());
		});
		
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}

	public  List<ByteString> listFiles(String basePath, FtpSettings settings,
			Materializer materializer) throws Exception {
		HashMap<String, List<byte[]>> result = new HashMap<String, List<byte[]>>();
		List<ByteString> resultByteString = new ArrayList<ByteString>();
		Ftp.ls(basePath, settings).runForeach(ftpFile -> {
			List<ByteString> ByteStringList = new ArrayList<ByteString>();
			if (!ftpFile.isDirectory()) {
				List<byte[]> resultint = new ArrayList<byte[]>();
				Sink<ByteString, CompletionStage<Done>> printlnSink = Sink.<ByteString>foreach(chunk -> {
					ByteStringList.add(chunk);
					resultint.add(chunk.toArray());
					result.put(ftpFile.path(), resultint);
				});
				Source<ByteString, CompletionStage<IOResult>> promise = retrieveFromPath(ftpFile.path(), settings);
				CompletionStage<IOResult> ioResult = promise.to(printlnSink).run(materializer);
				ioResult.toCompletableFuture().get();
				ByteString ByteStringResult = null ;
				for(int i= 0 ; i< ByteStringList.size() ; i++) {
					if(i==0) {
						ByteStringResult = ByteStringList.get(i);
					}else {
						ByteStringResult = ByteStringResult.concat(ByteStringList.get(i));
					}
				}
				log.info("=========================================  file name "+ftpFile.name());
				resultByteString.add(ByteStringResult);
				
			}
		}, materializer).toCompletableFuture().get();
		return resultByteString;
	}

	public static Source<ByteString, CompletionStage<IOResult>> retrieveFromPath(String path, FtpSettings settings)
			throws Exception {
		return Ftp.fromPath(path, settings);
	}

	public static void printResult(HashMap<String, List<byte[]>> result) {
		if (result != null) {

			for (String key : result.keySet()) {
				int i = 0;
				
				HashMap<byte[], byte[]> obj = new HashMap<byte[], byte[]>();

				for (byte[] bloc : result.get(key)) {
				
					obj.put((key + i).getBytes(), bloc);
					Jedis jedis = new Jedis("localhost", 6370);
					jedis.hmset(("storage" + key).getBytes(), obj);
					List<byte[]> blocstored = jedis.hmget(("storage" + key).getBytes(), (key + i).getBytes());
					
					i++;
				}
				i = 0;
			}
		}
	}
}
