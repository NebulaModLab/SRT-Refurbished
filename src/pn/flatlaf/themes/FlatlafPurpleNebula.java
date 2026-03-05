package pn.flatlaf.themes;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author: Purple Nebula (SRT Revised)
 */
public class FlatlafPurpleNebula extends FlatDarkLaf {

	public static final String NAME = "Flatlaf Purple";

	public static boolean setup() {
		return setup( new FlatlafPurpleNebula() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatlafPurpleNebula.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return "FlatLaf dark with purple";
	}

	public boolean isDark() {
		return true;
	}
}
