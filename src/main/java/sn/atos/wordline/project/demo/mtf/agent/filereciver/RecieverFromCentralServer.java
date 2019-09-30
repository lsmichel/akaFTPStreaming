package sn.atos.wordline.project.demo.mtf.agent.filereciver;

import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.grpc.GrpcClientSettings;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManager;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManagerClient;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManagerHandlerFactory;
import static  sn.atos.wordline.project.demo.mtf.agent.AkkaSpringExtentions.SpringExtension.SPRING_EXTENSION_PROVIDER;
public class RecieverFromCentralServer {
	@Autowired
	ActorSystem system;
	@PostConstruct
    private void start() {
		Config conf = ConfigFactory.parseString("akka.http.server.preview.enable-http2 = on")
                .withFallback(ConfigFactory.defaultApplication());
        ActorSystem sys = ActorSystem.create("HelloWorld", conf);
        Materializer mat = ActorMaterializer.create(sys);
        GrpcClientSettings settings = GrpcClientSettings.fromConfig(FileTransfertManager.name, sys);
		FileTransfertManagerClient client =  FileTransfertManagerClient.DefaultFileTransfertManagerClient.create(settings, mat, sys.dispatcher());
		ActorRef dataRecieverManagerActor = system.actorOf(SPRING_EXTENSION_PROVIDER.get(system)
				  .props("dataRecieverManagerActor"), "dataRecieverManagerActor");
         
        try {
			run(sys ,dataRecieverManagerActor , client ).thenAccept(binding -> {
			    System.out.println("gRPC server bound to: " + binding.localAddress());
			   
			});
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}
	
	  public static CompletionStage<ServerBinding> run(ActorSystem sys , ActorRef transfertManagerActor , FileTransfertManagerClient client) throws Exception {
	        Materializer mat = ActorMaterializer.create(sys);
	        RecieverFromCentralImpl impl = new RecieverFromCentralImpl(mat , transfertManagerActor ,  client);
	        return Http.get(sys).bindAndHandleAsync(
	                FileTransfertManagerHandlerFactory.create(impl, mat, sys  ),
	                ConnectHttp.toHost("0.0.0.0", 8083),
	                mat);
	    }
}
