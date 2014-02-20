package com.bfb.pos.agent.client;

public class SessionId {
	private byte[] sessionId; // max 32 bytes

	/**
	 * Constructs a session ID from a byte array (max size 32 bytes)
	 * 
	 * @param sessionId
	 */
	public SessionId(byte[] sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Returns the length of the ID, in bytes
	 * @return
	 */
	public int getLength() {
		return sessionId.length;
	}

	/**
	 * Returns the bytes in the ID. May be an empty array.
	 */
	public byte[] getId() {
		return (byte[]) sessionId;
	}

	/**
	 * Returns the ID as a string
	 * @return
	 */
	public String toString() {
		int len = sessionId.length;
		StringBuffer s = new StringBuffer(10 + 2 * len);

		s.append("{");
		for (int i = 0; i < len; i++) {
			s.append(0x0ff & sessionId[i]);
			if (i != (len - 1))
				s.append(", ");
		}
		s.append("}");
		return s.toString();
	}

	/**
	 * Returns a value which is the same for session IDs which are equal
	 */
	public int hashCode() {
		int retval = 0;

		for (int i = 0; i < sessionId.length; i++)
			retval += sessionId[i];
		return retval;
	}

	/**
	 * Returns true if the parameter is the same session ID
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof SessionId))
			return false;

		SessionId s = (SessionId) obj;
		byte[] b = s.getId();

		if (b.length != sessionId.length)
			return false;
		for (int i = 0; i < sessionId.length; i++) {
			if (b[i] != sessionId[i])
				return false;
		}
		return true;
	}
}
