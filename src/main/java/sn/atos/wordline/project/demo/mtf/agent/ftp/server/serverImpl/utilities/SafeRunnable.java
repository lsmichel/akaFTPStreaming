/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.utilities;

/**
 *
 * @author A746054
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SafeRunnable {
    private static final Logger logger = LoggerFactory.getLogger(SafeRunnable.class);

	public static Runnable of(Runnable unsafe) {
		return () -> {
			try {
				unsafe.run();
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		};
	}
}
