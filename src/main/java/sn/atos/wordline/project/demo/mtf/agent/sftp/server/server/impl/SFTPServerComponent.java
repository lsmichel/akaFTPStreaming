package sn.atos.wordline.project.demo.mtf.agent.sftp.server.server.impl;

import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;



import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import static com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder.newLinux;

@Component
public class SFTPServerComponent {
	private Log log = LogFactory.getLog(SFTPServerComponent.class);
	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin";
	private static final String host ="localhost";
	private static final int port = 2223;
	private SshServer sshd;
	private FileSystem fileSystem;

	@PostConstruct
	private void start() {
	    try {
			fileSystem = createFileSystem();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sshd = SshServer.setUpDefaultServer();
		sshd.setHost(host);
		sshd.setPort(port);
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
		sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
		sshd.setPasswordAuthenticator(
				(username, password, session) -> username.equals(USERNAME) && password.equals(PASSWORD));
		sshd.setFileSystemFactory(session -> new InmemoryFileSystem(fileSystem));
		
		
		
		try {
			sshd.start();
		
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		log.info("SFTP server started");
	}

	@PreDestroy
	private void stop() {
		if (sshd != null) {
			
				try {
					sshd.stop();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
		}
	}
	
	private static class InmemoryFileSystem extends FileSystem {
        final FileSystem fileSystem;

        InmemoryFileSystem(
            FileSystem fileSystem
        ) {
            this.fileSystem = fileSystem;
           
        }

        @Override
        public FileSystemProvider provider() {
            return fileSystem.provider();
        }

        @Override
        public void close(
        ) throws IOException {
            //will not be closed
        }

        @Override
        public boolean isOpen() {
            return fileSystem.isOpen();
        }

        @Override
        public boolean isReadOnly() {
            return fileSystem.isReadOnly();
        }

        @Override
        public String getSeparator() {
            return fileSystem.getSeparator();
        }

        @Override
        public Iterable<Path> getRootDirectories() {
            return fileSystem.getRootDirectories();
        }

        @Override
        public Iterable<FileStore> getFileStores() {
            return fileSystem.getFileStores();
        }

        @Override
        public Set<String> supportedFileAttributeViews() {
            return fileSystem.supportedFileAttributeViews();
        }

        @Override
        public Path getPath(
            String first,
            String... more
        ) {
            return fileSystem.getPath(first, more);
        }

        @Override
        public PathMatcher getPathMatcher(
            String syntaxAndPattern
        ) {
            return fileSystem.getPathMatcher(syntaxAndPattern);
        }

        @Override
        public UserPrincipalLookupService getUserPrincipalLookupService() {
            return fileSystem.getUserPrincipalLookupService();
        }

        @Override
        public WatchService newWatchService() throws IOException {
            return fileSystem.newWatchService();
        }
    }
	private FileSystem createFileSystem(
		    ) throws IOException {
		        fileSystem = newLinux().build("SftpServerMemoryFs" + hashCode());
		        return fileSystem;
		    }
}
