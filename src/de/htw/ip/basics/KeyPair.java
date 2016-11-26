package de.htw.ip.basics;

import java.util.Objects;

public class KeyPair {
	public Penalty key1;
	public int key2;
	
	public KeyPair(Penalty key1, int key2){
		this.key1 = key1;
		this.key2 = key2;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key1, key2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (!(obj instanceof KeyPair)) return false;
        KeyPair kp = (KeyPair) obj;
		return kp.key1.equals(key1) && kp.key2 == key2; 
	}
	
	
}
