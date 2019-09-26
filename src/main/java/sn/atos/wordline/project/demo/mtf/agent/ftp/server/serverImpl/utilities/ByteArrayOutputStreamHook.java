
package sn.atos.wordline.project.demo.mtf.agent.ftp.server.serverImpl.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author A746054
 */
public class ByteArrayOutputStreamHook extends ByteArrayOutputStream{
  private Runnable action;

	private ByteArrayOutputStream byteArrayOutputStream;

	private boolean wasClosedBefore = false;

	public ByteArrayOutputStreamHook(ByteArrayOutputStream byteArrayOutputStream) {
		this.byteArrayOutputStream = byteArrayOutputStream;
	}

	@Override
	public void close() throws IOException {
		super.close();
		byteArrayOutputStream.close();

		if (action != null && !wasClosedBefore) {
			wasClosedBefore = true;
			action.run();
		}
	}

	public void onClose(Runnable action) {
		this.action = action;
	}  
}
