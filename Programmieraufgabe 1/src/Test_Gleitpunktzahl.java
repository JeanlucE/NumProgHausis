import java.util.Arrays;

public class Test_Gleitpunktzahl {
/**
 * Testklasse, ob alles funktioniert!
 */
	
	
	public static void main(String[] argv) {
		test_Gleitpunktzahl();
	}

	public static void test_Gleitpunktzahl() {

		/**********************************/
		/* Test der Klasse Gleitpunktzahl */
		/**********************************/

		System.out.println("-----------------------------------------");
		System.out.println("Test der Klasse Gleitpunktzahl");

		/*
		 * Verglichen werden die BitFelder fuer Mantisse und Exponent und das
		 * Vorzeichen
		 */
		Gleitpunktzahl.setSizeMantisse(23);
		Gleitpunktzahl.setSizeExponent(8);

		Gleitpunktzahl x;
		Gleitpunktzahl y;
		Gleitpunktzahl gleitref = new Gleitpunktzahl();
		Gleitpunktzahl gleiterg;

		/* Test von setDouble */
		System.out.println("Test von setDouble");
		try {
			// Test: setDouble
			x = new Gleitpunktzahl(0.5);

			// Referenzwerte setzen
			gleitref = new Gleitpunktzahl(0.5);

			// Test, ob Ergebnis korrekt
			if (x.compareAbsTo(gleitref) != 0
					|| x.vorzeichen != gleitref.vorzeichen) {
				printErg("" + x.toDouble(), "" + gleitref.toDouble());
			} else {
				System.out.println("    Richtiges Ergebnis\n");
			}

			
			//teste normalisiere
			System.out.println("Eigener Test für normalisiere():");
			y = new Gleitpunktzahl(590000);
            System.out.println(y.toDouble());

            //teste denormalisiere
            System.out.println("Eigener Test für denormalisiere():");
            Gleitpunktzahl z = new Gleitpunktzahl(18);
            System.out.println(Integer.toBinaryString(y.mantisse) + ", " + (y.exponent-127));
            Gleitpunktzahl.denormalisiere(y, z);

            System.out.println(Integer.toBinaryString(y.mantisse) + ", " + (y.exponent-127));
            System.out.println(Integer.toBinaryString(z.mantisse) + ", " + (z.exponent-127));
		} catch (Exception e) {
			System.out.print("Exception bei der Auswertung des Ergebnis!!\n");
		}

		/* Addition */
		System.out.println("Test der Addition mit Gleitpunktzahl");
		
		double[][] addvals = {
				//{1.0, 3.25}
			//*
			{3.25, 2.75}, {1.0, 3.25}, {Double.NaN, 5.25}, {2.0, 2.0}, {3.25, -2.75}, {-1.0, -3.25},
			{Double.POSITIVE_INFINITY, 1024}, {Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}
			//*/
		};
		
		for (int i = 0; i < addvals.length; i++) {
			try {
				// Test: Addition
				System.out.println("Test: Addition  x + y: ("+addvals[i][0]+") + ("+addvals[i][1]+")");
				x = new Gleitpunktzahl(addvals[i][0]);
				y = new Gleitpunktzahl(addvals[i][1]);

				// Referenzwerte setzen
				gleitref = new Gleitpunktzahl(addvals[i][0] + addvals[i][1]);

				// Berechnung
				gleiterg = x.add(y);

				// Test, ob Ergebnis korrekt
				if (gleiterg.compareAbsTo(gleitref) != 0
						|| gleiterg.vorzeichen != gleitref.vorzeichen) {
					printAdd(x.toString(), y.toString());
					printErg(gleiterg.toString(), gleitref.toString());
				} else {
					System.out.println("    Richtiges Ergebnis\n");
				}

				/*************
				 * Eigene Tests einfuegen
				 */
			} catch (Exception e) {
				System.out
						.print("Exception bei der Auswertung des Ergebnis!!\n");
			}
		}
		double[][] subvals = {
				//{1.0, 3.25}
			//*
			{3.25, 2.75}, {1.0, 3.25}, {Double.NaN, 5.25}, {2.0, 2.0}, {3.25, -2.75}, {-1.0, -3.25},
			{Double.POSITIVE_INFINITY, 1024}, {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY}
			//*/
		};
		
		for (int i = 0; i < subvals.length; i++) {
			/* Subtraktion */
			try {
				System.out.println("Test der Subtraktion mit Gleitpunktzahl");

				// Test: Addition
				x = new Gleitpunktzahl(subvals[i][0]);
				y = new Gleitpunktzahl(subvals[i][1]);
				System.out.println("Test: Subtraktion  x - y: ("+subvals[i][0]+") - ("+subvals[i][1]+")");

				// Referenzwerte setzen
				gleitref = new Gleitpunktzahl((subvals[i][0] - subvals[i][1]));

				// Berechnung
				gleiterg = x.sub(y);

				// Test, ob Ergebnis korrekt
				if (gleiterg.compareAbsTo(gleitref) != 0
						|| gleiterg.vorzeichen != gleitref.vorzeichen) {
					printSub(x.toString(), y.toString());
					printErg(gleiterg.toString(), gleitref.toString());
				} else {
					System.out.println("    Richtiges Ergebnis\n");
				}

				/*************
				 * Eigene Tests einfuegen
				 */

				System.out.println("\n\nEIGENE TESTS EINFÜGEN!!!!!!!\n\n");

			} catch (Exception e) {
				System.out
						.print("Exception bei der Auswertung des Ergebnis!!\n");
			}
		}
		/* Sonderfaelle */
		System.out.println("Test der Sonderfaelle mit Gleitpunktzahl");

		try {
			// Test: Sonderfaelle
			// 0 - inf
			System.out.println("Test: Sonderfaelle");
			x = new Gleitpunktzahl(0.0);
			y = new Gleitpunktzahl(1.0 / 0.0);

			// Referenzwerte setzen
			gleitref.setInfinite(true);

			// Berechnung mit der Methode des Studenten durchfuehren
			gleiterg = x.sub(y);

			// Test, ob Ergebnis korrekt
			if (gleiterg.compareAbsTo(gleitref) != 0
					|| gleiterg.vorzeichen != gleitref.vorzeichen) {
				printSub(x.toString(), y.toString());
				printErg(gleiterg.toString(), gleitref.toString());
			} else {
				System.out.println("    Richtiges Ergebnis\n");
			}
			


		} catch (Exception e) {
			System.out
					.print("Exception bei der Auswertung des Ergebnis in der Klasse Gleitpunktzahl!!\n");
		}

	}


	private static void printAdd(String x, String y) {
		System.out.println("    Fehler!\n      Es wurde gerechnet:            "
				+ x + " + " + y);
	}

	private static void printSub(String x, String y) {
		System.out.println("    Fehler!\n      Es wurde gerechnet:            "
				+ x + " - " + y + " = \n");
	}

	private static void printErg(String erg, String checkref) {
		System.out.println("      Ihr Ergebnis lautet:           " + erg
				+ "\n      Das Korrekte Ergebnis lautet:  " + checkref + "\n");
	}
}