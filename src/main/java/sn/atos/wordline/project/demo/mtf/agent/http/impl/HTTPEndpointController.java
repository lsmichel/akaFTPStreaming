package sn.atos.wordline.project.demo.mtf.agent.http.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import sn.atos.wordline.project.demo.mtf.agent.data.transfert.MTFSender;

@RestController
@RequestMapping("/file")
@CrossOrigin(origins = "*")
public class HTTPEndpointController {
	@Autowired
	MTFSender _mTFSender ;
	@PostMapping("/upload") 
    public String singleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
        	
        	 return "file is empty";
        }
        else {
        	try {
				_mTFSender.SendFile(file.getBytes(), file.getOriginalFilename());
				 return "sucess full uploadeds"+file.getOriginalFilename();
			} catch (IOException e) {
				
				e.printStackTrace();
				return "error while sending file";
			}
        }
    }
}
