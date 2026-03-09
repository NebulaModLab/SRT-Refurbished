package pn.flatlaf.themes;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author: Purple Nebula (SRT Refurbished)
 */
public class FlatDarkLafRed extends FlatDarkLaf {

	public static final String NAME = "Flatlaf Dark Red";

	public static boolean setup() {
		return setup( new FlatDarkLafRed() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatDarkLafRed.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return "FlatLaf dark with red accents";
	}

	public boolean isDark() {
		return true;
	}
}
