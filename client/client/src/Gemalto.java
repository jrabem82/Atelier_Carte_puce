
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
	String AES1 = "master2CerAdiShamir";
	String AES2 = "master2CerAlanTuring";
	private boolean LOG = true;

	public boolean initGemalto() {
		/*
		 * initialier le lecteur de carte
		 */
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

	public void waitCardIn() {
		/*
		 * attente d'insertion d'un carte
		 */
		try {
			System.out.println("en attente de carte");
			terminal.waitForCardPresent(0);
			carte = terminal.connect("T=0");
			channel = carte.getBasicChannel();
			System.out.println("carte présentee");
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public void waitCardOut() {
		try {
			/*
			 * attente de retrait d'une carte
			 */
			System.out.println("retirer la carte");
			terminal.waitForCardAbsent(0);
			System.out.println("carte retiree");
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public boolean setPin(int numPin, byte[] oldPin, byte[] newPin) {
		/*
		 * modifier le code pin numero numePin valeur de retour : vrai si l'operation a
		 * reussi, faux sinon
		 */
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
				showLog(LOG, "setPin : code pin " + numPin + " ok");
				return true;
			} else
				showLog(LOG, "setPin : code pin " + numPin + " pb");
		}
		return false;
	}

	public boolean emulateUserMode() {
		/*
		 * passer la carte en mode simulation utilisateur valeur de retour : vrai si
		 * l'operation a reussi, faux sinon
		 */
		if (checkPin(0, PIN0)) {
			ResponseAPDU r = update(INS_VERIFY, (byte) 0x3A, (byte) 0x04, PIN0);
			if (r.getSW() == responseOk) {
				showLog(LOG, "emulateUserMode : ok");
				return true;
			} else
				showLog(LOG, "emulateUserMode : pb");
		}
		return false;
	}

	public boolean checkPin(int numPin, byte[] pin) {
		/*
		 * tester si le pin numero numPin est correct valeur de retour : vrai si
		 * l'operation a reussi, faux sinon
		 */
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
			showLog(LOG, "checkPin : code pin " + numPin + " ok");
			return true;
		} else {
			showLog(LOG, "checkPin : code pin " + numPin + " pb");
			return false;
		}
	}

	public boolean initCard(String id, byte[] pin1, String aes) {
		/*
		 * dans cette fonction on va mettre l'id dans user2 modifier le code pin 1
		 * mettre la clé AES dans user 1 mettre aac pour proteger user area 1 passer la
		 * carte en mode emulateur user valeur de retour : vrai si l'operation a reussi,
		 * faux sinon
		 */
		ResponseAPDU r;
		// enter PIN 0
		if (checkPin(0, PIN0)) {
			// user area 2 : len(id) + id
			// mettre la longueur de id sur 4 octect
			byte[] len_id = intToByte4(id.length());
			r = update(INS_UPDATE, (byte) 0x28, (byte) 0x04, len_id);
			if (r.getSW() == responseOk) {
				showLog(LOG, "initCard : put len_id ok");
				// mettre id dans user area 2
				byte[] id_byte = format_octect(stringToByte(id));
				r = update(INS_UPDATE, (byte) 0x29, (byte) id_byte.length, id_byte);
				if (r.getSW() == responseOk) {
					showLog(LOG, "initCard : put id ok");
					// modifier pin1
					if (setPin(1, PIN1, pin1)) {
						showLog(LOG, "initCard : set pin1 ok");
						// user area 1 : len(aes) + aes
						// mettre la longueur de aes sur 4 octect
						byte[] len_aes = intToByte4(aes.length());
						r = update(INS_UPDATE, (byte) 0x10, (byte) 0x04, len_aes);
						if (r.getSW() == responseOk) {
							showLog(LOG, "initCard : put len_aes ok");
							// mettre aes dans user area 1
							byte[] aes_byte = format_octect(stringToByte(aes));
							r = update(INS_UPDATE, (byte) 0x11, (byte) aes_byte.length, aes_byte);
							if (r.getSW() == responseOk) {
								showLog(LOG, "initCard : put aes ok");
								// modifier acces condition area
								/*
								 * libre area user 2 et proteger area user 1 : 0xA0 : 10100000
								 */
								byte[] aac = { (byte) 0xA0, (byte) 0xA0, (byte) 0xA0, (byte) 0xA0 };
								r = update(INS_UPDATE, (byte) 0x05, (byte) 0x04, aac);
								if (r.getSW() == responseOk) {
									showLog(LOG, "initCard : aac for user area 1 ok");
								} else
									showLog(LOG, "initCard : aac for user area 1 pb");
								// emuler mode user
								//return emulateUserMode();
								return true;
							} else
								showLog(LOG, "initCard : put aes pb");
						} else
							showLog(LOG, "initCard : put len_aes pb");
					} else
						showLog(LOG, "initCard : set pin1 pb");
				} else
					showLog(LOG, "initCard : put id pb");
			} else
				showLog(LOG, "initCard : put len_id pb");
		}
		return false;
	}

	public String readIdFromCard() {
		/*
		 * recuperer l'id de l'utilisateur dans area user 2 valeur de retour : id
		 * utilisateur si l'operation a reussi, null sinon
		 */
		ResponseAPDU r;
		// je recupere la longueur de id
		r = read((byte) 0x28, (byte) 0x04);
		if (r.getSW() == responseOk) {
			showLog(LOG, "readIdFromCard : lecture len_id ok");
			int len_id = byte4ToInt(r.getBytes());
			int len_octect = len_id + (4 - len_id % 4);
			// je recupere l'id
			r = read((byte) 0x29, (byte) len_octect);
			if (r.getSW() == responseOk) {
				showLog(LOG, "readIdFromCard : lecture id ok : "+byteToString(r.getBytes(), len_id));
				return byteToString(r.getBytes(), len_id);
			} else
				showLog(LOG, "readIdFromCard : lecture id pb");
		} else
			showLog(LOG, "readIdFromCard : lecture len_id pb");
		return null;
	}

	public String readAesFromCard(byte[] pin1) {
		/*
		 * recuperer l'aes de l'utilisateur dans area user 1 valeur de retour : ase
		 * utilisateur si l'operation a reussi, null sinon
		 */
		ResponseAPDU r;
		if (checkPin(1, pin1)) {
			// je recupere la longueur de aes
			r = read((byte) 0x10, (byte) 0x04);
			if (r.getSW() == responseOk) {
				showLog(LOG, "readAesFromCard : lecture len_aes ok");
				int len_aes = byte4ToInt(r.getBytes());
				int len_octect = len_aes + (4 - len_aes % 4);
				// je recupere aes
				r = read((byte) 0x11, (byte) len_octect);
				if (r.getSW() == responseOk) {
					showLog(LOG, "readAesFromCard : lecture aes ok");
					return byteToString(r.getBytes(), len_aes);
				} else
					showLog(LOG, "readaesFromCard : lecture aes pb");
			} else
				showLog(LOG, "readaesFromCard : lecture len_aes pb");
		}
		return null;
	}

	public byte[] format_octect(byte[] data) {// pour avoir un tableau de longueur multiple de 4
		/*
		 * formatter un tableau qlq pour avoir nu longueur multiple de 0x04, remplir le
		 * reste par du blanc valeur de retour : , tableau de byte
		 */
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

	public void disconnect(boolean reset) {
		try {
			carte.disconnect(reset);
		} catch (CardException e) {
			e.printStackTrace();
		}
	}

	public ResponseAPDU read(byte p2, byte le) {
		CommandAPDU commande = new CommandAPDU(0x00, 0xBE, 0x00, p2, le);
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
		CommandAPDU commande = new CommandAPDU(0x00, ins, 0x00, p2, data, le);
		ResponseAPDU r;
		try {
			r = channel.transmit(commande);
		} catch (CardException e) {
			e.printStackTrace();
			r = null;
		}
		return r;
	}

	public void showLog(boolean b, String s) {
		if (b)
			System.out.println(s);
	}
}