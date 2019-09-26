/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl;


import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.ftpserver.ftplet.FtpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sn.atos.wordline.project.demo.mtf.agent.data.transfert.MTFSender;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.users.FtpServerUser;

@Component
public class FTPServer {
	@Autowired
	MTFSender _mTFSender ;
	private static final int PORT = 2222;
	private FtpServerImpl ftpServerImpl =null ;
	
	@PostConstruct
    private void start() {
		 FtpServerUser FTP_SERVER_USER = new FtpServerUser("admin", "admin", true, 2);
		ftpServerImpl = new FtpServerImpl(PORT, file ->
		   {
			    _mTFSender.SendFile(file.getData(), file.getName());
		   } , Arrays.asList(FTP_SERVER_USER));
		
		try {
			ftpServerImpl.start();
		} catch (FtpException e) {
			
			e.printStackTrace();
		}
	}
	@PreDestroy
    private void stop() {
        if (ftpServerImpl != null) {
        	ftpServerImpl.stop();
        }
    }  
    
}
