package sn.atos.wordline.project.demo.mtf.agent.consure.dto;

import java.util.List;

import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.filePart;

public class FileToTransfertInfo {
	private List<filePart> fileParts;
	private Integer size;
	private String fileName;
	private String identifiant ;

	public List<filePart> getFileParts() {
		return fileParts;
	}

	public void setFileParts(List<filePart> fileParts) {
		this.fileParts = fileParts;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getIdentifiant() {
		return identifiant;
	}

	public void setIdentifiant(String identifiant) {
		this.identifiant = identifiant;
	}
	

}
