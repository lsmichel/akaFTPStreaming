package sn.atos.wordline.project.demo.mtf.agent.consure.dto;

import java.util.List;

import sn.atos.wordline.project.demo.mtf.agent.Grpcservice.filePart;

public class FileToTransfertInfo {
	private List<FilePartTDO> data  ;
    private String identifiant ;
    private Integer size ;
    private String fileName ;
    private Integer blocNumber ;
    public String getIdentifiant() {
        return identifiant;
    }

    public List<FilePartTDO> getData() {
        return data;
    }

    public void setData(List<FilePartTDO> data) {
        this.data = data;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
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

    public Integer getBlocNumber() {
        return blocNumber;
    }

    public void setBlocNumber(Integer blocNumber) {
        this.blocNumber = blocNumber;
    }
    
	

}
