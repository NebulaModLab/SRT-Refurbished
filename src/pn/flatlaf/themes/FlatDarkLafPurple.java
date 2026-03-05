package pn.flatlaf.themes;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author Purple Nebula (SRT Revised)
 */
public class FlatDarkLafPurple extends FlatDarkLaf {

	public static final String NAME = "Flatlaf Dark Purple";

	public static boolean setup() {
		return setup( new FlatDarkLafPurple() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatDarkLafPurple.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return "FlatLaf dark with purple accents";
	}

	public boolean isDark() {
		return true;
	}
}
