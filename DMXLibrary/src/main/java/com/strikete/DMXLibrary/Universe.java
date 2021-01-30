package com.strikete.DMXLibrary;

import java.io.IOException;
import java.util.Arrays;

import jd2xx.JD2XX;

public class Universe{

	/*
	 * VARIABLES
	 */
	private JD2XX dmx;
	private byte dmxStartCode = 0x00;
	private byte dmxValues[] = new byte[512];
	
	/*
	 * METHODS
	 */
	
	/**
	 * Sets the start code for the DMX Packet. In most cases it will be 0x00.
	 * @param startCode
	 */
	public void setStartCode(byte startCode) {
		dmxValues[0] = startCode;
	}
	
	/**
	 * Sets the level of an individual address.
	 * @param index (Address 1 is in slot 0)
	 * @param data (byte please)
	 */
	public void setAddressLevel(int index, byte data) {
		dmxValues[index] = data;
	}
	
	/**
	 * Sets the level of an entire 
	 * @param data (512 bytes long, address 1 is in slot 0)
	 */
	public void setDMX(byte data[]) {
		dmxValues = Arrays.copyOf(data, 512);
	}
	
	/**
	 * Closes out of the UART driver. Please use instead of just exiting the program to avoid having to re-plug the USB.
	 * @throws IOException
	 */
	public void closeDMX() throws IOException {
		dmx.close();
	}
	
	private void dmxLoop() throws IOException {
		dmx.setBaudRate(125000); //Sets baud rate for break
		dmx.write(0x00); //Break
		dmx.setBaudRate(250000); //Sets baud rate for data
		dmx.write(dmxStartCode); //Sends Start Code
		
		for(int x = 1; x < 513; x++) { //Send 512 bytes of control data
			dmx.write(dmxValues[x]); 
		}
		
		dmx.write(0xFF); //End code
	}
	
	/*
	 * CONSTRUCTOR
	 */
	public Universe(int usbPort) throws IOException {
		this.dmx = new JD2XX();
		dmx.open(usbPort);
		dmx.setBaudRate(250000);
		dmx.setDataCharacteristics(JD2XX.BITS_8, JD2XX.STOP_BITS_2, JD2XX.PARITY_NONE);
		dmx.setLatencyTimer(88);
		dmx.setBitMode(0, 0);
		dmx.setFlowControl(JD2XX.FLOW_NONE, 0, 0);
		dmx.setTimeouts(1000, 1000);
		
		Thread thread = new Thread(){
		    public void run(){
		    	while(true) {
		    		try {
						dmxLoop();
						Thread.sleep(1000/40); //This is the equivalent of 1/40, meaning it transmits full DMX at 40Hz.
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    	}
		    }
		  };
		  thread.start();
	}
	
	
	/*
	 * MAIN METHOD
	 */
	
	public static void main(String[] args) {
		System.out.println("Please run this library within another program.");

	}
}