package sn.atos.wordline.project.demo.mtf.agent;


import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoFtpServerApplication {
	@Autowired
	CamelContext camelContext;

	public static void main(String[] args) {
		
	    SpringApplication.run(DemoFtpServerApplication.class, args);  
	    
	}
}
