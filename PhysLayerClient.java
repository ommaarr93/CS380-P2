// Omar Rodriguez
// CS 380
// Professor Nima Davarpanah

import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

public final class PhysLayerClient
{
	private static Socket soc;
	private static InputStream in;
	private static OutputStream out;

	public static void main(String args[]) throws IOException {
		try {
			soc = new Socket("codebank.xyz", 38002);
			System.out.println("Connected to server.");
		  in =  soc.getInputStream();
			out = soc.getOutputStream();
		}
		catch(UnknownHostException e) {

		}

    byte[] returnArray = new byte[32];
		int total = 0;
		double baseline = 0.0;

		for(int i = 0; i < 64; i++) {
			total = total + in.read();
		}

		baseline = total / 64.0;
		System.out.println("Baseline established form preamble: " + baseline);

		String nrzi = nrziSig(baseline);
		int index = 0;
		for(int j = 0; j < 32; j++) {
			String fourB1 = fiveToFourBit(nrzi.substring(index, index + 5));
			index += 5;

			int firstNum = Integer.parseInt(fourB1, 2);

			String fourB2 = fiveToFourBit(nrzi.substring(index, index+5));
			index += 5;

			int secondNum = Integer.parseInt(fourB2, 2);

      returnArray[j] = (byte)(secondNum ^ (firstNum << 4));

		}

		System.out.print("Received 32 bytes: ");
		System.out.println(javax.xml.bind.DatatypeConverter.printHexBinary(returnArray));
		out.write(returnArray);

		if(in.read() == 1) {
      System.out.println("Response good.");
    } else {
      System.out.println("Response bad.");
    }

    System.out.println("Disconnected from server.");
	}

  public static String fiveToFourBit(String fiveBit) {
		String fourBit = "";
		switch (fiveBit) {
      case "11110":
        fourBit = "0000";
        break;
      case "01001":
        fourBit = "0001";
        break;
      case "10100":
        fourBit = "0010";
        break;
      case "10101":
        fourBit = "0011";
        break;
      case "01010":
        fourBit = "0100";
        break;
      case "01011":
        fourBit = "0101";
        break;
      case "01110":
        fourBit = "0110";
        break;
      case "01111":
        fourBit = "0111";
        break;
      case "10010":
        fourBit = "1000";
        break;
      case "10011":
        fourBit = "1001";
        break;
      case "10110":
        fourBit = "1010";
        break;
      case "10111":
        fourBit = "1011";
        break;
      case "11010":
        fourBit = "1100";
        break;
      case "11011":
        fourBit = "1101";
        break;
      case "11100":
        fourBit = "1110";
        break;
      case "11101":
        fourBit = "1111";
        break;
    }
		return fourBit;
	}

	public static String hiLoSig(double base) throws IOException {
		String sig  = "";
		for(int i = 0; i < 320; i++) {
			int input = in.read();

			if(input > base) {
				sig = sig + "1";
			} else {
				sig = sig + "0";
			}
		}
	  return sig;
	}

	public static String nrziSig(double base) throws IOException {
		String sig = hiLoSig(base);
		String nrzi = ("" + sig.charAt(0));

		for(int i = 1; i < 320; i++) {
			if(sig.charAt(i) == sig.charAt(i - 1)) {
				nrzi = nrzi + "0";
      } else {
				nrzi = nrzi + "1";
      }
		}
		return nrzi;
	}
}
