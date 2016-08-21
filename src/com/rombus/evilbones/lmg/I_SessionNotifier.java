package com.rombus.evilbones.lmg;

/**
 * @author rombus
 *
 * 23/07/2016 19:53:38
 */
public interface I_SessionNotifier {

	// Por ahora solo quiero diferenciar el USTA y LIST de los demás, por eso están esos solos
	public enum Commands{
		USTA, LIST, NO_CMD
	}
	
	public void writeOutput(String msg);
	public void setSentMsg(Commands cmd);
}
