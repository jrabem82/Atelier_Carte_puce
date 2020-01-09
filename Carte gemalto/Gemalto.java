package card;

import java.util.List;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class Gemalto {

	private CardTerminal terminal;
	private Card carte;
	private CardChannel channel;
	private int responseOk = 0x9000;
	private byte INS_UPDATE = (byte) 0xDE;
	private byte INS_VERIFY = (byte) 0x20;
	byte[] PIN0 = { (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA };
	byte[] PIN1 = { (byte) 0x11, (byte) 0x11, (byte) 0x11, (byte) 0x11 };
	byte[] PIN2 = { (byte) 0x22, (byte) 0x22, (byte) 0x22, (byte) 0x22 };

	public boolean initGemalto() {
		try {
			if (terminal == null) {
				List<CardTerminal> terminauxDispos = TerminalFactory.getDefault().terminals().list();
				terminal = terminauxDispos.get(0);
			}
			return true;
		} catch (CardException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void waitCard() {
		try {
			System.out.println("en attente de carte");
			terminal.waitForCardPresent(0);
			carte = terminal.connect("T=0");
			channel = carte.getBasicChannel();
			System.out.println("carte présente");
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public boolean setPin(int numPin, byte[] oldPin, byte[] newPin) {
		if (checkPin(numPin, oldPin)) {
			byte p2;
			if (numPin == 0)
				p2 = 0x06;
			else if (numPin == 1)
				p2 = 0x38;
			else
				p2 = 0x3A;
			ResponseAPDU r = update(INS_UPDATE, p2, (byte) 0x04, newPin);
			if (r.getSW() == responseOk) {
				System.out.println("setPin : code pin " + numPin + " ok");
				return true;
			} else
				System.out.println("setPin : code pin " + numPin + " pb");
		}
		return false;
	}

	public boolean emulateUserMode(byte[] pin0) {
		if (checkPin(0, pin0)) {
			ResponseAPDU r = update(INS_VERIFY, (byte) 0x3A, (byte) 0x04, pin0);
			if (r.getSW() == responseOk) {
				System.out.println("emulateUserMode : ok");
				return true;
			} else
				System.out.println("emulateUserMode : pb");
		}
		return false;
	}

	public boolean checkPin(int numPin, byte[] pin) {
		ResponseAPDU r;
		byte p2;
		if (numPin == 0)
			p2 = 0x07;
		else if (numPin == 1)
			p2 = 0x39;
		else
			p2 = 0x3B;
		r = update(INS_VERIFY, p2, (byte) 0x04, pin);
		if (r.getSW() == responseOk) {
			System.out.println("checkPin : code pin " + numPin + " ok");
			return true;
		} else {
			System.out.println("checkPin : code pin " + numPin + " pb");
			return false;
		}
	}

	byte[] pin1 = { (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

	public boolean initCard(String id, byte[] pin1) {
		ResponseAPDU r;
		// enter PIN 0
		if (checkPin(0, PIN0)) {
			// user area 1 : len + data
			// mettre la longueur de id sur 4 octect
			byte[] len_id = intToByte4(id.length());
			r = update(INS_UPDATE, (byte) 0x10, (byte) 0x04, len_id);
			if (r.getSW() == responseOk) {
				System.out.println("initCard : put len_id id ok");
				// mettre id
				byte[] id_byte = format_octect(stringToByte(id));
				r = update(INS_UPDATE, (byte) 0x11, (byte) id_byte.length, id_byte);
				if (r.getSW() == responseOk) {
					System.out.println("initCard : put id ok");
					// modifier pin1
					//if (setPin(1, PIN1, PIN1)) {
						//System.out.println("initCard : set pin1 ok");
						// modifier acces condition area
						//proteger CSC1 et area user 1 : 0xA0
						
						// mettre en mode user

						return true;
					//} else
						//System.out.println("initCard : set pin1 pb");
				} else
					System.out.println("initCard : put id pb");
			} else
				System.out.println("initCard : put len_id pb");
		}
		return false;
	}

	public String readIdFromCard(byte[] pin) {
		ResponseAPDU r;
		// enter PIN 0
//////////////////////changer 1 par 0
		if (checkPin(0, PIN0)) {
			// je recupere la longueur de id
			r = read((byte) 0x10, (byte) 0x04);
			if (r.getSW() == responseOk) {
				System.out.println("readIdFromCard : lecture len_id ok");
				int len_id = byte4ToInt(r.getBytes());
				int len_octect = len_id + (4 - len_id % 4);
				// je recupere l'id
				r = read((byte) 0x11, (byte) len_octect);
				if (r.getSW() == responseOk) {
					System.out.println("readIdFromCard : lecture id ok");
					return byteToString(r.getBytes(), len_id);
				} else
					System.out.println("readIdFromCard : lecture id pb");
			} else
				System.out.println("readIdFromCard : lecture len_id pb");
		}
		return null;
	}

	public byte[] format_octect(byte[] data) {// pour avoir un tableau de longueur multiple de 4
		int f = data.length + (4 - data.length % 4);
		byte[] data2 = new byte[f];
		int i;
		for (i = 0; i < data.length; i++)
			data2[i] = data[i];
		for (int j = i; j < f; j++) {
			data2[j] = 0x00;
		}
		return data2;
	}

	public byte[] stringToByte(String s) {
		return s.getBytes();
	}

	public String byteToString(byte[] data) {
		return new String(data);
	}

	public String byteToString(byte[] data, int len) {
		byte[] data2 = new byte[len];
		for (int i = 0; i < len; i++)
			data2[i] = data[i];
		return new String(data2);
	}

	public byte[] intToByte4(int i) {
		byte[] data = new byte[4];
		data[0] = (byte) (i >> 24);
		data[1] = (byte) (i >> 16);
		data[2] = (byte) (i >> 8);
		data[3] = (byte) (i);
		return data;
	}

	public int byte4ToInt(byte[] data) {
		int a = 0;
		a = (a | (data[0] & 0XFF)) << 8;
		a = (a | (data[1] & 0XFF)) << 8;
		a = (a | (data[2] & 0XFF)) << 8;
		a = (a | (data[3] & 0XFF));
		return a;
	}

	public void disconnect() {
		try {
			carte.disconnect(false);
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public ResponseAPDU read(byte p2, byte le) {
		CommandAPDU commande = new CommandAPDU(0x0, 0xBE, 0x00, p2, le);
		ResponseAPDU r;
		try {
			r = channel.transmit(commande);
		} catch (CardException e) {
			e.printStackTrace();
			r = null;
		}
		return r;
	}

	public ResponseAPDU update(byte ins, byte p2, byte le, byte[] data) {
		CommandAPDU commande = new CommandAPDU(0x80, ins, 0x00, p2, data, le);
		ResponseAPDU r;
		try {
			r = channel.transmit(commande);
		} catch (CardException e) {
			e.printStackTrace();
			r = null;
		}
		return r;
	}
}