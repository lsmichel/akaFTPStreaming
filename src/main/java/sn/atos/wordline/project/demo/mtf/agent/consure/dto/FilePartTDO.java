package sn.atos.wordline.project.demo.mtf.agent.consure.dto;

import akka.util.ByteString;

public class FilePartTDO {
	  private akka.util.ByteString data  ;
	    private String identifiant ;
	    private Integer size ;
	    private String fileName ;
	    private Integer blocNumber ; 

	    public ByteString getData() {
	        return data;
	    }

	    public void setData(ByteString data) {
	        this.data = data;
	    }
	    public String getIdentifiant() {
	        return identifiant;
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
