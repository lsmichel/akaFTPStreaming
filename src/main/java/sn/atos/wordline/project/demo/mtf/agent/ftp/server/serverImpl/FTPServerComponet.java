package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl;


import sn.atos.wordline.project.demo.mtf.agent.dto.FTPInfo;
import sn.atos.wordline.project.demo.mtf.agent.dto.UserInfo;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.utiles.Properties;
import sn.atos.wordline.project.demo.mtf.agent.ftp.server.utiles.PropertiesHelper;

import org.apache.ftpserver.*;
import org.apache.ftpserver.ftplet.*;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@Component
public class FTPServerComponet {
   
	private static final Logger logger = LoggerFactory.getLogger(FTPServerComponet.class);

    private FtpServer ftpServer;
    private UserManager um;

	
	private static final String CONFIG_FILE_NAME = "application.properties";
    private static final String USERS_FILE_NAME = "users.properties";
    private static final int MAX_IDLE_TIME = 300;

    @Value("${server.host}")
    private String host;
    @Value("${ftp.port}")
    private int port;
    @Value("${ftp.passive-ports}")
    private String passivePorts;
    @Value("${ftp.max-login}")
    private Integer maxLogin;
    @Value("${ftp.max-threads}")
    private Integer maxThreads;
    @Value("${ftp.username}")
    private String username;
    @Value("${ftp.password}")
    private String password;
    @Value("${ftp.home-dir}")
    private String homeDir;
    
    @PostConstruct
    private void start() {
        mkHomeDir(homeDir);
        try {
            createConfigFile();
        } catch (IOException e) {
        	e.printStackTrace();
           
        }
        FtpServerFactory serverFactory = new FtpServerFactory();
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        connectionConfigFactory.setMaxLogins(maxLogin);
        connectionConfigFactory.setMaxThreads(maxThreads);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());

        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setPort(port);
        if (!Objects.equals(passivePorts, "")) {
            DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();
            
            dataConnectionConfFactory.setPassivePorts(passivePorts);
            if (!(Objects.equals(host, "localhost") || Objects.equals(host, "127.0.0.1"))) {
                
                dataConnectionConfFactory.setPassiveExternalAddress(host);
            }
            listenerFactory.setDataConnectionConfiguration(
                    dataConnectionConfFactory.createDataConnectionConfiguration());
        }

        serverFactory.addListener("default", listenerFactory.createListener());
        
        
        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File umFile = new File(USERS_FILE_NAME);
        //logger.info(umFile.getAbsolutePath());
        userManagerFactory.setFile(umFile);
       
        userManagerFactory.setAdminName(username);
        um = userManagerFactory.createUserManager();
        try {
            initUser();
        } catch (FtpException e) {
            logger.warn("init user fail:", e);
            return;
        }
        serverFactory.setUserManager(um);

        ftpServer = serverFactory.createServer();
        try {
            ftpServer.start();
        } catch (FtpException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
    }

    @PreDestroy
    private void stop() {
        if (ftpServer != null) {
            ftpServer.stop();
        }
    }

    private void initUser() throws FtpException {
        boolean exist = um.doesExist(username);
        
        if (!exist) {
            List<Authority> authorities = new ArrayList<>();
            authorities.add(new WritePermission());
            authorities.add(new ConcurrentLoginPermission(0, 0));
            BaseUser user = new BaseUser();
            user.setName(username);
            user.setPassword(password);
            user.setHomeDirectory(homeDir);
            user.setMaxIdleTime(MAX_IDLE_TIME);
            user.setAuthorities(authorities);
            um.save(user);
        }
    }

    public void setPassword(UserInfo userInfo) throws FtpException {
        String username = um.getAdminName();
        User savedUser = um.authenticate(new UsernamePasswordAuthentication(username, userInfo.getOldPassword()));
        BaseUser baseUser = new BaseUser(savedUser);
        baseUser.setPassword(userInfo.getPassword());
        um.save(baseUser);
    }

    
    public void setHomeDir(String homeDir) throws FtpException, IOException {
        User userInfo = um.getUserByName(um.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        mkHomeDir(homeDir);
        baseUser.setHomeDirectory(homeDir);
        um.save(baseUser);
        
        Properties ftpProperties = PropertiesHelper.getProperties(CONFIG_FILE_NAME);
        if (!homeDir.endsWith("/")) {
            homeDir += "/";
        }
        ftpProperties.setProperty("ftp.home-dir", homeDir);
        PropertiesHelper.saveProperties(ftpProperties, CONFIG_FILE_NAME);
    }

    
    public void setMaxDownloadRate(int maxDownloadRate) throws FtpException {
        int maxUploadRate = getFTPInfo().getMaxUploadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }

    public void setMaxUploadRate(int maxUploadRate) throws FtpException {
        int maxDownloadRate = getFTPInfo().getMaxDownloadRate();
        saveTransferRateInfo(maxUploadRate * 1024, maxDownloadRate * 1024);
    }

   
    private void saveTransferRateInfo(int maxUploadRate, int maxDownloadRate) throws FtpException {
        User userInfo = um.getUserByName(um.getAdminName());
        BaseUser baseUser = new BaseUser(userInfo);
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        authorities.add(new TransferRatePermission(maxDownloadRate, maxUploadRate));
        baseUser.setAuthorities(authorities);
        um.save(baseUser);
    }

   
    public FTPInfo getFTPInfo() throws FtpException {
        User userInfo = um.getUserByName(um.getAdminName());
        TransferRateRequest transferRateRequest = (TransferRateRequest) userInfo
                .authorize(new TransferRateRequest());
        File homeDir = Paths.get(userInfo.getHomeDirectory()).toFile();
        long totalSpace = homeDir.getTotalSpace();
        long usedSpace = totalSpace - homeDir.getUsableSpace();

        return new FTPInfo(host, port, homeDir.getAbsolutePath(),
                transferRateRequest.getMaxDownloadRate() / 1024,
                transferRateRequest.getMaxUploadRate() / 1024,
                usedSpace, totalSpace);
    }

    private void mkHomeDir(String homeDir) {
        try {
            Files.createDirectories(Paths.get(homeDir, "temp"));
        } catch (IOException e) {
            
            throw new UncheckedIOException(e);
        }
    }

    private void createConfigFile() throws IOException {
        File configFile = new File(CONFIG_FILE_NAME);
        if (!configFile.exists()) {
            boolean result = configFile.createNewFile();
            if (!result) {
               
            }
        }
        File usersFile = new File(USERS_FILE_NAME);
        if (!usersFile.exists()) {
            boolean result = usersFile.createNewFile();
            if (!result) {
                
            }
        }
    }
}
