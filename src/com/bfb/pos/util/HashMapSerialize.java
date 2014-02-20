package com.bfb.pos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.HashMap;

public class HashMapSerialize {

	public static byte[] serialize(HashMap<String, String> hashMap) {
		try {
			ByteArrayOutputStream mem_out = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(mem_out);

			out.writeObject(hashMap);

			out.close();
			mem_out.close();

			byte[] bytes = mem_out.toByteArray();
			
			return bytes;
		} catch (IOException e) {
			return new byte[]{};
		}
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> deserialize(byte[] bytes) {
		HashMap<String, String> hashmap = new HashMap<String, String>();
		try {
			ByteArrayInputStream mem_in = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(mem_in);

			hashmap = (HashMap<String, String>) in.readObject();

			in.close();
			mem_in.close();

			return hashmap;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
			return hashmap;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return hashmap;
		} catch (IOException e) {
			e.printStackTrace();
			return hashmap;
		}
	}

}
