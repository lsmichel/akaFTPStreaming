/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.vitualfilesystem;

import java.util.HashMap;
import java.util.Map;
import org.apache.ftpserver.ftplet.FileSystemFactory;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;

/**
 *
 * @author A746054
 */
public class VirtualFtpFilesystemFactory implements FileSystemFactory{
   private Map<User, VirtualFileSystemView> fileSystems = new HashMap<>();

	private FileUploadListener fileUploadedListener;

	@Override
	public FileSystemView createFileSystemView(User user) throws FtpException {
		VirtualFileSystemView virtualFileSystemView = new VirtualFileSystemView(user);
		virtualFileSystemView.setFileUplodedListener(fileUploadedListener);
		fileSystems.put(user, virtualFileSystemView);
		return virtualFileSystemView;
	}

	public void setFileUploadedListener(FileUploadListener fileUploadedListener) {
		this.fileUploadedListener = fileUploadedListener;
	}

}
