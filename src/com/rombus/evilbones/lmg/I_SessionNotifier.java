package com.rombus.evilbones.lmg;

/**
 * @author rombus
 *
 * 23/07/2016 19:53:38
 */
public interface I_SessionNotifier {

	// Por ahora solo quiero diferenciar el USTA de los demás, por eso está este solo
	public enum Commands{
		USTA
	}
	
	public void writeOutput(String msg);
	public void setSentMsg(Commands cmd);
}
