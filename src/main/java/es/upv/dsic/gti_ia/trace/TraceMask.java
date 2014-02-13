package es.upv.dsic.gti_ia.trace;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;

/**
 * Mask used to manage the trace interactions.
 * 
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class TraceMask {
	
	/** Constants for the standard char representation of true and false. */
	private static final char FALSE_CHAR = '0';
	private static final char TRUE_CHAR = '1';
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * subscriptions to tracing services related to agents life cycle.
	 */
	public static final int LIFE_CYCLE = 0;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * publication and subscription to custom tracing services.
	 */
	public static final int CUSTOM = 1;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * subscription to the sending and receiving of ACL messages.
	 */
	public static final int MSG = 2;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * subscription to the sending and receiving of ACL messages with the
	 * content included.
	 */
	public static final int MSG_DTL = 3;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * a list in the trace manager that contains all the tracing entities.
	 */
	public static final int LIST_ENTITIES = 4;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * a list in the trace manager that contains all the tracing services.
	 */
	public static final int LIST_SERVICES = 5;
	
	/**
	 * Constant with the position of the bit that represents the availability of
	 * subscription to all available tracing services.
	 */
	public static final int SUBSCRIBE_TO_ALL_SERVICES = 6;
	
	/**
	 * Constant with the position of the bit that represents the greeting of the
	 * trace manager.
	 */
	public static final int WELCOME = 10;
	
	/**
	 * Constant with the position of the bit that represents the update of the
	 * mask.
	 */
	public static final int UPDATE = 11;
	
	/**
	 * Constant with the position of the bit that represents the death of the
	 * trace manager.
	 */
	public static final int DIE = 12;
	
	/**
	 * Constant list with the permissions indexes.
	 */
	private static final List<Integer> PERMISSIONS_INDEXES;
	static {
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		tmp.add(LIFE_CYCLE);
		tmp.add(CUSTOM);
		tmp.add(MSG);
		tmp.add(MSG_DTL);
		tmp.add(LIST_ENTITIES);
		tmp.add(LIST_SERVICES);
		tmp.add(SUBSCRIBE_TO_ALL_SERVICES);
		Collections.sort(tmp);
		PERMISSIONS_INDEXES = Collections.unmodifiableList(tmp);
	}
	
	/**
	 * Constant list with the control indexes.
	 */
	private static final List<Integer> CONTROL_INDEXES;
	static {
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		tmp.add(WELCOME);
		tmp.add(UPDATE);
		tmp.add(DIE);
		Collections.sort(tmp);
		CONTROL_INDEXES = Collections.unmodifiableList(tmp);
	}
	
	/**
	 * Constant list with the valid indexes.
	 */
	private static final List<Integer> VALID_INDEXES;
	static {
		ArrayList<Integer> tmp = new ArrayList<Integer>();
		tmp.addAll(PERMISSIONS_INDEXES);
		tmp.addAll(CONTROL_INDEXES);
		Collections.sort(tmp);
		VALID_INDEXES = Collections.unmodifiableList(tmp);
	}
	
	/** Bit set used to represent the mask. */
	private BitSet mask;
	
	/** Integer that indicates the control bit active currently. */
	private Integer currentControl;
	
	/**
	 * Creates a new trace mask with all the tracing services available or
	 * unavailable, depending on the value of the given flag. In any case, the
	 * welcome bit will be set to active.
	 * 
	 * @param availability
	 *            the availability of the tracing services represented by this
	 *            new mask
	 */
	public TraceMask(boolean availability) {
		mask = new BitSet();
		if (availability) {
			// Active all the services.
			for (Integer index : PERMISSIONS_INDEXES) {
				mask.set(index, true);
			}
		}
		// Active the WELCOME bit.
		mask.set(WELCOME);
		currentControl = WELCOME;
	}
	
	/**
	 * Creates a new trace mask using the encoded one given as a parameter.
	 * 
	 * @param encodedTraceMask
	 *            the trace mask encoded.
	 * @throws ParseException
	 *             if the given encoded trace mask is not valid.
	 */
	public TraceMask(String encodedTraceMask) throws ParseException {
		char c;
		int i = 0;
		boolean controlBit = false;
		
		mask = new BitSet();
		/*
		 * If the encoded mask has a number of chars different from the number
		 * of valid indexes, throw an exception.
		 */
		if (encodedTraceMask.length() != VALID_INDEXES.size())
			throw new ParseException("Invalid codification of the trace mask: Incorrect size.",-1);
		else {
			/*
			 * Cover the positions of the encoded mask that represent the
			 * permission indexes and set the required ones to active.
			 * 
			 * If an invalid char is detected, throw an exception.
			 */
			while (i < PERMISSIONS_INDEXES.size()) {
				c = encodedTraceMask.charAt(i);
				switch (c) {
					case TRUE_CHAR:
						mask.set(PERMISSIONS_INDEXES.get(i));
						break;
					case FALSE_CHAR:
						break;
					default:
						throw new ParseException("Invalid codification of the trace mask: Invalid character.",i);
				}
				++i;
			}
			
			/*
			 * Cover the positions of the encoded mask that represent the valid
			 * bits that are not permissions bit, i.e., the control bits, and
			 * set one of them.
			 * 
			 * If a number different from 1 control bits are set, or a invalid
			 * char is detected, throw an exception.
			 */
			while (i < VALID_INDEXES.size()) {
				c = encodedTraceMask.charAt(i);
				switch (c) {
					case TRUE_CHAR:
						if (controlBit == true) {
							/*
							 * A control bit has already been set. Invalid
							 * encoding!
							 */
							throw new ParseException("Invalid codification of the trace mask: Several active control bits.",-1);
						}
						mask.set(VALID_INDEXES.get(i));
						currentControl = VALID_INDEXES.get(i);
						controlBit = true;
						break;
					case FALSE_CHAR:
						break;
					default:
						throw new ParseException("Invalid codification of the trace mask: Invalid character.",i);
				}
				++i;
			}
			if (controlBit == false)
				throw new ParseException("Invalid codification of the trace mask: No active control bit.",-1);
		}
	}
	
	/**
	 * Returns the value of the bit with the specified index. The value is true
	 * if the bit with the index bitIndex is currently set in this TraceMask;
	 * otherwise, the result is false.
	 * 
	 * @param bitIndex
	 *            the bit index
	 * @return the value of the bit with the specified index
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is negative or does not correspond to
	 *             a valid bit.
	 */
	public boolean get(int bitIndex) throws IndexOutOfBoundsException {
		if (VALID_INDEXES.contains(bitIndex))
			return mask.get(bitIndex);
		else
			throw new IndexOutOfBoundsException();
	}
	
	/**
	 * Set to true the value of the bit with the specified index.
	 * 
	 * @param bitIndex
	 *            the bit index
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is negative or does not correspond to
	 *             a valid bit.
	 */
	protected void set(int bitIndex) throws IndexOutOfBoundsException {
		try {
			this.set(bitIndex, true);
		} catch (IndexOutOfBoundsException e) {
			throw e;
		} catch (Exception e) {
			/*
			 * This catch is supposed to be never executed, since an Exception
			 * is only thrown if the set value is false.F
			 */
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the bit of the specified index with the given value.
	 * 
	 * @param bitIndex
	 *            the bit index
	 * @param value
	 *            the value that will be given to the bit
	 * @throws IndexOutOfBoundsException
	 *             if the specified index is negative or does not correspond to
	 *             a valid bit.
	 * @throws Exception
	 *             if the active control bit is going to be set off.
	 */
	protected void set(int bitIndex, boolean value) throws IndexOutOfBoundsException, Exception {
		if (VALID_INDEXES.contains(bitIndex)) {
			if (CONTROL_INDEXES.contains(bitIndex)) {
				if (value == true) {
					/*
					 * Only one control bit can be active at a time, so, if the
					 * bit that is going to be set is a control bit, deactivate
					 * the previous control bit and, then, set the new one.
					 */
					mask.set(currentControl, false);
					currentControl = bitIndex;
				} else if (bitIndex == currentControl) {
					/*
					 * The only active control bit is going to be set off, so
					 * throw an exception.
					 */
					throw new Exception("One of the control bits must be active.");
				}
			}
			mask.set(bitIndex, value);
		} else
			throw new IndexOutOfBoundsException();
	}
	
	/**
	 * Returns the number of valid indexes of this trace mask.
	 * 
	 * @return the number of valid indexes.
	 */
	public int length() {
		return VALID_INDEXES.size();
	}
	
	/**
	 * Returns true if the trace is available, and false, otherwise.
	 * 
	 * @return true if the trace is available; false, otherwise
	 */
	public boolean isTraceAvailable() {
		// If the trace manager is dead, the trace is not available.
		if (mask.get(DIE) == true)
			return false;
		else {
			for (Integer index : PERMISSIONS_INDEXES) {
				if (mask.get(index) == true)
					return true;
			}
			return false;
		}
	}
	
	/**
	 * Returns the string representation of this trace mask, in a way such that
	 * each valid index is represented with a 0 if its value is false or a 1 if
	 * its value is true.
	 * 
	 * For example, the a trace mask with 5 valid bits and the bits 1 and 2 set
	 * to true is represented as "01100".
	 * 
	 * @return a string representation of this trace mask.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Integer index : VALID_INDEXES) {
			sb.append(mask.get(index) ? TRUE_CHAR : FALSE_CHAR);
		}
		return sb.toString();
	}
	
	/**
	 * Returns the true if the given object is an instance of TraceMask with the
	 * same tracing services allowed than this, and false, otherwise.
	 * 
	 * @param obj
	 *            the object to compare with this
	 * @return true if the object is a trace mask with the same permissions and
	 *         false, otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TraceMask) {
			/*
			 * If the object is an instance of TraceMask, and all its
			 * permissions bits have the same value than the permissions bits of
			 * this, they are equal.
			 */
			TraceMask other = (TraceMask) obj;
			for (Integer index : PERMISSIONS_INDEXES) {
				if (mask.get(index) != other.get(index)) {
					return false;
				}
			}
			return true;
		} else {
			/*
			 * If the object is not an instance of TraceMask, they are not
			 * equal.
			 */
			return false;
		}
	}
	
	/**
	 * Returns a copy trace mask with the same features than this trace mask.
	 * 
	 * @return a different object which is an exact copy of this trace mask.
	 */
	@Override
	public TraceMask clone() {
		TraceMask newMask = null;
		try {
			newMask = new TraceMask(this.toString());
		} catch (Exception e) {
			/*
			 * This exception is never supposed to be thrown. If that happens,
			 * something has gone really wrong.
			 */
			e.printStackTrace();
		}
		return newMask;
	}
	
//	public static void main(String args[]) {
//		TraceMask tm = new TraceMask(false);
//		tm.set(TraceMask.LIFE_CYCLE);
//		tm.set(TraceMask.CUSTOM);
//		tm.set(TraceMask.MSG);
//		tm.set(TraceMask.UPDATE);
//		String s = tm.toString();
//		System.out.println(s);
//		System.out.println(tm.size());
//		System.out.println(tm.length());
//		TraceMask tm2 = null;
//		try {
//			tm2 = new TraceMask(s);
//		} catch (Exception e) {
//		}
//		System.out.println(tm2);
//		System.out.println(tm2.size());
//		System.out.println(tm2.length());
//	}
}