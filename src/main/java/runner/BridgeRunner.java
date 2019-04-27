package runner;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.pmw.tinylog.Logger;

import helpers.SystemHelper;
import infrastructure.Constants;
import initializer.InitializationHandler;

public class BridgeRunner extends Constants {

	public static void main(String[] args) throws Exception {

		try {

			test_arg = args[0];
			almUser = args[1];
			almUserPassword = args[2];
			testPlanFolderId = args[3];
			testSetId = args[4];
			testRunAttachmentType = args[5];
			testAttachment = args[6];

		} catch (ArrayIndexOutOfBoundsException e) {
			SystemHelper.printHelpMessage();
		}

		SSLContext ctx = SSLContext.getInstance("TLS");
		ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
		SSLContext.setDefault(ctx);

		try {
			Logger.info("***************************** Execution Started *****************\n");

			InitializationHandler.inializeALMConfigurations();
			InitializationHandler.initializeLogger();
			
			BridgeRunnerHelper.handleALMOperationBasedOnFrameworkType();
			
			Logger.info("All reports updated successfully");
			Logger.info("***************************** Execution Ends *****************\n");
		} catch (Exception e) {
			Logger.warn("problem encountered while parsing test reports -\n"
					+ "-  for any help, Please send an email to the CTM|CTS Team cts.ta@db.com\n"
					+ " - any clarification / enhancement request : Central Test Services Team (CTS)");
			Logger.error(e);
			System.exit(0);
		}

	}

	private static class DefaultTrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

}
