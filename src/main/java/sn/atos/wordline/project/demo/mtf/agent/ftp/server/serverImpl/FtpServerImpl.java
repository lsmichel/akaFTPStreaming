/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl;

import java.util.List;
import org.apache.ftpserver.DataConnectionConfiguration;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;

import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.users.DynamicUserManager;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.users.FtpServerUser;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.vitualfilesystem.FileUploadListener;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.vitualfilesystem.VirtualFtpFilesystemFactory;

/**
 *
 * @author A746054
 */
public class FtpServerImpl {
   private int port;

	private FileUploadListener fileUploadedListener;
	private List<FtpServerUser> users;
	private FtpServer server;
	private String passivePortsString = null;

	/**
	 * The fileUploadListener runs in a FTP client thread, so the action should be short or be executed
	 * asynchronously.
	 */
	public FtpServerImpl(int port, FileUploadListener fileUploadedListener, List<FtpServerUser> users) {
		this.port = port;
		this.fileUploadedListener = fileUploadedListener;
		this.users = users;
	}

	public void start() throws FtpException {
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		factory.setPort(port);
		factory.setDataConnectionConfiguration(createDataConnectionConfig());
		serverFactory.addListener("default", factory.createListener());
		serverFactory.setUserManager(new DynamicUserManager(users));
		VirtualFtpFilesystemFactory fileSystem = new VirtualFtpFilesystemFactory();
		fileSystem.setFileUploadedListener(fileUploadedListener);
		serverFactory.setFileSystem(fileSystem);
		server = serverFactory.createServer();
		server.start();
	}

	private DataConnectionConfiguration createDataConnectionConfig() {
		DataConnectionConfigurationFactory dataConnectionConfigurationFactory = new DataConnectionConfigurationFactory();

		if (passivePortsString != null && !passivePortsString.trim().isEmpty()) {
			dataConnectionConfigurationFactory.setPassivePorts(passivePortsString);
		}

		return dataConnectionConfigurationFactory.createDataConnectionConfiguration();
	}

	public void stop() {
		server.stop();
	}

	/**
	 * See DataConnectionConfigurationFactory.setPassivePorts
	 *
	 * Defaults to any available port, which may be undesirable.
	 */
	public void setPassivePortsString(String passivePortsString) {
		this.passivePortsString = passivePortsString;
	}
}
