package sn.atos.wordline.project.demo.mtf.agent.data.transfert;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import akka.actor.ActorSystem;
import akka.grpc.GrpcClientSettings;
import akka.stream.ActorMaterializer;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManager;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManagerClient;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.filePart;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManagerClient.DefaultFileTransfertManagerClient;

@Component
public class MTFSender {
	@Autowired
	ActorSystem system;

	public void SendFile(byte[] fileData, String fileName) {
		Date date = new Date();
		Long time = date.getTime();
		ActorMaterializer materializer = ActorMaterializer.create(system);
		GrpcClientSettings settings = GrpcClientSettings.fromConfig(FileTransfertManager.name, system);
		FileTransfertManagerClient client = DefaultFileTransfertManagerClient.create(settings, materializer,
				system.dispatcher());

		filePart _filePart;
		int i = 0;
		int blocNumber = fileData.length + 1;
		int bloclength = (int) Math.floor(fileData.length / blocNumber);
		if (bloclength * blocNumber != fileData.length)
			blocNumber = blocNumber + 1;
		if (fileData.length < blocNumber) {
			_filePart = filePart.newBuilder().setFileName(fileName)
					.setChunk(com.google.protobuf.ByteString.copyFrom(fileData)).setTotalblocNumber(1)
					.setIdentifiant(time.toString()).setZise(String.valueOf(0.0)).setBlocNumber(0).build();
			client.postFile(_filePart);
		} else {

			for (int index = 0; index < fileData.length; index = index + bloclength) {
				if (index < fileData.length) {
					_filePart = filePart.newBuilder().setFileName(fileName)
							.setChunk(com.google.protobuf.ByteString
									.copyFrom(Arrays.copyOfRange(fileData, index, index + bloclength)))
							.setTotalblocNumber(blocNumber).setIdentifiant(time.toString()).setZise(String.valueOf(0.0))
							.setBlocNumber(i).build();
					client.postFile(_filePart);
				} else {
					_filePart = filePart.newBuilder().setFileName(fileName)
							.setChunk(com.google.protobuf.ByteString
									.copyFrom(Arrays.copyOfRange(fileData, index, fileData.length)))
							.setTotalblocNumber(blocNumber).setIdentifiant(time.toString()).setZise(String.valueOf(0.0))
							.setBlocNumber(i).build();
					client.postFile(_filePart);
				}
				i++;
			}

		}

	}
}
