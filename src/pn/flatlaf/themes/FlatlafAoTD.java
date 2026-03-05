package pn.flatlaf.themes;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author: Purple Nebula (SRT Revised)
 */
public class FlatlafAoTD extends FlatDarkLaf {

	public static final String NAME = "Ashes of The Domain";

	public static boolean setup() {
		return setup( new FlatlafAoTD() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatlafAoTD.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return "FlatLaf in the style of Ashes of The Domain";
	}

	public boolean isDark() {
		return true;
	}
}
