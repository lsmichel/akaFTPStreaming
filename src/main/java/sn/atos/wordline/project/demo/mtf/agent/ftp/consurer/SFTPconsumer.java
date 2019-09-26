package sn.atos.wordline.project.demo.mtf.agent.ftp.consurer;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.remote.RemoteFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sn.atos.wordline.project.demo.mtf.agent.data.transfert.MTFSender;

@Component
public class SFTPconsumer extends RouteBuilder {
	@Autowired
	MTFSender _mTFSender ;
	
	@Override
	public void configure() throws Exception {
		from("sftp://admin@localhost:2223?password=admin&binary=true&delete=true")
        .process(new Processor() {
            public void process(final Exchange exchange) throws Exception { 
            	byte[] fileData = exchange.getIn().getBody(byte[].class);
				RemoteFile _rFile = exchange.getIn().getBody(RemoteFile.class);
				_mTFSender.SendFile(fileData, _rFile.getFileName() );	
        };
  });
}
}
