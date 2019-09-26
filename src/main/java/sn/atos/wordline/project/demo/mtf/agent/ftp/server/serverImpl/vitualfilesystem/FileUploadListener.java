/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.vitualfilesystem;

import sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.utilities.SimpleFile;

/**
 *
 * @author A746054
 */
public interface FileUploadListener {
    void onFileUploadDone(SimpleFile file);
}
