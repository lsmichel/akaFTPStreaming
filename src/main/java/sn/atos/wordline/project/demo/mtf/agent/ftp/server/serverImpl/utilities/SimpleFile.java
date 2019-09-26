/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.utilities;

import java.time.LocalDateTime;

/**
 *
 * @author A746054
 */
public class SimpleFile {
        private final String uploader;

	private final String name;

	private final byte[] data;

	private final LocalDateTime uploaded;

	public SimpleFile(String name, byte[] data, String uploader, LocalDateTime uploaded) {
		this.name = name;
		this.data = data;
		this.uploader = uploader;
		this.uploaded = uploaded;
	}

	public String getName() {
		return name;
	}

	public byte[] getData() {
		return data;
	}

	public String getUploader() {
		return uploader;
	}

	public LocalDateTime getUploaded() {
		return uploaded;
	}

	@Override
	public String toString() {
		return "SimpleFile [uploader=" + uploader + ", name=" + name + ", uploaded=" + uploaded + "]";
	}
}
