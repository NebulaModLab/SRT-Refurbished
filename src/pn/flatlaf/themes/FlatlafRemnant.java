package pn.flatlaf.themes;

import com.formdev.flatlaf.FlatDarkLaf;

/**
 * @author: Purple Nebula (SRT Refurbished)
 */
public class FlatlafRemnant extends FlatDarkLaf {

	public static final String NAME = "Starsector Remnant";

	public static boolean setup() {
		return setup( new FlatlafRemnant() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatlafRemnant.class );
	}

	@Override
	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return "FlatLaf base, Starsector Remnant themed";
	}

	public boolean isDark() {
		return true;
	}
}
