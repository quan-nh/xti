package rfc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

/**
 * This class implements a client OSI over TCP/IP.
 * 
 */
public class ClientOSI {

	private int maxTPDUSizeParam = 16;
	private SocketFactory socketFactory = null;
	private int messageTimeout = 0;
	private int messageFragmentTimeout = 60000;
	private int connectTimeout = 0;

	private byte[] tSelRemote = null;
	private byte[] tSelLocal = null;

	/**
	 * Use this constructor to create a client that will start connections
	 * to remote.
	 */
	public ClientOSI() {
		socketFactory = SocketFactory.getDefault();
	}

	/**
	 * Use this constructor to create a client that will start connections
	 * to remote. You could pass an SSLSocketFactory to enable SSL.
	 */
	public ClientOSI(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	/**
	 * Set the connection timeout for waiting for the first byte of a new
	 * message. Default is 0 (unlimited)
	 * 
	 * @param messageTimeout
	 *            in milliseconds
	 */
	public void setMessageTimeout(int messageTimeout) {
		this.messageTimeout = messageTimeout;
	}

	/**
	 * Set the connection timeout for receiving data once the beginning of a
	 * message has been received. Default is 60000 (60seconds)
	 * 
	 * @param messageFragmentTimeout
	 *            in milliseconds
	 */
	public void setMessageFragmentTimeout(int messageFragmentTimeout) {
		this.messageFragmentTimeout = messageFragmentTimeout;
	}

	/**
	 * Set the maxTPDUSize. The default maxTPDUSize is 65531 (see RFC 1006).
	 * Only use this function if you want to change this.
	 * 
	 * @param maxTPDUSizeParam
	 *            The maximum length is equal to 2^(maxTPDUSizeParam) octets.
	 *            Note that the actual TSDU size that can be transfered is equal
	 *            to TPDUSize-3. Default is 65531 octets (see RFC 1006), 7 <=
	 *            maxTPDUSizeParam <= 16, needs to be set before listening or
	 *            connecting
	 */
	public void setMaxTPDUSizeParam(int maxTPDUSizeParam) {
		if (maxTPDUSizeParam < 7 || maxTPDUSizeParam > 16) {
			throw new IllegalArgumentException("maxTPDUSizeParam is out of bound");
		}
		this.maxTPDUSizeParam = maxTPDUSizeParam;
	}

	/**
	 * Get the maxTPDUSize Parameter to be used by this
	 * 
	 * @return the maximum TPDU size
	 */
	public int getMaxTPDUSizeParam() {
		return maxTPDUSizeParam;
	}

	/**
	 * Get the maximum TPDUSize. This is equal to 2^(maxTPDUSizeParam)
	 * 
	 * @param maxTPDUSizeParam
	 * @return the maximum TPDU size
	 * @throws IOException
	 */
	public static int getMaxTPDUSize(int maxTPDUSizeParam) throws IOException {
		if (maxTPDUSizeParam < 7 || maxTPDUSizeParam > 16) {
			throw new IllegalArgumentException("maxTPDUSizeParam is out of bound");
		}
		if (maxTPDUSizeParam == 16) {
			return 65531;
		} else {
			return (int) Math.pow(2, maxTPDUSizeParam);
		}
	}

	/**
	 * Connect to a remote that is listening at the destination address.
	 * 
	 * @param address
	 *            remote InetAddress
	 * @param port
	 *            remote port
	 * @return the Connection Object
	 * @throws IOException
	 *             is thrown if connection was unsuccessful.
	 */
	public TConnection connectTo(InetAddress address, int port) throws IOException {
		Socket socket = socketFactory.createSocket();
		socket.connect(new InetSocketAddress(address, port), connectTimeout);
		TConnection tConnection = new TConnection(socket, maxTPDUSizeParam, messageTimeout, messageFragmentTimeout);
		tConnection.tSelRemote = tSelRemote;
		tConnection.tSelLocal = tSelLocal;
		tConnection.startConnection();

		return tConnection;
	}

	/**
	 * Connect to a remote that is listening at the destination address.
	 * 
	 * @param address
	 *            remote Address name
	 * @param port
	 *            remote port
	 * @return the Connection Object
	 * @throws IOException
	 *             is thrown if connection was unsuccessful.
	 */
	public TConnection connectTo(String addressName, int port) throws IOException {
		InetAddress address = InetAddress.getByName(addressName);

		return connectTo(address, port);
	}

	public void setConnectTimeout(int timeout) {
		this.connectTimeout = timeout;
	}

	public void setSocketFactory(SocketFactory socketFactory) {
		this.socketFactory = socketFactory;
	}

	public byte[] gettSelRemote() {
		return tSelRemote;
	}

	public void settSelRemote(byte[] tSelRemote) {
		this.tSelRemote = tSelRemote;
	}

	public byte[] gettSelLocal() {
		return tSelLocal;
	}

	public void settSelLocal(byte[] tSelLocal) {
		this.tSelLocal = tSelLocal;
	}
	
}
