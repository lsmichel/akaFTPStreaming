package sn.atos.wordline.project.demo.mtf.agent.AkkaSpringExtentions;

import java.io.OutputStream;
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
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.FileTransfertManagerClient;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.fileInfo;
import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.filePart;
import sn.atos.wordline.project.demo.mtf.agent.consure.dto.FilePartTDO;
import sn.atos.wordline.project.demo.mtf.agent.consure.dto.FileToTransfertInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DataRecieverManagerActor extends  AbstractActor {
	 private static List<FileToTransfertInfo> _listFileToTransfertInfo;
	    private int count = 0;
	    private  FileTransfertManagerClient client;

	    

	    @Override
	    public Receive createReceive() {
	        return receiveBuilder()
	                .match(
	                        fileInfo.class,
	                        fileinfos -> {
	                            // System.out.println(fileinfos.getIdentifiant());
	                            searchOrCreateFile(fileinfos);
	                        })
	                .match(
	                        filePart.class,
	                        filepart -> {
	                            count++;
	                            //System.out.println("======================>> recieved " + count);
	                            addFile(filepart);
	                            // getSelf().tell("write", getSelf());
	                        })
	                .matchEquals(
	                        "write",
	                        msg -> {
	                            // System.out.println("tranfered ========***** "+_listFileToTransfertInfo.size());
	                            searchAndSaveTerminatedTransfert();
	                        })
	                .build();
	    }

	    public static void searchOrCreateFile(fileInfo fileinfos) {
	        boolean b = false;
	        for (FileToTransfertInfo fileToTransfertInfo : _listFileToTransfertInfo) {
	            if (fileToTransfertInfo.getIdentifiant().equals(fileinfos.getIdentifiant())) {
	                b = true;
	            }
	        }
	        if (b == false) {
	            FileToTransfertInfo _fileToTransfertInfo = new FileToTransfertInfo();
	            _fileToTransfertInfo.setBlocNumber(fileinfos.getTotalblocNumber());
	            _fileToTransfertInfo.setFileName(fileinfos.getFileName());
	            _fileToTransfertInfo.setIdentifiant(fileinfos.getIdentifiant());
	            _fileToTransfertInfo.setSize(fileinfos.getSize());
	            _fileToTransfertInfo.setData(new ArrayList<FilePartTDO>());
	            _listFileToTransfertInfo.add(_fileToTransfertInfo);
	            // System.out.println(_fileToTransfertInfo.getBlocNumber());
	        }
	    }

	    public void addFile(filePart filepart) {
	       // System.out.println("=========================boucle");
	        FilePartTDO _filePartTDO = new FilePartTDO();
	        _filePartTDO.setBlocNumber(filepart.getBlocNumber());
	        _filePartTDO.setFileName(filepart.getFileName());
	        _filePartTDO.setData(akka.util.ByteString.fromArray(filepart.getChunk().toByteArray()));
	        boolean add = false;
	        for (FileToTransfertInfo fileToTransfertInfo : _listFileToTransfertInfo) {

	            if (fileToTransfertInfo.getIdentifiant().equals(filepart.getIdentifiant())) {

	                fileToTransfertInfo.getData().add(_filePartTDO);
	                add = true;
	                if (fileToTransfertInfo.getData().size() == fileToTransfertInfo.getBlocNumber()) {
	                    searchAndSaveTerminatedTransfert();
	                }
	            }
	        }
	        if (!add) {
	            FileToTransfertInfo _fileToTransfertInfo = new FileToTransfertInfo();
	            _fileToTransfertInfo.setBlocNumber(filepart.getTotalblocNumber());
	            _fileToTransfertInfo.setFileName(filepart.getFileName());
	            _fileToTransfertInfo.setIdentifiant(filepart.getIdentifiant());
	            //_fileToTransfertInfo.setSize(filepart.getSize());
	            _fileToTransfertInfo.setData(new ArrayList<FilePartTDO>());
	            _fileToTransfertInfo.getData().add(_filePartTDO);
	            _listFileToTransfertInfo.add(_fileToTransfertInfo);
	            if (_fileToTransfertInfo.getData().size() == _fileToTransfertInfo.getBlocNumber()) {
	                searchAndSaveTerminatedTransfert();
	            }
	        }

	    }

	    public void searchAndSaveTerminatedTransfert() {
	        for (FileToTransfertInfo fileToTransfertInfo : _listFileToTransfertInfo) {
	            if (fileToTransfertInfo.getBlocNumber() == fileToTransfertInfo.getData().size()) {
	                OutputStream os = null;
	                System.out.println("========================= bloc number " +fileToTransfertInfo.getData().size());
	                
	                    akka.util.ByteString allFileContent = null;
	                    
	                    for (int i = 0; i < fileToTransfertInfo.getBlocNumber(); i++) {
	                        if (i == 0) {
	                            allFileContent = getBloc(i, fileToTransfertInfo);
	                        } else {
	                            allFileContent = allFileContent.concat(getBloc(i, fileToTransfertInfo));
	                            
	                   }
	                }
	            }
	        }
	    }

	    private akka.util.ByteString getBloc(int i, FileToTransfertInfo fileToTransfertInfo) {
	        akka.util.ByteString res = null;
	        for (FilePartTDO data : fileToTransfertInfo.getData()) {
	            if (data.getBlocNumber() == i) {
	                res = data.getData();

	            }
	        }
	        return res;
	    }

}
